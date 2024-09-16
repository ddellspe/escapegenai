package net.ddellspe.escapegenai.controller

import io.mockk.*
import java.util.*
import net.ddellspe.escapegenai.model.*
import net.ddellspe.escapegenai.service.TeamService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class TeamControllerTest {
  private val team: Team = mockk()
  private val teamContainer: TeamContainer = mockk()
  private val teamService: TeamService = mockk()
  private val id = UUID.randomUUID()
  private val teamController: TeamController = TeamController(teamService)

  @Test
  fun dumbCoverageTest() {
    teamController.teamService = teamService
  }

  @Test
  fun whenGetTeamDetails_hasTeam_thenListOfTeam() {
    every { teamService.getAllTeams() } returns listOf(team)

    val result: ResponseEntity<List<Team>> = teamController.getTeamDetails()

    verify(exactly = 1) { teamService.getAllTeams() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(team, result.body?.get(0))
  }

  @Test
  fun whenGetTeams_hasTeam_thenListOfTeam() {
    every { teamService.getAllTeams() } returns listOf(team)
    every { team.toTeamContainer() } returns teamContainer

    val result: ResponseEntity<List<TeamContainer>> = teamController.getTeams()

    verify(exactly = 1) { team.toTeamContainer() }
    verify(exactly = 1) { teamService.getAllTeams() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(teamContainer, result.body?.get(0))
  }

  @Test
  fun whenCreateTeam_hasValue_thenReturnTeam() {
    every { teamService.createTeam(teamContainer) } returns team
    every { team.toTeamContainer() } returns teamContainer

    val result: ResponseEntity<TeamContainerWithError> = teamController.createTeam(teamContainer)

    verify(exactly = 1) { teamService.createTeam(teamContainer) }
    verify(exactly = 1) { team.toTeamContainer() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertEquals(null, result.body?.error)
  }

  @Test
  fun whenCreateTeam_hasValueThrowsException_thenReturnError() {
    every { teamService.createTeam(teamContainer) } throws
      IllegalArgumentException("Team with id=123456 already exists, use update instead.")

    val result: ResponseEntity<TeamContainerWithError> = teamController.createTeam(teamContainer)

    verify(exactly = 1) { teamService.createTeam(teamContainer) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertEquals(
      mapOf(
        "error" to true,
        "message" to "Team with id=123456 already exists, use update instead.",
      ),
      result.body?.error,
    )
  }

  @Test
  fun whenUpdateTeam_hasIdNull_thenReturnError() {
    every { teamContainer.id } returns null

    val result: ResponseEntity<TeamContainerWithError> = teamController.updateTeam(teamContainer)

    verify(exactly = 1) { teamContainer.id }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(teamContainer, result.body?.teamContainer)
    assertEquals(
      mapOf("error" to true, "message" to "Team has no id present, please use create instead."),
      result.body?.error,
    )
  }

  @Test
  fun whenDeleteTeam_hasErrorDeleting_thenReturnError() {
    every { teamService.deleteTeam(id) } throws IllegalArgumentException("Error")

    val result: ResponseEntity<Map<String, Any>> = teamController.deleteTeam(id)

    verify(exactly = 1) { teamService.deleteTeam(id) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(mapOf("error" to true, "message" to "Error"), result.body)
  }

  @Test
  fun whenDeleteTeam_hasNoErrorDeleting_thenReturnSuccess() {
    every { teamService.deleteTeam(id) } just runs

    val result: ResponseEntity<Map<String, Any>> = teamController.deleteTeam(id)

    verify(exactly = 1) { teamService.deleteTeam(id) }
    assertNull(result.body)
    assertEquals(HttpStatus.NO_CONTENT, result.statusCode)
  }
}
