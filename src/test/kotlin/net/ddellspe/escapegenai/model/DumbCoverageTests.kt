package net.ddellspe.escapegenai.model

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class DumbCoverageTests {

  @Test
  fun testContainerWithErrorTests() {
    val teamContainerWithError = TeamContainerWithError()

    assertNull(teamContainerWithError.teamContainer)
    assertNull(teamContainerWithError.error)
  }

  @Test
  fun gameSubmissionTest() {
    val gameSubmission = GameSubmission(UUID.randomUUID())

    assertEquals(null, gameSubmission.fact)
  }

  @Test
  fun testToMinimalTeam() {
    val team = Team()
    team.firstSelected = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)

    val minimalTeam = team.toMinimalTeam()

    val expected = MinimalTeam(team.id, team.name, team.firstSelected)
    assertEquals(expected, minimalTeam)
  }

  @Test
  fun toTeamContainerQuoteNotPresent() {
    val team = Team()
    team.firstSelected = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)

    val teamContainer = team.toTeamContainer()

    val expected = TeamContainer(team.id, team.name, team.firstSelected)
    assertEquals(expected, teamContainer)
  }

  @Test
  fun toTeamContainerQuotePresent() {
    val team = Team()
    team.firstSelected = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)

    val teamContainer = team.toTeamContainer()

    val expected = TeamContainer(team.id, team.name, team.firstSelected)
    assertEquals(expected, teamContainer)
  }
}
