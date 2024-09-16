package net.ddellspe.escapegenai.controller

import com.itextpdf.html2pdf.HtmlConverter
import java.io.ByteArrayOutputStream
import java.util.*
import net.ddellspe.escapegenai.config.EscapeGenAIProperties
import net.ddellspe.escapegenai.model.GameSubmission
import net.ddellspe.escapegenai.model.MinimalTeam
import net.ddellspe.escapegenai.service.TeamService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/game")
@EnableConfigurationProperties(EscapeGenAIProperties::class)
class GameDataController(var teamService: TeamService, var quotePartService: QuotePartService) {

  @Autowired private var props: EscapeGenAIProperties = EscapeGenAIProperties()

  @GetMapping("/teams")
  fun getTeams(): ResponseEntity<List<MinimalTeam>> {
    val teams: List<MinimalTeam> =
      teamService.getAllTeams().stream().map { t -> t.toMinimalTeam() }.toList()
    return ResponseEntity.ok(teams)
  }

  @PostMapping("/submit")
  fun submitGameState(@RequestBody gameSubmission: GameSubmission): ResponseEntity<GameSubmission> {
    try {
      val team = teamService.getTeam(gameSubmission.id)
      teamService.verifyTeamOpened(team.id)
      val returnSubmission = GameSubmission(team.id)
      return ResponseEntity(returnSubmission, HttpStatus.OK)
    } catch (e: IllegalArgumentException) {
      return ResponseEntity(GameSubmission(gameSubmission.id), HttpStatus.BAD_REQUEST)
    }
  }
}
