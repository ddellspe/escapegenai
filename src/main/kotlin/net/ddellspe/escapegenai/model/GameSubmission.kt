package net.ddellspe.escapegenai.model

import java.util.*

data class GameSubmission(
  var id: UUID,
  var password: String? = null,
  var teamWord: String? = null,
  var quote: String? = null,
  var fact: String? = null,
)
