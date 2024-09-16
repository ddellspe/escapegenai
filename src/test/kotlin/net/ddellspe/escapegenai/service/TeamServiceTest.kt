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
      assertThrows<IllegalArgumentException> { teamService.verifyTeamOpened(id) }

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    assertEquals("Team with id=${id} does not exist.", exception.message)
  }

  @Test
  fun whenVerifyTeamOpened_hasTeam_thenExpectAppropriateMockCalls() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { teamRepository.save(team) } returns team
    every { team.firstSelected } returns null
    every { team.firstSelected = capture(dateSlot) } just runs

    teamService.verifyTeamOpened(id)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { teamRepository.save(team) }
    verify(exactly = 1) { team.firstSelected }
    verify(exactly = 1) { team.firstSelected = any<OffsetDateTime>() }
    assertEquals(true, dateSlot.captured.isBefore(OffsetDateTime.now()))
  }

  @Test
  fun whenVerifyTeamOpened_hasTeamDateAlreadySet_thenExpectAppropriateMockCalls() {
    val dt = OffsetDateTime.now()
    every { teamRepository.findByIdOrNull(id) } returns team
    every { teamRepository.save(team) } returns team
    every { team.firstSelected } returns dt
    every { team.firstSelected = dt } just runs

    teamService.verifyTeamOpened(id)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { teamRepository.save(team) }
    verify(exactly = 1) { team.firstSelected }
    verify(exactly = 1) { team.firstSelected = dt }
  }
}
