package net.ddellspe.escapegenai.model

import java.time.OffsetDateTime
import java.util.*

data class MinimalTeam(
  var id: UUID,
  var name: String,
  var firstSelected: OffsetDateTime? = null,
)
