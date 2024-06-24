package net.ddellspe.escapegenai.controller

import io.mockk.*
import java.util.*
import net.ddellspe.escapegenai.model.*
import net.ddellspe.escapegenai.service.PasswordService
import net.ddellspe.escapegenai.service.QuoteService
import net.ddellspe.escapegenai.service.TeamService
import net.ddellspe.escapegenai.service.TeamWordService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class TeamControllerTest {
  private val team: Team = mockk()
  private val teamContainer: TeamContainer = mockk()
  private val minimalTeam: MinimalTeam = mockk()
  private val teamService: TeamService = mockk()
  private val passwordService: PasswordService = mockk()
  private val teamWordService: TeamWordService = mockk()
  private val quoteService: QuoteService = mockk()
  private val password: Password = mockk()
  private val word: TeamWord = mockk()
  private val quote: Quote = mockk()
  private val id = UUID.randomUUID()
  private val teamController: TeamController =
      TeamController(teamService, passwordService, teamWordService, quoteService)

  @Test
  fun dumbCoverageTest() {
    teamController.teamService = teamService
    teamController.passwordService = passwordService
    teamController.teamWordService = teamWordService
    teamController.quoteService = quoteService
  }

  @Test
  fun whenGetTeamDetails_hasTeam_thenListOfTeam() {
    every { teamService.getAllTeams() } returns listOf(team)

    val result: ResponseEntity<List<Team>> = teamController.getTeamDetails()

    verify(exactly = 1) { teamService.getAllTeams() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(team, result.body?.get(0))
  }

  @Test
  fun whenGetTeams_hasTeam_thenListOfTeam() {
    every { teamService.getAllTeams() } returns listOf(team)
    every { team.toMinimalTeam() } returns minimalTeam

    val result: ResponseEntity<List<MinimalTeam>> = teamController.getTeams()

    verify(exactly = 1) { team.toMinimalTeam() }
    verify(exactly = 1) { teamService.getAllTeams() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(minimalTeam, result.body?.get(0))
  }

  @Test
  fun whenCreateTeam_hasValue_thenReturnTeam() {
    every { teamService.createTeam(teamContainer) } returns team
    every { team.toTeamContainer() } returns teamContainer

    val result: ResponseEntity<TeamContainerWithError> = teamController.createTeam(teamContainer)

    verify(exactly = 1) { teamService.createTeam(teamContainer) }
    verify(exactly = 1) { team.toTeamContainer() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertEquals(null, result.body?.error)
  }

  @Test
  fun whenCreateTeam_hasValueThrowsException_thenReturnError() {
    every { teamService.createTeam(teamContainer) } throws
        IllegalArgumentException("Team with id=123456 already exists, use update instead.")

    val result: ResponseEntity<TeamContainerWithError> = teamController.createTeam(teamContainer)

    verify(exactly = 1) { teamService.createTeam(teamContainer) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertEquals(
        mapOf(
            "error" to true,
            "message" to "Team with id=123456 already exists, use update instead.",
        ),
        result.body?.error,
    )
  }

  @Test
  fun whenUpdateTeam_hasIdNull_thenReturnError() {
    every { teamContainer.id } returns null

    val result: ResponseEntity<TeamContainerWithError> = teamController.updateTeam(teamContainer)

    verify(exactly = 1) { teamContainer.id }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertEquals(
        mapOf("error" to true, "message" to "Team has no id present, please use create instead."),
        result.body?.error,
    )
  }

  @Test
  fun whenUpdateTeam_hasIdNotNullHasPasswordNull_thenReturnError() {
    every { teamContainer.id } returns id
    every { teamContainer.passwordId } returns null

    val result: ResponseEntity<TeamContainerWithError> = teamController.updateTeam(teamContainer)

    verify(exactly = 1) { teamContainer.id }
    verify(exactly = 1) { teamContainer.passwordId }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertEquals(
        mapOf(
            "error" to true,
            "message" to
                "Team has no passwordId present, please use create to generate a password.",
        ),
        result.body?.error,
    )
  }

  @Test
  fun whenUpdateTeam_hasIdAndPasswordIDNotNullHasWordNull_thenReturnError() {
    every { teamContainer.id } returns id
    every { teamContainer.passwordId } returns id
    every { teamContainer.wordId } returns null

    val result: ResponseEntity<TeamContainerWithError> = teamController.updateTeam(teamContainer)

    verify(exactly = 1) { teamContainer.id }
    verify(exactly = 1) { teamContainer.passwordId }
    verify(exactly = 1) { teamContainer.wordId }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertEquals(
        mapOf(
            "error" to true,
            "message" to "Team has no wordId present, please use create to generate a word.",
        ),
        result.body?.error,
    )
  }

  @Test
  fun whenUpdateTeam_hasAllRequiredFieldsTeamDoesntExist_thenReturnError() {
    every { teamContainer.id } returns id
    every { teamContainer.passwordId } returns id
    every { teamContainer.wordId } returns id
    every { teamService.getTeam(id) } throws IllegalArgumentException("Team not found")

    val result: ResponseEntity<TeamContainerWithError> = teamController.updateTeam(teamContainer)

    verify(exactly = 3) { teamContainer.id }
    verify(exactly = 1) { teamContainer.passwordId }
    verify(exactly = 1) { teamContainer.wordId }
    verify(exactly = 1) { teamService.getTeam(id) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertEquals(
        mapOf(
            "error" to true,
            "message" to "Team with id=${id} not found, please use create instead.",
        ),
        result.body?.error,
    )
  }

  @Test
  fun whenUpdateTeam_hasAllRequiredFieldsNewPasswordNotFound_thenReturnError() {
    every { teamContainer.id } returns id
    every { teamContainer.passwordId } returns id
    every { teamContainer.wordId } returns id
    every { teamService.getTeam(id) } returns team
    every { team.password } returns password
    every { password.id } returns UUID.randomUUID()
    every { passwordService.getPassword(id) } throws IllegalArgumentException("Password not found")

    val result: ResponseEntity<TeamContainerWithError> = teamController.updateTeam(teamContainer)

    verify(exactly = 2) { teamContainer.id }
    verify(exactly = 4) { teamContainer.passwordId }
    verify(exactly = 1) { teamContainer.wordId }
    verify(exactly = 1) { team.password }
    verify(exactly = 1) { password.id }
    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { passwordService.getPassword(id) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertEquals(
        mapOf(
            "error" to true,
            "message" to
                "Password with id=${id} not found, please create a new team to generate a " +
                    "new password.",
        ),
        result.body?.error,
    )
  }

  @Test
  fun whenUpdateTeam_hasAllRequiredFieldsNewPasswordUpdated_thenReturnSuccess() {
    every { teamContainer.id } returns id
    every { teamContainer.passwordId } returns id
    every { teamContainer.wordId } returns id
    every { teamService.getTeam(id) } returns team
    every { team.password } returns password
    every { password.id } returns UUID.randomUUID()
    every { passwordService.getPassword(id) } returns password
    every { team.password = password } just runs
    every { team.passwordEntered = null } just runs
    every { team.word } returns word
    every { word.id } returns id
    every { teamContainer.quoteId } returns null
    every { teamService.updateTeam(team) } returns team
    every { team.toTeamContainer() } returns teamContainer

    val result: ResponseEntity<TeamContainerWithError> = teamController.updateTeam(teamContainer)

    verify(exactly = 2) { teamContainer.id }
    verify(exactly = 3) { teamContainer.passwordId }
    verify(exactly = 2) { teamContainer.wordId }
    verify(exactly = 1) { teamContainer.quoteId }
    verify(exactly = 1) { team.password }
    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { password.id }
    verify(exactly = 1) { passwordService.getPassword(id) }
    verify(exactly = 1) { team.password = password }
    verify(exactly = 1) { team.passwordEntered = null }
    verify(exactly = 1) { team.word }
    verify(exactly = 1) { word.id }
    verify(exactly = 1) { teamService.updateTeam(team) }
    verify(exactly = 1) { team.toTeamContainer() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertNull(result.body?.error)
  }

  @Test
  fun whenUpdateTeam_hasAllRequiredFieldsPasswordIsTheSameWordDifferentNotFound_thenReturnError() {
    every { teamContainer.id } returns id
    every { teamContainer.passwordId } returns id
    every { teamContainer.wordId } returns id
    every { teamService.getTeam(id) } returns team
    every { team.password } returns password
    every { password.id } returns id
    every { team.word } returns word
    every { word.id } returns UUID.randomUUID()
    every { teamWordService.getTeamWord(id) } throws IllegalArgumentException("Word not found")

    val result: ResponseEntity<TeamContainerWithError> = teamController.updateTeam(teamContainer)

    verify(exactly = 2) { teamContainer.id }
    verify(exactly = 2) { teamContainer.passwordId }
    verify(exactly = 4) { teamContainer.wordId }
    verify(exactly = 1) { team.password }
    verify(exactly = 1) { password.id }
    verify(exactly = 1) { team.word }
    verify(exactly = 1) { word.id }
    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { teamWordService.getTeamWord(id) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertEquals(
        mapOf(
            "error" to true,
            "message" to
                "Word with id=${id} not found, please create a new team to generate a " +
                    "new word.",
        ),
        result.body?.error,
    )
  }

  @Test
  fun whenUpdateTeam_hasAllRequiredFieldsNewWordUpdated_thenReturnSuccess() {
    every { teamContainer.id } returns id
    every { teamContainer.passwordId } returns id
    every { teamContainer.wordId } returns id
    every { teamService.getTeam(id) } returns team
    every { team.password } returns password
    every { password.id } returns id
    every { team.word } returns word
    every { word.id } returns UUID.randomUUID()
    every { teamWordService.getTeamWord(id) } returns word
    every { team.word = word } just runs
    every { team.wordEntered = null } just runs
    every { teamContainer.quoteId } returns null
    every { teamService.updateTeam(team) } returns team
    every { team.toTeamContainer() } returns teamContainer

    val result: ResponseEntity<TeamContainerWithError> = teamController.updateTeam(teamContainer)

    verify(exactly = 2) { teamContainer.id }
    verify(exactly = 2) { teamContainer.passwordId }
    verify(exactly = 3) { teamContainer.wordId }
    verify(exactly = 1) { teamContainer.quoteId }
    verify(exactly = 1) { team.password }
    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { password.id }
    verify(exactly = 1) { team.word }
    verify(exactly = 1) { teamWordService.getTeamWord(id) }
    verify(exactly = 1) { team.word = word }
    verify(exactly = 1) { team.wordEntered = null }
    verify(exactly = 1) { word.id }
    verify(exactly = 1) { teamService.updateTeam(team) }
    verify(exactly = 1) { team.toTeamContainer() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertNull(result.body?.error)
  }

  @Test
  fun whenUpdateTeam_hasAllRequiredFieldsQuoteNotNullAlreadySet_thenReturnSuccess() {
    every { teamContainer.id } returns id
    every { teamContainer.passwordId } returns id
    every { teamContainer.wordId } returns id
    every { teamService.getTeam(id) } returns team
    every { team.password } returns password
    every { password.id } returns id
    every { team.word } returns word
    every { word.id } returns id
    every { teamContainer.quoteId } returns id
    every { team.quote } returns quote
    every { quote.id } returns id
    every { teamService.updateTeam(team) } returns team
    every { team.toTeamContainer() } returns teamContainer

    val result: ResponseEntity<TeamContainerWithError> = teamController.updateTeam(teamContainer)

    verify(exactly = 2) { teamContainer.id }
    verify(exactly = 2) { teamContainer.passwordId }
    verify(exactly = 2) { teamContainer.wordId }
    verify(exactly = 2) { teamContainer.quoteId }
    verify(exactly = 1) { team.password }
    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { password.id }
    verify(exactly = 1) { team.word }
    verify(exactly = 1) { word.id }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { quote.id }
    verify(exactly = 1) { teamService.updateTeam(team) }
    verify(exactly = 1) { team.toTeamContainer() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertNull(result.body?.error)
  }

  @Test
  fun whenUpdateTeam_hasAllRequiredFieldsQuoteNullAlreadySet_thenReturnSuccess() {
    every { teamContainer.id } returns id
    every { teamContainer.passwordId } returns id
    every { teamContainer.wordId } returns id
    every { teamService.getTeam(id) } returns team
    every { team.password } returns password
    every { password.id } returns id
    every { team.word } returns word
    every { word.id } returns id
    every { teamContainer.quoteId } returns id
    every { team.quote } returns null
    every { quoteService.getQuote(id) } returns quote
    every { team.quote = quote } just runs
    every { team.quoteEntered = null } just runs
    every { teamService.updateTeam(team) } returns team
    every { team.toTeamContainer() } returns teamContainer

    val result: ResponseEntity<TeamContainerWithError> = teamController.updateTeam(teamContainer)

    verify(exactly = 2) { teamContainer.id }
    verify(exactly = 2) { teamContainer.passwordId }
    verify(exactly = 2) { teamContainer.wordId }
    verify(exactly = 3) { teamContainer.quoteId }
    verify(exactly = 1) { team.password }
    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { password.id }
    verify(exactly = 1) { team.word }
    verify(exactly = 1) { word.id }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { quoteService.getQuote(id) }
    verify(exactly = 1) { team.quote = quote }
    verify(exactly = 1) { team.quoteEntered = null }
    verify(exactly = 1) { teamService.updateTeam(team) }
    verify(exactly = 1) { team.toTeamContainer() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertNull(result.body?.error)
  }

  @Test
  fun whenUpdateTeam_hasAllRequiredFieldsQuoteDifferentNotFound_thenReturnError() {
    every { teamContainer.id } returns id
    every { teamContainer.passwordId } returns id
    every { teamContainer.wordId } returns id
    every { teamService.getTeam(id) } returns team
    every { team.password } returns password
    every { password.id } returns id
    every { team.word } returns word
    every { word.id } returns id
    every { teamContainer.quoteId } returns id
    every { team.quote } returns quote
    every { quote.id } returns UUID.randomUUID()
    every { quoteService.getQuote(id) } throws IllegalArgumentException("Quote not found")

    val result: ResponseEntity<TeamContainerWithError> = teamController.updateTeam(teamContainer)

    verify(exactly = 2) { teamContainer.id }
    verify(exactly = 2) { teamContainer.passwordId }
    verify(exactly = 2) { teamContainer.wordId }
    verify(exactly = 4) { teamContainer.quoteId }
    verify(exactly = 1) { team.password }
    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { password.id }
    verify(exactly = 1) { team.word }
    verify(exactly = 1) { word.id }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { quote.id }
    verify(exactly = 1) { quoteService.getQuote(id) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertEquals(
        mapOf(
            "error" to true,
            "message" to
                "Quote with id=${id} not found, please create a new quote first, then associate " +
                    "it with the team.",
        ),
        result.body?.error,
    )
  }

  @Test
  fun whenUpdateTeam_hasAllRequiredFieldsQuoteSuccessfullySet_thenReturnSuccess() {
    every { teamContainer.id } returns id
    every { teamContainer.passwordId } returns id
    every { teamContainer.wordId } returns id
    every { teamService.getTeam(id) } returns team
    every { team.password } returns password
    every { password.id } returns id
    every { team.word } returns word
    every { word.id } returns id
    every { teamContainer.quoteId } returns id
    every { team.quote } returns quote
    every { quote.id } returns UUID.randomUUID()
    every { quoteService.getQuote(id) } returns quote
    every { team.quote = quote } just runs
    every { team.quoteEntered = null } just runs
    every { teamService.updateTeam(team) } returns team
    every { team.toTeamContainer() } returns teamContainer

    val result: ResponseEntity<TeamContainerWithError> = teamController.updateTeam(teamContainer)

    verify(exactly = 2) { teamContainer.id }
    verify(exactly = 2) { teamContainer.passwordId }
    verify(exactly = 2) { teamContainer.wordId }
    verify(exactly = 3) { teamContainer.quoteId }
    verify(exactly = 1) { team.password }
    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { password.id }
    verify(exactly = 1) { team.word }
    verify(exactly = 1) { word.id }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { quote.id }
    verify(exactly = 1) { quoteService.getQuote(id) }
    verify(exactly = 1) { team.quote = quote }
    verify(exactly = 1) { team.quoteEntered = null }
    verify(exactly = 1) { teamService.updateTeam(team) }
    verify(exactly = 1) { team.toTeamContainer() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertNull(result.body?.error)
  }

  @Test
  fun whenUpdateTeam_hasAllRequiredFieldsWhenUpdatedThrowsException_thenReturnError() {
    every { teamContainer.id } returns id
    every { teamContainer.passwordId } returns id
    every { teamContainer.wordId } returns id
    every { teamService.getTeam(id) } returns team
    every { team.password } returns password
    every { password.id } returns id
    every { team.word } returns word
    every { word.id } returns id
    every { teamContainer.quoteId } returns null
    every { teamService.updateTeam(team) } throws IllegalArgumentException("Error")

    val result: ResponseEntity<TeamContainerWithError> = teamController.updateTeam(teamContainer)

    verify(exactly = 2) { teamContainer.id }
    verify(exactly = 2) { teamContainer.passwordId }
    verify(exactly = 2) { teamContainer.wordId }
    verify(exactly = 1) { teamContainer.quoteId }
    verify(exactly = 1) { team.password }
    verify(exactly = 1) { password.id }
    verify(exactly = 1) { word.id }
    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { teamService.updateTeam(team) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertEquals(mapOf("error" to true, "message" to "Error"), result.body?.error)
  }

  @Test
  fun whenUpdateTeam_hasAllRequiredFieldsPasswordAndWordIsTheSameQuoteNotSet_thenReturnSuccess() {
    every { teamContainer.id } returns id
    every { teamContainer.passwordId } returns id
    every { teamContainer.wordId } returns id
    every { teamService.getTeam(id) } returns team
    every { team.password } returns password
    every { password.id } returns id
    every { team.word } returns word
    every { word.id } returns id
    every { teamContainer.quoteId } returns null
    every { teamService.updateTeam(team) } returns team
    every { team.toTeamContainer() } returns teamContainer

    val result: ResponseEntity<TeamContainerWithError> = teamController.updateTeam(teamContainer)

    verify(exactly = 2) { teamContainer.id }
    verify(exactly = 2) { teamContainer.passwordId }
    verify(exactly = 2) { teamContainer.wordId }
    verify(exactly = 1) { teamContainer.quoteId }
    verify(exactly = 1) { team.password }
    verify(exactly = 1) { password.id }
    verify(exactly = 1) { word.id }
    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { teamService.updateTeam(team) }
    verify(exactly = 1) { team.toTeamContainer() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertNull(result.body?.error)
  }
}
