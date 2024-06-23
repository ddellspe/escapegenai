package net.ddellspe.escapegenai.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import net.ddellspe.escapegenai.model.TeamWord
import net.ddellspe.escapegenai.repository.TeamWordRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

class TeamWordServiceTest {
  private val teamWordRepository: TeamWordRepository = mockk()
  private val teamWordService = TeamWordService(teamWordRepository)
  private val teamWord: TeamWord = mockk()
  private val id = UUID.randomUUID()

  @Test
  fun dumbCoverageTest() {
    teamWordService.teamWordRepository = teamWordRepository
  }

  @Test
  fun whenGetTeamWord_hasNoTeamWord_thenExpectException() {
    every { teamWordRepository.findByIdOrNull(id) } returns null

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { teamWordService.getTeamWord(id) }

    verify(exactly = 1) { teamWordRepository.findByIdOrNull(id) }
    assertEquals("TeamWord with id=${id} does not exist.", exception.message)
  }

  @Test
  fun whenGetTeamWord_hasTeamWord_thenReturnsTeamWord() {
    every { teamWordRepository.findByIdOrNull(id) } returns teamWord

    val result = teamWordService.getTeamWord(id)

    verify(exactly = 1) { teamWordRepository.findByIdOrNull(id) }
    assertEquals(teamWord, result)
  }
}
