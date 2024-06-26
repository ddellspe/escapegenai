package net.ddellspe.escapegenai.service

import io.mockk.*
import java.time.OffsetDateTime
import java.util.*
import net.ddellspe.escapegenai.model.*
import net.ddellspe.escapegenai.repository.TeamRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

class TeamServiceTest {
  private val teamRepository: TeamRepository = mockk()
  private val teamService = TeamService(teamRepository)
  private val team: Team = mockk()
  private val quote: Quote = mockk()
  private val word: TeamWord = mockk()
  private val password: Password = mockk()
  private val dateSlot = slot<OffsetDateTime>()
  private val id = UUID.randomUUID()

  @Test
  fun dumbCoverageTest() {
    teamService.teamRepository = teamRepository
  }

  @Test
  fun whenCreateTeam_hasNoId_thenReturnTeam() {
    val teamContainer = TeamContainer(name = "test")
    every { teamRepository.save(match { it.name == "test" }) } returns team

    val result: Team = teamService.createTeam(teamContainer)

    verify(exactly = 1) { teamRepository.save(match { it.name == "test" }) }
    assertEquals(team, result)
  }

  @Test
  fun whenCreateTeam_hasIdInRepository_thenExpectException() {
    val teamContainer = TeamContainer(id = id, name = "test")
    every { teamRepository.findByIdOrNull(id) } returns team

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { teamService.createTeam(teamContainer) }

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    assertEquals("Team with id=${id} already exists, use update instead.", exception.message)
  }

  @Test
  fun whenCreateTeam_hasIdNotInRepository_thenReturnTeam() {
    val teamContainer = TeamContainer(id = id, name = "test")
    every { teamRepository.findByIdOrNull(id) } returns null
    every { teamRepository.save(match { it.name == "test" }) } returns team

    val result: Team = teamService.createTeam(teamContainer)

    verify(exactly = 1) { teamRepository.save(match { it.name == "test" }) }
    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    assertEquals(team, result)
  }

