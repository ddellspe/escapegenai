package net.ddellspe.escapegenai.controller

import java.util.*
import net.ddellspe.escapegenai.model.MinimalTeam
import net.ddellspe.escapegenai.service.QuotePartService
import net.ddellspe.escapegenai.service.TeamService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/game")
class GameController(var teamService: TeamService, var quotePartService: QuotePartService) {
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
              .map { q -> mapOf("href" to "https://escapegenai.com/game/quotePart/${q}") }
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
}
