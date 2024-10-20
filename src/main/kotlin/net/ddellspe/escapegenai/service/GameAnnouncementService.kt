package net.ddellspe.escapegenai.service

import java.util.*
import net.ddellspe.escapegenai.model.GameAnnouncement
import net.ddellspe.escapegenai.repository.GameAnnouncementRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class GameAnnouncementService(var gameAnnouncementRepository: GameAnnouncementRepository) {
  fun getAllGameAnnouncements(): List<GameAnnouncement> {
    return gameAnnouncementRepository.findAll()
  }

  fun createGameAnnouncement(gameAnnouncement: GameAnnouncement): GameAnnouncement {
    return gameAnnouncementRepository.save(gameAnnouncement)
  }

  fun getGameAnnouncement(id: UUID): GameAnnouncement {
    return gameAnnouncementRepository.findByIdOrNull(id)
      ?: throw IllegalArgumentException("GameAnnouncement with id=${id} does not exist.")
  }

  fun deleteGameAnnouncement(id: UUID) {
    val gameAnnouncement = getGameAnnouncement(id)
    gameAnnouncementRepository.delete(gameAnnouncement)
  }
}
