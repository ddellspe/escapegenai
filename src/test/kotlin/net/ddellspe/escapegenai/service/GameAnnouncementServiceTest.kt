package net.ddellspe.escapegenai.service

import io.mockk.*
import java.util.*
import net.ddellspe.escapegenai.model.GameAnnouncement
import net.ddellspe.escapegenai.repository.GameAnnouncementRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

class GameAnnouncementServiceTest {
  private var gameAnnouncementRepository: GameAnnouncementRepository = mockk()
  private var gameAnnouncementService = GameAnnouncementService(gameAnnouncementRepository)
  private var gameAnnouncement: GameAnnouncement = mockk()
  private var id = UUID.randomUUID()

  @Test
  fun dumbTest() {
    gameAnnouncementService.gameAnnouncementRepository = gameAnnouncementRepository
  }

  @Test
  fun whenGameAnnouncementRepository_hasNoAnnouncements_returnEmptyList() {
    every { gameAnnouncementRepository.findAll() } returns emptyList()

    val result = gameAnnouncementService.getAllGameAnnouncements()

    verify(exactly = 1) { gameAnnouncementRepository.findAll() }
    assertEquals(emptyList<GameAnnouncement>(), result)
  }

  @Test
  fun whenGameAnnouncementRepository_hasAnnouncements_returnAnnouncements() {
    every { gameAnnouncementRepository.findAll() } returns listOf(gameAnnouncement)

    val result = gameAnnouncementService.getAllGameAnnouncements()

    verify(exactly = 1) { gameAnnouncementRepository.findAll() }
    assertEquals(1, result.size)
    assertEquals(gameAnnouncement, result.first())
  }

  @Test
  fun whenGameAnnouncement_present_returnAnnouncement() {
    every { gameAnnouncementRepository.save(gameAnnouncement) } returns gameAnnouncement

    val result = gameAnnouncementService.createGameAnnouncement(gameAnnouncement)

    verify(exactly = 1) { gameAnnouncementRepository.save(gameAnnouncement) }
    assertEquals(gameAnnouncement, result)
  }

  @Test
  fun whenGameAnnouncement_notPresent_deleteReturnException() {
    every { gameAnnouncementRepository.findByIdOrNull(id) } returns null

    val result =
      assertThrows<IllegalArgumentException> { gameAnnouncementService.deleteGameAnnouncement(id) }

    verify(exactly = 1) { gameAnnouncementRepository.findByIdOrNull(id) }
    assertEquals("GameAnnouncement with id=${id} does not exist.", result.message)
  }

  @Test
  fun whenGameAnnouncement_present_deleteRuns() {
    every { gameAnnouncementRepository.findByIdOrNull(id) } returns gameAnnouncement
    every { gameAnnouncementRepository.delete(gameAnnouncement) } just runs

    gameAnnouncementService.deleteGameAnnouncement(id)

    verify(exactly = 1) { gameAnnouncementRepository.findByIdOrNull(id) }
    verify(exactly = 1) { gameAnnouncementRepository.delete(gameAnnouncement) }
  }
}
