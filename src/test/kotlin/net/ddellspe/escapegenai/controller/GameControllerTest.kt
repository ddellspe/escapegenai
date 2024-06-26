package net.ddellspe.escapegenai.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import net.ddellspe.escapegenai.model.*
import net.ddellspe.escapegenai.service.QuotePartService
import net.ddellspe.escapegenai.service.TeamService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class GameControllerTest {
  private val teamService: TeamService = mockk()
  private val quotePartService: QuotePartService = mockk()
  private val team: Team = mockk()
  private val minimalTeam: MinimalTeam = mockk()
  private val password: Password = mockk()
  private val word: TeamWord = mockk()
  private val quote: Quote = mockk()
  private val quotePart: QuotePart = mockk()
  private val id = UUID.randomUUID()
  private val gameController = GameController(teamService, quotePartService)

  @Test
  fun dumbCoverageTests() {
    gameController.teamService = teamService
    gameController.quotePartService = quotePartService
  }

  @Test
  fun whenGetTeams_hasNoTeams_thenNoTeamsPresent() {
    every { teamService.getAllTeams() } returns emptyList()

    val result = gameController.getTeams()

    verify(exactly = 1) { teamService.getAllTeams() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(0, result.body?.size)
  }

  @Test
  fun whenGetTeams_hasTeams_thenTeamsPresent() {
    every { teamService.getAllTeams() } returns listOf(team)
    every { team.toMinimalTeam() } returns minimalTeam

    val result = gameController.getTeams()

    verify(exactly = 1) { teamService.getAllTeams() }
    verify(exactly = 1) { team.toMinimalTeam() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(1, result.body?.size)
    assertEquals(minimalTeam, result.body?.get(0))
  }

  @Test
  fun whenGetTeamPassword_hasTeamNotFound_thenResultIsNotFound() {
    every { teamService.getTeam(id) } throws IllegalArgumentException("Not Found")

    val result = gameController.getTeamPassword(id)

    verify(exactly = 1) { teamService.getTeam(id) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
    assertEquals("Not Found", result.body)
  }

  @Test
  fun whenGetTeamPassword_hasTeam_thenExpectTeamNameInContent() {
    every { teamService.getTeam(id) } returns team
    every { team.name } returns "Team Name"
    every { team.password } returns password
    every { password.pageContent } returns "Content"

    val result = gameController.getTeamPassword(id)

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { team.name }
    verify(exactly = 1) { team.password }
    verify(exactly = 1) { password.pageContent }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(MediaType.TEXT_HTML, result.headers.contentType)
    assertEquals(true, result.body?.contains("Team Name"))
    assertEquals(true, result.body?.contains("Content"))
  }

  @Test
  fun whenGetTeamWord_hasTeamNotFound_thenResultIsNotFound() {
    every { teamService.getTeam(id) } throws IllegalArgumentException("Not Found")

    val result = gameController.getTeamWord(id)

    verify(exactly = 1) { teamService.getTeam(id) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
    assertEquals("Not Found", result.body)
  }

  @Test
  fun whenGetTeamWord_hasTeam_thenExpectTeamNameInContent() {
    every { teamService.getTeam(id) } returns team
    every { team.name } returns "Team Name"
    every { team.word } returns word
    every { word.generatedContent } returns "Content"

    val result = gameController.getTeamWord(id)

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { team.name }
    verify(exactly = 1) { team.word }
    verify(exactly = 1) { word.generatedContent }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(MediaType.TEXT_HTML, result.headers.contentType)
    assertEquals(true, result.body?.contains("Team Name"))
    assertEquals(true, result.body?.contains("Content"))
  }

  @Test
  fun whenGetQuoteParts_hasTeamNotFound_thenResultIsNotFound() {
    every { teamService.getTeam(id) } throws IllegalArgumentException("Not Found")

    val result = gameController.getQuoteParts(id)

    verify(exactly = 1) { teamService.getTeam(id) }
    assertNull(result.body)
    assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
  }

  @Test
  fun whenGetQuoteParts_hasTeamFound_thenResultHasData() {
    val id1 = UUID.fromString("00000000-0000-0000-0000-000000000000")
    val id2 = UUID.fromString("11111111-1111-1111-1111-111111111111")
    every { teamService.getTeam(id) } returns team
    every { team.quote } returns quote
    every { quote.quoteParts } returns mutableListOf(quotePart, quotePart)
    every { quotePart.id }.returnsMany(id1, id2)

    val result = gameController.getQuoteParts(id)

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { quote.quoteParts }
    verify(exactly = 2) { quotePart.id }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(
      mapOf(
        "links" to
          listOf(
            mapOf(
              "href" to
                "https://escapegenai.com/game/quotePart/00000000-0000-0000-0000-000000000000"
            ),
            mapOf(
              "href" to
                "https://escapegenai.com/game/quotePart/11111111-1111-1111-1111-111111111111"
            ),
          ),
        "otherData" to id,
      ),
      result.body,
    )
  }

  @Test
  fun whenGetQuoteParts_hasTeamFoundReverseOrder_thenResultIsHasDAta() {
    val id1 = UUID.fromString("00000000-0000-0000-0000-000000000000")
    val id2 = UUID.fromString("11111111-1111-1111-1111-111111111111")
    every { teamService.getTeam(id) } returns team
    every { team.quote } returns quote
    every { quote.quoteParts } returns mutableListOf(quotePart, quotePart)
    every { quotePart.id }.returnsMany(id2, id1)

    val result = gameController.getQuoteParts(id)

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { quote.quoteParts }
    verify(exactly = 2) { quotePart.id }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(
      mapOf(
        "links" to
          listOf(
            mapOf(
              "href" to
                "https://escapegenai.com/game/quotePart/00000000-0000-0000-0000-000000000000"
            ),
            mapOf(
              "href" to
                "https://escapegenai.com/game/quotePart/11111111-1111-1111-1111-111111111111"
            ),
          ),
        "otherData" to id,
      ),
      result.body,
    )
  }

  @Test
  fun whenGetQuoteDocument_hasTeamNotFound_thenResultIsNotFound() {
    every { quotePartService.getQuotePart(id) } throws IllegalArgumentException("Not Found")

    val result = gameController.getQuoteDocument(id)

    verify(exactly = 1) { quotePartService.getQuotePart(id) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
    assertEquals("Not Found", result.body)
  }

  @Test
  fun whenGetQuoteDocument_hasTeam_thenExpectTeamNameInContent() {
    every { quotePartService.getQuotePart(id) } returns quotePart
    every { quotePart.generatedContent } returns "Content"

    val result = gameController.getQuoteDocument(id)

    verify(exactly = 1) { quotePartService.getQuotePart(id) }
    verify(exactly = 1) { quotePart.generatedContent }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(MediaType.TEXT_HTML, result.headers.contentType)
    assertEquals(true, result.body?.contains("the quote word"))
    assertEquals(true, result.body?.contains("Content"))
  }
}
