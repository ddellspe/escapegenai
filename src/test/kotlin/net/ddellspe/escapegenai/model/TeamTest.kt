package net.ddellspe.escapegenai.model

import java.time.OffsetDateTime
import java.time.ZoneOffset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TeamTest {
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
}
