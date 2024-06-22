package net.ddellspe.escapegenai.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TeamContainerTest {
  @Test
  fun coverageTest() {
    val teamContainer = TeamContainer(name = "Test Team")

    assertEquals(null, teamContainer.passwordId)
  }
}
