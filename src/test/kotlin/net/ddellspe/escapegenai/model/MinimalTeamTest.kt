package net.ddellspe.escapegenai.model

import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MinimalTeamTest {
  @Test
  fun coverageTest() {
    val minimalTeam = MinimalTeam(UUID.randomUUID(), "test")

    assertEquals(null, minimalTeam.passwordEntered)
  }
}
