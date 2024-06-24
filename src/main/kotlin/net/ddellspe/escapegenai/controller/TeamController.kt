package net.ddellspe.escapegenai.controller

import net.ddellspe.escapegenai.model.MinimalTeam
import net.ddellspe.escapegenai.model.Team
import net.ddellspe.escapegenai.model.TeamContainer
import net.ddellspe.escapegenai.model.TeamContainerWithError
import net.ddellspe.escapegenai.service.PasswordService
import net.ddellspe.escapegenai.service.QuoteService
import net.ddellspe.escapegenai.service.TeamService
import net.ddellspe.escapegenai.service.TeamWordService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class TeamController(
  var teamService: TeamService,
  var passwordService: PasswordService,
  var teamWordService: TeamWordService,
  var quoteService: QuoteService,
) {

  @GetMapping("/team_details")
  fun getTeamDetails(): ResponseEntity<List<Team>> {
    val teams: List<Team> = teamService.getAllTeams()
    return ResponseEntity.ok(teams)
  }

  @GetMapping("/teams")
  fun getTeams(): ResponseEntity<List<MinimalTeam>> {
    val teams: List<MinimalTeam> =
      teamService.getAllTeams().stream().map { t -> t.toMinimalTeam() }.toList()
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
    } else if (teamContainer.passwordId == null) {
      errorMap["error"] = true
      errorMap["message"] =
        "Team has no passwordId present, please use create to generate a password."
    } else if (teamContainer.wordId == null) {
      errorMap["error"] = true
      errorMap["message"] = "Team has no wordId present, please use create to generate a word."
    } else {
      val team: Team
      try {
        team = teamService.getTeam(teamContainer.id!!)
        if (team.password.id != teamContainer.passwordId!!) {
          try {
            team.password = passwordService.getPassword(teamContainer.passwordId!!)
            team.passwordEntered = null
          } catch (e: IllegalArgumentException) {
            errorMap["error"] = true
            errorMap["message"] =
              "Password with id=${teamContainer.passwordId} not found, please create a new team " +
                "to generate a new password."
          }
        }
        if (errorMap.isEmpty() && team.word.id != teamContainer.wordId!!) {
          try {
            team.word = teamWordService.getTeamWord(teamContainer.wordId!!)
            team.wordEntered = null
          } catch (e: IllegalArgumentException) {
            errorMap["error"] = true
            errorMap["message"] =
              "Word with id=${teamContainer.wordId} not found, please create a new team to " +
                "generate a new word."
          }
        }
        if (
          errorMap.isEmpty() &&
            teamContainer.quoteId != null &&
            team.quote?.id != teamContainer.quoteId
        ) {
          try {
            team.quote = quoteService.getQuote(teamContainer.quoteId!!)
            team.quoteEntered = null
          } catch (e: IllegalArgumentException) {
            errorMap["error"] = true
            errorMap["message"] =
              "Quote with id=${teamContainer.quoteId} not found, please create a new quote " +
                "first, then associate it with the team."
          }
        }
        if (errorMap.isEmpty()) {
          try {
            val updatedTeam: Team = teamService.updateTeam(team)
            return ResponseEntity.ok(TeamContainerWithError(updatedTeam.toTeamContainer(), null))
          } catch (e: IllegalArgumentException) {
            errorMap["error"] = true
            errorMap["message"] = e.message!!
          }
        }
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
}
