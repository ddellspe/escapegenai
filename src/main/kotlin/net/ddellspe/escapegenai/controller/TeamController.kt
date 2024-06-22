package net.ddellspe.escapegenai.controller

import net.ddellspe.escapegenai.model.MinimalTeam
import net.ddellspe.escapegenai.model.Team
import net.ddellspe.escapegenai.model.TeamContainer
import net.ddellspe.escapegenai.service.TeamService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class TeamController(var teamService: TeamService) {
  @GetMapping("/teams")
  fun getTeams(): ResponseEntity<List<MinimalTeam>> {
    val teams: List<MinimalTeam> =
      teamService.getAllTeams().stream().map { t -> t.toMinimalTeam() }.toList()
    return ResponseEntity(teams, HttpStatus.OK)
  }

  @PostMapping("/teams/create")
  fun createTeam(@RequestParam name: String): ResponseEntity<TeamContainer> {
    val team: Team = teamService.createTeam(name)
    return ResponseEntity(team.toTeamContainer(), HttpStatus.OK)
  }
}
