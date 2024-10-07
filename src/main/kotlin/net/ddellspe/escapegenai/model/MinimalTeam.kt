package net.ddellspe.escapegenai.model

import java.time.OffsetDateTime
import java.util.*

data class MinimalTeam(
  var id: UUID,
  var name: String,
  var firstSelected: OffsetDateTime? = null,
  var productsIdentified: OffsetDateTime? = null,
  var leakageIdentified: OffsetDateTime? = null,
  var suppliersContacted: OffsetDateTime? = null,
  var primaryInvoiceId: UUID,
  var invoiceIds: List<UUID>,
)
