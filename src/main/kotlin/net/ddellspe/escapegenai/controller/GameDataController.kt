package net.ddellspe.escapegenai.controller

import com.itextpdf.html2pdf.HtmlConverter
import java.io.ByteArrayOutputStream
import java.util.*
import net.ddellspe.escapegenai.config.EscapeGenAIProperties
import net.ddellspe.escapegenai.model.GameSubmission
import net.ddellspe.escapegenai.model.MinimalTeam
import net.ddellspe.escapegenai.service.QuotePartService
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

  @GetMapping("/team/{id}/password")
  fun getTeamPassword(@PathVariable id: UUID): ResponseEntity<String> {
    try {
      val team = teamService.getTeam(id)
      val content =
        this::class
          .java
          .getResourceAsStream("/game/password_template.html")!!
          .bufferedReader()
          .readLines()
          .joinToString("\n")
      return ResponseEntity.ok()
        .contentType(MediaType.TEXT_HTML)
        .body(
          content.replace("[TEAM_NAME]", team.name).replace("[CONTENT]", team.password.pageContent)
        )
    } catch (e: IllegalArgumentException) {
      return ResponseEntity<String>(e.message!!, HttpStatus.NOT_FOUND)
    }
  }

  @GetMapping(
    "/team/{id}/password_pdf",
    produces = [MediaType.APPLICATION_PDF_VALUE, MediaType.TEXT_HTML_VALUE],
  )
  fun getTeamPasswordPDF(@PathVariable id: UUID): ResponseEntity<out Any> {
    try {
      val team = teamService.getTeam(id)
      val content =
        this::class
          .java
          .getResourceAsStream("/game/password_pdf_template.html")!!
          .bufferedReader()
          .readLines()
          .joinToString("\n")
      val targetOutputStream = ByteArrayOutputStream()
      HtmlConverter.convertToPdf(
        content.replace("[TEAM_NAME]", team.name).replace("[CONTENT]", team.password.pageContent),
        targetOutputStream,
      )
      val headers = HttpHeaders()
      headers.add("Content-Disposition", "inline; filename=${team.name} Password.pdf")
      return ResponseEntity.ok()
        .headers(headers)
        .contentType(MediaType.APPLICATION_PDF)
        .body(targetOutputStream.toByteArray())
    } catch (e: IllegalArgumentException) {
      return ResponseEntity<String>(e.message!!, HttpStatus.NOT_FOUND)
    }
  }

  @GetMapping("/team/{id}/word")
  fun getTeamWord(@PathVariable id: UUID): ResponseEntity<String> {
    try {
      val team = teamService.getTeam(id)
      val content =
        this::class
          .java
          .getResourceAsStream("/game/word_count_template.html")!!
          .bufferedReader()
          .readLines()
          .joinToString("\n")
      return ResponseEntity.ok()
        .contentType(MediaType.TEXT_HTML)
        .body(
          content.replace("[TEAM_NAME]", team.name).replace("[CONTENT]", team.word.generatedContent)
        )
    } catch (e: IllegalArgumentException) {
      return ResponseEntity<String>(e.message!!, HttpStatus.NOT_FOUND)
    }
  }

  @GetMapping("/team/{id}/quote")
  fun getQuoteParts(@PathVariable id: UUID): ResponseEntity<Map<String, Any>> {
    try {
      val team = teamService.getTeam(id)
      return ResponseEntity.ok(
        mapOf(
          "links" to
            team.quote!!
              .quoteParts
              .stream()
              .map { q -> q.id.toString() }
              .sorted()
              .map { q -> mapOf("href" to "${props.hostname}/game/quotePart/${q}") }
              .toList(),
          "otherData" to id,
        )
      )
    } catch (e: IllegalArgumentException) {
      return ResponseEntity(HttpStatus.NOT_FOUND)
    }
  }

  @GetMapping("/quotePart/{id}")
  fun getQuoteDocument(@PathVariable id: UUID): ResponseEntity<String> {
    try {
      val quotePart = quotePartService.getQuotePart(id)
      val content =
        this::class
          .java
          .getResourceAsStream("/game/word_count_template.html")!!
          .bufferedReader()
          .readLines()
          .joinToString("\n")
      return ResponseEntity.ok()
        .contentType(MediaType.TEXT_HTML)
        .body(
          content
            .replace("[TEAM_NAME]", "the quote word")
            .replace("[CONTENT]", quotePart.generatedContent)
        )
    } catch (e: IllegalArgumentException) {
      return ResponseEntity<String>(e.message!!, HttpStatus.NOT_FOUND)
    }
  }

  @PostMapping("/submit")
  fun submitGameState(@RequestBody gameSubmission: GameSubmission): ResponseEntity<GameSubmission> {
    try {
      val team = teamService.getTeam(gameSubmission.id)
      teamService.verifyTeamOpened(team.id)
      val returnSubmission = GameSubmission(team.id)
      if (
        gameSubmission.password == null ||
          !teamService.verifyTeamPassword(team.id, gameSubmission.password!!)
      ) {
        return ResponseEntity(returnSubmission, HttpStatus.OK)
      }
      returnSubmission.password = gameSubmission.password
      if (
        gameSubmission.teamWord == null ||
          !teamService.verifyTeamWord(team.id, gameSubmission.teamWord!!)
      ) {
        return ResponseEntity(returnSubmission, HttpStatus.OK)
      }
      returnSubmission.teamWord = gameSubmission.teamWord
      if (
        gameSubmission.quote == null ||
          !teamService.verifyTeamQuote(team.id, gameSubmission.quote!!)
      ) {
        return ResponseEntity(returnSubmission, HttpStatus.OK)
      }
      returnSubmission.quote = gameSubmission.quote
      if (
        gameSubmission.fact == null || !teamService.verifyFunFact(team.id, gameSubmission.fact!!)
      ) {
        return ResponseEntity(returnSubmission, HttpStatus.OK)
      }
      returnSubmission.fact = gameSubmission.fact
      return ResponseEntity(returnSubmission, HttpStatus.OK)
    } catch (e: IllegalArgumentException) {
      return ResponseEntity(GameSubmission(gameSubmission.id), HttpStatus.BAD_REQUEST)
    }
  }
}
