package net.ddellspe.escapegenai.model

import java.time.OffsetDateTime
import java.util.*

data class MinimalTeam(
  var id: UUID,
  var name: String,
  var passwordEntered: OffsetDateTime? = null,
  var wordEntered: OffsetDateTime? = null,
  var quoteEntered: OffsetDateTime? = null,
  var funFactType: String? = null,
  var funFactEntered: OffsetDateTime? = null,
)
