package net.ddellspe.escapegenai.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.ddellspe.escapegenai.model.MinimalTeam
import net.ddellspe.escapegenai.model.Team
import net.ddellspe.escapegenai.service.TeamService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class TeamControllerTest {
  private val team: Team = mockk()
  private val minimalTeam: MinimalTeam = mockk()
  private val teamService: TeamService = mockk()
  private val teamController: TeamController = TeamController(teamService)

  @Test
  fun dumbCoverageTest() {
    teamController.teamService = teamService
  }

  @Test
  fun whenGetTeams_hasTeam_thenListOfTeam() {
    every { teamService.getAllTeams() } returns listOf(team)
    every { team.toMinimalTeam() } returns minimalTeam

    val result: ResponseEntity<List<MinimalTeam>> = teamController.getTeams()

    verify(exactly = 1) { team.toMinimalTeam() }
    verify(exactly = 1) { teamService.getAllTeams() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(minimalTeam, result.body?.get(0))
  }

  @Test
  fun whenCreateTeam_hasValue_thenReturnTeam() {
    every { teamService.createTeam("test") } returns team

    val result: ResponseEntity<Team> = teamController.createTeam("test")

    verify(exactly = 1) { teamService.createTeam("test") }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(team, result.body)
  }
}
