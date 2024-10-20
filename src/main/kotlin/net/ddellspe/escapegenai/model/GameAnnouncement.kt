package net.ddellspe.escapegenai.model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "game_announcement")
data class GameAnnouncement(
  @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID = UUID.randomUUID(),
  var message: String,
  var link: String? = null,
  var linkText: String? = null,
)
