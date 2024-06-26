package net.ddellspe.escapegenai.model

import java.util.*

data class GameTeam(
  val id: UUID,
  var name: String,
  var passwordId: UUID,
  var wordId: UUID,
  var quoteId: UUID?,
  var quotePartIDs: List<UUID>?,
  var funFactType: String,
)
