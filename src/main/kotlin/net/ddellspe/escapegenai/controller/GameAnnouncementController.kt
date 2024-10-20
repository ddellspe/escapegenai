package net.ddellspe.escapegenai.controller

import java.util.*
import net.ddellspe.escapegenai.model.GameAnnouncement
import net.ddellspe.escapegenai.service.GameAnnouncementService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class GameAnnouncementController(var gameAnnouncementService: GameAnnouncementService) {
  @GetMapping("/announcements")
  fun getAnnouncements(): ResponseEntity<List<GameAnnouncement>> {
    return ResponseEntity.ok(gameAnnouncementService.getAllGameAnnouncements())
  }

  @PostMapping("/announcements")
  fun createAnnouncement(
    @RequestBody gameAnnouncement: GameAnnouncement
  ): ResponseEntity<GameAnnouncement> {
    val newGameAnnouncement =
      GameAnnouncement(
        message = gameAnnouncement.message,
        link = if (gameAnnouncement.link?.isBlank() == true) null else gameAnnouncement.link,
        linkText =
          if (gameAnnouncement.linkText?.isBlank() == true) null else gameAnnouncement.linkText,
      )
    return ResponseEntity.ok(gameAnnouncementService.createGameAnnouncement(newGameAnnouncement))
  }

  @DeleteMapping("/announcements/{id}")
  fun deleteAnnouncement(@PathVariable id: UUID): ResponseEntity<Map<String, Any>> {
    try {
      gameAnnouncementService.deleteGameAnnouncement(id)
      return ResponseEntity(null, HttpStatus.NO_CONTENT)
    } catch (e: IllegalArgumentException) {
      return ResponseEntity(
        mapOf<String, Any>("error" to true, "message" to e.message!!),
        HttpStatus.BAD_REQUEST,
      )
    }
  }
}