  @Test
  fun whenUpdateTeam_hasNoTeam_thenThrowException() {
    every { teamRepository.findByIdOrNull(id) } returns null
    every { team.id } returns id

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { teamService.updateTeam(team) }

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.id }
    assertEquals("Team with id=${id} does not exist, please create it first.", exception.message)
  }

  @Test
  fun whenUpdateTeam_hasTeam_thenSaves() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { teamRepository.save(team) } returns team
    every { team.id } returns id

    val result: Team = teamService.updateTeam(team)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { teamRepository.save(team) }
    verify(exactly = 1) { team.id }
    assertEquals(team, result)
  }

  @Test
  fun whenGetTeam_hasTeam_thenReturnTeam() {
    every { teamRepository.findByIdOrNull(id) } returns team

    val result = teamService.getTeam(id)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    assertEquals(team, result)
  }

  @Test
  fun whenGetTeam_hasNoTeam_thenThrowException() {
    every { teamRepository.findByIdOrNull(id) } returns null

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { teamService.getTeam(id) }

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    assertEquals("Team with id=${id} does not exist.", exception.message)
  }

  @Test
  fun whenGetAllTeams_hasNoTeams_thenExpectEmptyList() {
    every { teamRepository.findAll() } returns emptyList()

    val result: List<Team> = teamService.getAllTeams()

    verify(exactly = 1) { teamRepository.findAll() }
    assertEquals(emptyList<Team>(), result)
  }

  @Test
  fun whenGetAllTeams_hasTeams_thenExpectListWithTeam() {
    every { teamRepository.findAll() } returns listOf(team)

    val result: List<Team> = teamService.getAllTeams()

    verify(exactly = 1) { teamRepository.findAll() }
    assertEquals(listOf(team), result)
  }

  @Test
  fun whenDeleteTeam_hasNoTeam_thenExpectError() {
    every { teamRepository.findByIdOrNull(id) } returns null

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { teamService.deleteTeam(id) }

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    assertEquals("Team with id=${id} does not exist.", exception.message)
  }

  @Test
  fun whenDeleteTeam_hasTeam_thenExpectNoError() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { teamRepository.delete(team) } just runs

    teamService.deleteTeam(id)
  }

  @Test
  fun whenVerifyTeamPassword_hasNoTeam_thenExpectException() {
    every { teamRepository.findByIdOrNull(id) } returns null

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { teamService.verifyTeamPassword(id, "password") }

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    assertEquals("Team with id=${id} does not exist.", exception.message)
  }

  @Test
  fun whenVerifyTeamPassword_hasTeamPasswordIncorrect_thenExpectFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.password } returns password
    every { password.password } returns "password2"

    val result: Boolean = teamService.verifyTeamPassword(id, "password")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.password }
    verify(exactly = 1) { password.password }
    assertEquals(false, result)
  }

  @Test
  fun whenVerifyTeamPassword_hasTeamPasswordCorrectNoDate_thenExpectTrue() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { teamRepository.save(team) } returns team
    every { team.password } returns password
    every { team.passwordEntered } returns null
    every { team.passwordEntered = capture(dateSlot) } just runs
    every { password.password } returns "password"

    val result: Boolean = teamService.verifyTeamPassword(id, "password")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.password }
    verify(exactly = 1) { team.passwordEntered }
    verify(exactly = 1) { team.passwordEntered = any<OffsetDateTime>() }
    verify(exactly = 1) { password.password }
    assertEquals(true, result)
    assertEquals(true, dateSlot.captured.isBefore(OffsetDateTime.now()))
  }

  @Test
  fun whenVerifyTeamPassword_hasTeamPasswordCorrectDate_thenExpectTrue() {
    val dt: OffsetDateTime = OffsetDateTime.now()
    every { teamRepository.findByIdOrNull(id) } returns team
    every { teamRepository.save(team) } returns team
    every { team.password } returns password
    every { team.passwordEntered } returns dt
    every { team.passwordEntered = dt } just runs
    every { password.password } returns "password"

    val result: Boolean = teamService.verifyTeamPassword(id, "password")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.password }
    verify(exactly = 1) { team.passwordEntered }
    verify(exactly = 1) { team.passwordEntered = dt }
    verify(exactly = 1) { password.password }
    assertEquals(true, result)
  }

  @Test
  fun whenVerifyTeamWord_hasNoTeam_thenExpectException() {
    every { teamRepository.findByIdOrNull(id) } returns null

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { teamService.verifyTeamWord(id, "word") }

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    assertEquals("Team with id=${id} does not exist.", exception.message)
  }

  @Test
  fun whenVerifyTeamWord_hasTeamWordIncorrect_thenExpectFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.word } returns word
    every { word.word } returns "word2"

    val result: Boolean = teamService.verifyTeamWord(id, "word")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.word }
    verify(exactly = 1) { word.word }
    assertEquals(false, result)
  }

  @Test
  fun whenVerifyTeamWord_hasTeamWordCorrectNoDate_thenExpectTrue() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { teamRepository.save(team) } returns team
    every { team.word } returns word
    every { team.wordEntered } returns null
    every { team.wordEntered = capture(dateSlot) } just runs
    every { word.word } returns "word"

    val result: Boolean = teamService.verifyTeamWord(id, "word")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.word }
    verify(exactly = 1) { team.wordEntered }
    verify(exactly = 1) { team.wordEntered = any<OffsetDateTime>() }
    verify(exactly = 1) { word.word }
    assertEquals(true, result)
    assertEquals(true, dateSlot.captured.isBefore(OffsetDateTime.now()))
  }

  @Test
  fun whenVerifyTeamWord_hasTeamWordCorrectDate_thenExpectTrue() {
    val dt: OffsetDateTime = OffsetDateTime.now()
    every { teamRepository.findByIdOrNull(id) } returns team
    every { teamRepository.save(team) } returns team
    every { team.word } returns word
    every { team.wordEntered } returns dt
    every { team.wordEntered = dt } just runs
    every { word.word } returns "word"

    val result: Boolean = teamService.verifyTeamWord(id, "word")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.word }
    verify(exactly = 1) { team.wordEntered }
    verify(exactly = 1) { team.wordEntered = dt }
    verify(exactly = 1) { word.word }
    assertEquals(true, result)
  }

  @Test
  fun whenVerifyTeamQuote_hasNoTeam_thenExpectException() {
    every { teamRepository.findByIdOrNull(id) } returns null

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { teamService.verifyTeamQuote(id, "quote") }

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    assertEquals("Team with id=${id} does not exist.", exception.message)
  }

  @Test
  fun whenVerifyTeamQuote_hasNoQuote_thenExpectFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.quote } returns null

    val result: Boolean = teamService.verifyTeamQuote(id, "quote")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.quote }
    assertEquals(false, result)
  }

  @Test
  fun whenVerifyTeamQuote_hasTeamQuoteIncorrect_thenExpectFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.quote } returns quote
    every { quote.quote } returns "quote2"

    val result: Boolean = teamService.verifyTeamQuote(id, "quote")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { quote.quote }
    assertEquals(false, result)
  }

  @Test
  fun whenVerifyTeamQuote_hasTeamQuoteCorrectNoDate_thenExpectTrue() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { teamRepository.save(team) } returns team
    every { team.quote } returns quote
    every { team.quoteEntered } returns null
    every { team.quoteEntered = capture(dateSlot) } just runs
    every { quote.quote } returns "quote"

    val result: Boolean = teamService.verifyTeamQuote(id, "quote")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { team.quoteEntered }
    verify(exactly = 1) { team.quoteEntered = any<OffsetDateTime>() }
    verify(exactly = 1) { quote.quote }
    assertEquals(true, result)
    assertEquals(true, dateSlot.captured.isBefore(OffsetDateTime.now()))
  }

  @Test
  fun whenVerifyTeamQuote_hasTeamQuoteCorrectDate_thenExpectTrue() {
    val dt: OffsetDateTime = OffsetDateTime.now()
    every { teamRepository.findByIdOrNull(id) } returns team
    every { teamRepository.save(team) } returns team
    every { team.quote } returns quote
    every { team.quoteEntered } returns dt
    every { team.quoteEntered = dt } just runs
    every { quote.quote } returns "quote"

    val result: Boolean = teamService.verifyTeamQuote(id, "quote")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { team.quoteEntered }
    verify(exactly = 1) { team.quoteEntered = dt }
    verify(exactly = 1) { quote.quote }
    assertEquals(true, result)
  }

  @Test
  fun whenVerifyFunFactType_hasNoTeam_thenExpectException() {
    every { teamRepository.findByIdOrNull(id) } returns null

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { teamService.verifyFunFact(id, "funFact") }

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    assertEquals("Team with id=${id} does not exist.", exception.message)
  }

  @Test
  fun whenVerifyFunFact_hasNoQuote_thenExpectFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.quote } returns null

    val result = teamService.verifyFunFact(id, "funFact")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.quote }
    assertEquals(false, result)
  }

  @Test
  fun whenVerifyFunFact_hasQuoteInvalidLookupType_thenExpectFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.quote } returns quote
    every { team.funFactType } returns "unknown"

    val result = teamService.verifyFunFact(id, "funFact")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { team.funFactType }
    assertEquals(false, result)
  }

  @Test
  fun whenVerifyFunFact_hasQuoteTypeAuthorNotTheSame_thenExpectFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.quote } returns quote
    every { team.funFactType } returns "author"
    every { quote.author } returns "funFact"

    val result = teamService.verifyFunFact(id, "funFact2")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { team.funFactType }
    verify(exactly = 1) { quote.author }
    assertEquals(false, result)
  }

  @Test
  fun whenVerifyFunFact_hasQuoteTypeAuthorAddressNotTheSame_thenExpectFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.quote } returns quote
    every { team.funFactType } returns "authorAddress"
    every { quote.authorAddress } returns "funFact"

    val result = teamService.verifyFunFact(id, "funFact2")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { team.funFactType }
    verify(exactly = 1) { quote.authorAddress }
    assertEquals(false, result)
  }

  @Test
  fun whenVerifyFunFact_hasQuoteTypeAuthorTitleNotTheSame_thenExpectFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.quote } returns quote
    every { team.funFactType } returns "authorTitle"
    every { quote.authorTitle } returns "funFact"

    val result = teamService.verifyFunFact(id, "funFact2")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { team.funFactType }
    verify(exactly = 1) { quote.authorTitle }
    assertEquals(false, result)
  }

  @Test
  fun whenVerifyFunFact_hasQuoteTypeCompanyNotTheSame_thenExpectFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.quote } returns quote
    every { team.funFactType } returns "company"
    every { quote.company } returns "funFact"

    val result = teamService.verifyFunFact(id, "funFact2")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { team.funFactType }
    verify(exactly = 1) { quote.company }
    assertEquals(false, result)
  }

  @Test
  fun whenVerifyFunFact_hasQuoteTypeCompanyAddressNotTheSame_thenExpectFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.quote } returns quote
    every { team.funFactType } returns "companyAddress"
    every { quote.companyAddress } returns "funFact"

    val result = teamService.verifyFunFact(id, "funFact2")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { team.funFactType }
    verify(exactly = 1) { quote.companyAddress }
    assertEquals(false, result)
  }

  @Test
  fun whenVerifyFunFact_hasQuoteTypeCompanyIndustryNotTheSame_thenExpectFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.quote } returns quote
    every { team.funFactType } returns "companyIndustry"
    every { quote.companyIndustry } returns "funFact"

    val result = teamService.verifyFunFact(id, "funFact2")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { team.funFactType }
    verify(exactly = 1) { quote.companyIndustry }
    assertEquals(false, result)
  }

  @Test
  fun whenVerifyFunFact_hasQuoteTypeAuthorTheSame_thenExpectTrue() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.quote } returns quote
    every { team.funFactType } returns "author"
    every { quote.author } returns "funFact"
    every { team.funFactEntered } returns null
    every { team.funFactEntered = capture(dateSlot) } just runs
    every { teamRepository.save(team) } returns team

    val result = teamService.verifyFunFact(id, "funFact")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { team.funFactType }
    verify(exactly = 1) { quote.author }
    verify(exactly = 1) { team.funFactEntered }
    verify(exactly = 1) { team.funFactEntered = any<OffsetDateTime>() }
    verify(exactly = 1) { teamRepository.save(team) }
    assertEquals(true, result)
    assertEquals(true, dateSlot.captured.isBefore(OffsetDateTime.now()))
  }

  @Test
  fun whenVerifyFunFact_hasQuoteTypeAuthorTheSameDateAlreadySet_thenExpectTrue() {
    val dt = OffsetDateTime.now()
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.quote } returns quote
    every { team.funFactType } returns "author"
    every { quote.author } returns "funFact"
    every { team.funFactEntered } returns dt
    every { team.funFactEntered = dt } just runs
    every { teamRepository.save(team) } returns team

    val result = teamService.verifyFunFact(id, "funFact")

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.quote }
    verify(exactly = 1) { team.funFactType }
    verify(exactly = 1) { quote.author }
    verify(exactly = 1) { team.funFactEntered }
    verify(exactly = 1) { team.funFactEntered = dt }
    verify(exactly = 1) { teamRepository.save(team) }
    assertEquals(true, result)
  }
}
