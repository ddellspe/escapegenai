package net.ddellspe.escapegenai.model

import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GameSubmissionTest {

  @Test
  fun coverageTest() {
    val gameSubmission = GameSubmission(UUID.randomUUID())

    assertEquals(null, gameSubmission.fact)
  }
}
