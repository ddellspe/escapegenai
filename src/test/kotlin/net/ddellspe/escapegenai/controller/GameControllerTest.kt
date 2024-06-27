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
  private val gameSubmission: GameSubmission = mockk()
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
              "href" to "http://localhost:8080/game/quotePart/00000000-0000-0000-0000-000000000000"
            ),
            mapOf(
              "href" to "http://localhost:8080/game/quotePart/11111111-1111-1111-1111-111111111111"
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
              "href" to "http://localhost:8080/game/quotePart/00000000-0000-0000-0000-000000000000"
            ),
            mapOf(
              "href" to "http://localhost:8080/game/quotePart/11111111-1111-1111-1111-111111111111"
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

  @Test
  fun whenSubmitGameState_hasNoTeam_thenExpectBadRequest() {
    every { teamService.getTeam(id) } throws IllegalArgumentException("No Team")
    every { gameSubmission.id } returns id

    val result = gameController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id)

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 2) { gameSubmission.id }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeamNoPassword_thenExpectOKNoPassword() {
    every { teamService.getTeam(id) } returns team
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.password } returns null

    val result = gameController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id)

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { team.id }
    verify(exactly = 1) { gameSubmission.password }
    verify(exactly = 1) { gameSubmission.id }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeamPasswordNotCorrect_thenExpectOKNoPassword() {
    every { teamService.getTeam(id) } returns team
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.password } returns "password"
    every { teamService.verifyTeamPassword(id, "password") } returns false

    val result = gameController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id)

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 2) { team.id }
    verify(exactly = 2) { gameSubmission.password }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 1) { teamService.verifyTeamPassword(id, "password") }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeamPasswordCorrectNoWord_thenExpectOKNoWord() {
    every { teamService.getTeam(id) } returns team
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.password } returns "password"
    every { teamService.verifyTeamPassword(id, "password") } returns true
    every { gameSubmission.teamWord } returns null

    val result = gameController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id, "password")

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 2) { team.id }
    verify(exactly = 3) { gameSubmission.password }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 1) { teamService.verifyTeamPassword(id, "password") }
    verify(exactly = 1) { gameSubmission.teamWord }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeamPasswordIncorrectWord_thenExpectOKNoWord() {
    every { teamService.getTeam(id) } returns team
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.password } returns "password"
    every { teamService.verifyTeamPassword(id, "password") } returns true
    every { gameSubmission.teamWord } returns "word"
    every { teamService.verifyTeamWord(id, "word") } returns false

    val result = gameController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id, "password")

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 3) { team.id }
    verify(exactly = 3) { gameSubmission.password }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 1) { teamService.verifyTeamPassword(id, "password") }
    verify(exactly = 2) { gameSubmission.teamWord }
    verify(exactly = 1) { teamService.verifyTeamWord(id, "word") }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeamPasswordWordNoQuote_thenExpectOKNoQuote() {
    every { teamService.getTeam(id) } returns team
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.password } returns "password"
    every { teamService.verifyTeamPassword(id, "password") } returns true
    every { gameSubmission.teamWord } returns "word"
    every { teamService.verifyTeamWord(id, "word") } returns true
    every { gameSubmission.quote } returns null

    val result = gameController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id, "password", "word")

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 3) { team.id }
    verify(exactly = 3) { gameSubmission.password }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 1) { teamService.verifyTeamPassword(id, "password") }
    verify(exactly = 3) { gameSubmission.teamWord }
    verify(exactly = 1) { teamService.verifyTeamWord(id, "word") }
    verify(exactly = 1) { gameSubmission.quote }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeamPasswordWordIncorrectQuote_thenExpectOKNoQuote() {
    every { teamService.getTeam(id) } returns team
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.password } returns "password"
    every { teamService.verifyTeamPassword(id, "password") } returns true
    every { gameSubmission.teamWord } returns "word"
    every { teamService.verifyTeamWord(id, "word") } returns true
    every { gameSubmission.quote } returns "quote"
    every { teamService.verifyTeamQuote(id, "quote") } returns false

    val result = gameController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id, "password", "word")

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 4) { team.id }
    verify(exactly = 3) { gameSubmission.password }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 1) { teamService.verifyTeamPassword(id, "password") }
    verify(exactly = 3) { gameSubmission.teamWord }
    verify(exactly = 1) { teamService.verifyTeamWord(id, "word") }
    verify(exactly = 2) { gameSubmission.quote }
    verify(exactly = 1) { teamService.verifyTeamQuote(id, "quote") }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeamPasswordWordQuoteNoFunFact_thenExpectOKNoFunFact() {
    every { teamService.getTeam(id) } returns team
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.password } returns "password"
    every { teamService.verifyTeamPassword(id, "password") } returns true
    every { gameSubmission.teamWord } returns "word"
    every { teamService.verifyTeamWord(id, "word") } returns true
    every { gameSubmission.quote } returns "quote"
    every { teamService.verifyTeamQuote(id, "quote") } returns true
    every { gameSubmission.fact } returns null

    val result = gameController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id, "password", "word", "quote")

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 4) { team.id }
    verify(exactly = 3) { gameSubmission.password }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 1) { teamService.verifyTeamPassword(id, "password") }
    verify(exactly = 3) { gameSubmission.teamWord }
    verify(exactly = 1) { teamService.verifyTeamWord(id, "word") }
    verify(exactly = 3) { gameSubmission.quote }
    verify(exactly = 1) { teamService.verifyTeamQuote(id, "quote") }
    verify(exactly = 1) { gameSubmission.fact }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeamPasswordWordQuoteIncorrectFunFact_thenExpectOKNoFunFact() {
    every { teamService.getTeam(id) } returns team
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.password } returns "password"
    every { teamService.verifyTeamPassword(id, "password") } returns true
    every { gameSubmission.teamWord } returns "word"
    every { teamService.verifyTeamWord(id, "word") } returns true
    every { gameSubmission.quote } returns "quote"
    every { teamService.verifyTeamQuote(id, "quote") } returns true
    every { gameSubmission.fact } returns "fact"
    every { teamService.verifyFunFact(id, "fact") } returns false

    val result = gameController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id, "password", "word", "quote")

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 5) { team.id }
    verify(exactly = 3) { gameSubmission.password }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 1) { teamService.verifyTeamPassword(id, "password") }
    verify(exactly = 3) { gameSubmission.teamWord }
    verify(exactly = 1) { teamService.verifyTeamWord(id, "word") }
    verify(exactly = 3) { gameSubmission.quote }
    verify(exactly = 1) { teamService.verifyTeamQuote(id, "quote") }
    verify(exactly = 2) { gameSubmission.fact }
    verify(exactly = 1) { teamService.verifyFunFact(id, "fact") }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeamPasswordWordQuoteFunFact_thenExpectOKAllData() {
    every { teamService.getTeam(id) } returns team
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.password } returns "password"
    every { teamService.verifyTeamPassword(id, "password") } returns true
    every { gameSubmission.teamWord } returns "word"
    every { teamService.verifyTeamWord(id, "word") } returns true
    every { gameSubmission.quote } returns "quote"
    every { teamService.verifyTeamQuote(id, "quote") } returns true
    every { gameSubmission.fact } returns "fact"
    every { teamService.verifyFunFact(id, "fact") } returns true

    val result = gameController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id, "password", "word", "quote", "fact")

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 5) { team.id }
    verify(exactly = 3) { gameSubmission.password }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 1) { teamService.verifyTeamPassword(id, "password") }
    verify(exactly = 3) { gameSubmission.teamWord }
    verify(exactly = 1) { teamService.verifyTeamWord(id, "word") }
    verify(exactly = 3) { gameSubmission.quote }
    verify(exactly = 1) { teamService.verifyTeamQuote(id, "quote") }
    verify(exactly = 3) { gameSubmission.fact }
    verify(exactly = 1) { teamService.verifyFunFact(id, "fact") }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }
}
