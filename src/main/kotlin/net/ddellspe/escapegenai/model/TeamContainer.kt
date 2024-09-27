package net.ddellspe.escapegenai.model

import java.time.OffsetDateTime
import java.util.*

data class TeamContainer(
  var id: UUID? = null,
  var name: String,
  var firstSelected: OffsetDateTime? = null,
  var primaryInvoiceId: Long? = null,
)
