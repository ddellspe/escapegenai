package net.ddellspe.escapegenai.controller

import io.mockk.*
import java.util.*
import net.ddellspe.escapegenai.model.*
import net.ddellspe.escapegenai.service.TeamService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GameDataControllerTest {
  private val teamService: TeamService = mockk()
  private val team: Team = mockk()
  private val minimalTeam: MinimalTeam = mockk()
  private val gameSubmission: GameSubmission = mockk()
  private val id = UUID.randomUUID()
  private val gameDataController = GameDataController(teamService)

  @Test
  fun dumbCoverageTests() {
    gameDataController.teamService = teamService
  }

  @Test
  fun whenGetTeams_hasNoTeams_thenNoTeamsPresent() {
    every { teamService.getAllTeams() } returns emptyList()

    val result = gameDataController.getTeams()

    verify(exactly = 1) { teamService.getAllTeams() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(0, result.body?.size)
  }

  @Test
  fun whenGetTeams_hasTeams_thenTeamsPresent() {
    every { teamService.getAllTeams() } returns listOf(team)
    every { team.toMinimalTeam() } returns minimalTeam

    val result = gameDataController.getTeams()

    verify(exactly = 1) { teamService.getAllTeams() }
    verify(exactly = 1) { team.toMinimalTeam() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(1, result.body?.size)
    assertEquals(minimalTeam, result.body?.get(0))
  }

  @Test
  fun whenSubmitGameState_hasNoTeam_thenExpectBadRequest() {
    every { teamService.getTeam(id) } throws IllegalArgumentException("No Team")
    every { gameSubmission.id } returns id

    val result = gameDataController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id)

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 2) { gameSubmission.id }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(expectedBody, result.body)
  }
}
