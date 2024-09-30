package net.ddellspe.escapegenai.controller

import java.util.*
import net.ddellspe.escapegenai.model.Team
import net.ddellspe.escapegenai.model.TeamContainer
import net.ddellspe.escapegenai.model.TeamContainerWithError
import net.ddellspe.escapegenai.service.TeamService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class TeamController(var teamService: TeamService) {

  @GetMapping("/team_details")
  fun getTeamDetails(): ResponseEntity<List<Team>> {
    val teams: List<Team> = teamService.getAllTeams()
    return ResponseEntity.ok(teams)
  }

  @GetMapping("/teams")
  fun getTeams(): ResponseEntity<List<TeamContainer>> {
    val teams: List<TeamContainer> =
      teamService.getAllTeams().map { t -> t.toTeamContainer() }.toList()
    return ResponseEntity.ok(teams)
  }

  @PostMapping("/teams")
  fun createTeam(
    @RequestBody teamContainer: TeamContainer
  ): ResponseEntity<TeamContainerWithError> {
    try {
      val team: Team = teamService.createTeam(teamContainer)
      return ResponseEntity.ok(TeamContainerWithError(team.toTeamContainer(), null))
    } catch (e: IllegalArgumentException) {
      val errorMap: MutableMap<String, Any> = HashMap()
      errorMap["error"] = true
      errorMap["message"] = e.message!!
      return ResponseEntity(
        TeamContainerWithError(teamContainer = teamContainer, error = errorMap),
        HttpStatus.BAD_REQUEST,
      )
    }
  }

  @PutMapping("/teams")
  fun updateTeam(
    @RequestBody teamContainer: TeamContainer
  ): ResponseEntity<TeamContainerWithError> {
    val errorMap: MutableMap<String, Any> = HashMap()
    if (teamContainer.id == null) {
      errorMap["error"] = true
      errorMap["message"] = "Team has no id present, please use create instead."
    } else {
      val team: Team
      try {
        team = teamService.getTeam(teamContainer.id!!)
        if (teamContainer.name != team.name) {
          team.name = teamContainer.name
        }
        val updatedTeam = teamService.updateTeam(team)
        return ResponseEntity.ok(TeamContainerWithError(updatedTeam.toTeamContainer()))
      } catch (e: IllegalArgumentException) {
        errorMap["error"] = true
        errorMap["message"] =
          "Team with id=${teamContainer.id} not found, please use create instead."
      }
    }
    return ResponseEntity(
      TeamContainerWithError(teamContainer = teamContainer, error = errorMap),
      HttpStatus.BAD_REQUEST,
    )
  }

  @DeleteMapping("/teams/{id}")
  fun deleteTeam(@PathVariable id: UUID): ResponseEntity<Map<String, Any>> {
    try {
      teamService.deleteTeam(id)
      return ResponseEntity(null, HttpStatus.NO_CONTENT)
    } catch (e: IllegalArgumentException) {
      return ResponseEntity(
        mapOf<String, Any>("error" to true, "message" to e.message!!),
        HttpStatus.BAD_REQUEST,
      )
    }
  }
}
