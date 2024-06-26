package net.ddellspe.escapegenai.model

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import net.ddellspe.escapegenai.util.generateParts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class DumbCoverageTests {
  @Test
  fun testContainerTests() {
    val teamContainer = TeamContainer(name = "Test Team")

    assertEquals(null, teamContainer.passwordId)
  }

  @Test
  fun testContainerWithErrorTests() {
    val teamContainerWithError = TeamContainerWithError()

    assertNull(teamContainerWithError.teamContainer)
    assertNull(teamContainerWithError.error)
  }

  @Test
  fun toQuoteContainerTest() {
    val quote = Quote(UUID.randomUUID(), "quote")

    val quoteContainer = QuoteContainer(quote.id, "quote")

    assertEquals(quoteContainer, quote.toQuoteContainer())
  }

  @Test
  fun quoteContainerWithError() {
    val quoteContainerWithError = QuoteContainerWithError()

    assertNull(quoteContainerWithError.quoteContainer)
    assertNull(quoteContainerWithError.error)
  }

  @Test
  fun minimalTeamTest() {
    val minimalTeam = MinimalTeam(UUID.randomUUID(), "test")

    assertEquals(null, minimalTeam.passwordEntered)
  }

  @Test
  fun gameSubmissionTest() {
    val gameSubmission = GameSubmission(UUID.randomUUID())

    assertEquals(null, gameSubmission.fact)
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
    assertEquals(expected, minimalTeam)
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
        team.funFactType,
        null,
      )
    assertEquals(expected, teamContainer)
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
        team.funFactType,
        null,
      )
    assertEquals(expected, teamContainer)
  }

  @Test
  fun toGameTeamQuoteNotPresent() {
    val team = Team()
    team.passwordEntered = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
    team.wordEntered = OffsetDateTime.of(2024, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)

    val gameTeam = team.toGameTeam()

    val expected =
      GameTeam(
        team.id,
        team.name,
        team.password.id,
        team.word.id,
        null,
        emptyList<UUID>(),
        team.funFactType,
      )
    assertEquals(expected, gameTeam)
  }

  @Test
  fun toGameTeamQuotePresent() {
    val team = Team()
    val quote = Quote(quote = "things", quoteParts = generateParts("things"))
    team.quote = quote
    team.passwordEntered = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
    team.wordEntered = OffsetDateTime.of(2024, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)

    val gameTeam = team.toGameTeam()

    val expected =
      GameTeam(
        team.id,
        team.name,
        team.password.id,
        team.word.id,
        quote.id,
        quote.quoteParts.stream().map { q -> q.id }.toList(),
        team.funFactType,
      )
    assertEquals(expected, gameTeam)
  }
}
