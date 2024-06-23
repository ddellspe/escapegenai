package net.ddellspe.escapegenai.model

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import net.ddellspe.escapegenai.util.generateParts
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DumbCoverageTests {
  @Test
  fun testContainerTests() {
    val teamContainer = TeamContainer(name = "Test Team")

    Assertions.assertEquals(null, teamContainer.passwordId)
  }

  @Test
  fun testContainerWithErrorTests() {
    val teamContainerWithError = TeamContainerWithError()

    Assertions.assertNull(teamContainerWithError.teamContainer)
    Assertions.assertNull(teamContainerWithError.error)
  }

  @Test
  fun toQuoteContainerTest() {
    val quote = Quote(UUID.randomUUID(), "quote")

    val quoteContainer = QuoteContainer(quote.id, "quote")

    Assertions.assertEquals(quoteContainer, quote.toQuoteContainer())
  }

  @Test
  fun minimalTeamTest() {
    val minimalTeam = MinimalTeam(UUID.randomUUID(), "test")

    Assertions.assertEquals(null, minimalTeam.passwordEntered)
  }

  @Test
  fun gameSubmissionTest() {
    val gameSubmission = GameSubmission(UUID.randomUUID())

    Assertions.assertEquals(null, gameSubmission.fact)
  }

  @Test
  fun testToMinimalTeam() {
    val team = Team()
    team.passwordEntered = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
    team.wordEntered = OffsetDateTime.of(2024, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)
    team.quoteEntered = OffsetDateTime.of(2024, 1, 3, 0, 0, 0, 0, ZoneOffset.UTC)
    team.funFactEntered = OffsetDateTime.of(2024, 1, 4, 0, 0, 0, 0, ZoneOffset.UTC)

    val minimalTeam = team.toMinimalTeam()

    val expected =
      MinimalTeam(
        team.id,
        team.name,
        team.passwordEntered,
        team.wordEntered,
        team.quoteEntered,
        team.funFactEntered,
      )
    Assertions.assertEquals(expected, minimalTeam)
  }

  @Test
  fun toTeamContainerQuoteNotPresent() {
    val team = Team()
    team.passwordEntered = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
    team.wordEntered = OffsetDateTime.of(2024, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)

    val teamContainer = team.toTeamContainer()

    val expected =
      TeamContainer(
        team.id,
        team.name,
        team.password.id,
        team.passwordEntered,
        team.word.id,
        team.wordEntered,
        null,
        null,
        null,
        null,
      )
    Assertions.assertEquals(expected, teamContainer)
  }

  @Test
  fun toTeamContainerQuotePresent() {
    val team = Team()
    val quote = Quote(quote = "things", quoteParts = generateParts("things"))
    team.quote = quote
    team.passwordEntered = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
    team.wordEntered = OffsetDateTime.of(2024, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)

    val teamContainer = team.toTeamContainer()

    val expected =
      TeamContainer(
        team.id,
        team.name,
        team.password.id,
        team.passwordEntered,
        team.word.id,
        team.wordEntered,
        quote.id,
        null,
        null,
        null,
      )
    Assertions.assertEquals(expected, teamContainer)
  }
}
