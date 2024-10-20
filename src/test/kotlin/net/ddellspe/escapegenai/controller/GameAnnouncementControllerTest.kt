package net.ddellspe.escapegenai.controller

import io.mockk.*
import java.util.*
import net.ddellspe.escapegenai.model.GameAnnouncement
import net.ddellspe.escapegenai.service.GameAnnouncementService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GameAnnouncementControllerTest {
  private var gameAnnouncementService: GameAnnouncementService = mockk()
  private var gameAnnouncementController = GameAnnouncementController(gameAnnouncementService)
  private var gameAnnouncement: GameAnnouncement = mockk()
  private var capturedGameAnnouncement = slot<GameAnnouncement>()
  private var id = UUID.randomUUID()

  @Test
  fun coverageTest() {
    gameAnnouncementController.gameAnnouncementService = gameAnnouncementService
  }

  @Test
  fun whenGetAnnouncements_hasInvoices_returnInvoices() {
    every { gameAnnouncementService.getAllGameAnnouncements() } returns listOf(gameAnnouncement)

    val result = gameAnnouncementController.getAnnouncements()

    verify(exactly = 1) { gameAnnouncementService.getAllGameAnnouncements() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(listOf(gameAnnouncement), result.body)
  }

  @Test
  fun whenGetAnnouncements_hasNoInvoices_returnNoInvoices() {
    every { gameAnnouncementService.getAllGameAnnouncements() } returns emptyList()

    val result = gameAnnouncementController.getAnnouncements()

    verify(exactly = 1) { gameAnnouncementService.getAllGameAnnouncements() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(emptyList<GameAnnouncement>(), result.body)
  }

  @Test
  fun whenCreateAnnouncement_hasNewAnnouncement_returnCreatedAnnouncement() {
    every {
      gameAnnouncementService.createGameAnnouncement(capture(capturedGameAnnouncement))
    } returns gameAnnouncement

    val result =
      gameAnnouncementController.createAnnouncement(
        GameAnnouncement(message = "message", link = "link", linkText = "linkText")
      )

    verify(exactly = 1) { gameAnnouncementService.createGameAnnouncement(any<GameAnnouncement>()) }
    assertEquals(true, capturedGameAnnouncement.isCaptured)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(gameAnnouncement, result.body)
    assertEquals("message", capturedGameAnnouncement.captured.message)
    assertEquals("link", capturedGameAnnouncement.captured.link)
    assertEquals("linkText", capturedGameAnnouncement.captured.linkText)
  }

  @Test
  fun whenCreateAnnouncement_hasNewAnnouncementEmptyString_returnCreatedAnnouncement() {
    every {
      gameAnnouncementService.createGameAnnouncement(capture(capturedGameAnnouncement))
    } returns gameAnnouncement

    val result =
      gameAnnouncementController.createAnnouncement(
        GameAnnouncement(message = "message", link = "", linkText = "")
      )

    verify(exactly = 1) { gameAnnouncementService.createGameAnnouncement(any<GameAnnouncement>()) }
    assertEquals(true, capturedGameAnnouncement.isCaptured)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(gameAnnouncement, result.body)
    assertEquals("message", capturedGameAnnouncement.captured.message)
    assertEquals(null, capturedGameAnnouncement.captured.link)
    assertEquals(null, capturedGameAnnouncement.captured.linkText)
  }

  @Test
  fun whenCreateAnnouncement_hasNewAnnouncementNoLinkOrLinkText_returnCreatedAnnouncement() {
    every {
      gameAnnouncementService.createGameAnnouncement(capture(capturedGameAnnouncement))
    } returns gameAnnouncement

    val result =
      gameAnnouncementController.createAnnouncement(GameAnnouncement(message = "message"))

    verify(exactly = 1) { gameAnnouncementService.createGameAnnouncement(any<GameAnnouncement>()) }
    assertEquals(true, capturedGameAnnouncement.isCaptured)
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(gameAnnouncement, result.body)
    assertEquals("message", capturedGameAnnouncement.captured.message)
    assertNull(capturedGameAnnouncement.captured.link)
    assertNull(capturedGameAnnouncement.captured.linkText)
  }

  @Test
  fun whenDeleteAnnouncement_throwsException_returnErrorMessage() {
    every { gameAnnouncementService.deleteGameAnnouncement(id) } throws
      IllegalArgumentException("ID $id not found")

    val result = gameAnnouncementController.deleteAnnouncement(id)

    verify(exactly = 1) { gameAnnouncementService.deleteGameAnnouncement(id) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(mapOf("error" to true, "message" to "ID $id not found"), result.body)
  }

  @Test
  fun whenDeleteAnnouncement_Runs_returnSuccess() {
    every { gameAnnouncementService.deleteGameAnnouncement(id) } just runs

    val result = gameAnnouncementController.deleteAnnouncement(id)

    verify(exactly = 1) { gameAnnouncementService.deleteGameAnnouncement(id) }
    assertNull(result.body)
    assertEquals(HttpStatus.NO_CONTENT, result.statusCode)
  }
}
