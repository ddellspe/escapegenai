package net.ddellspe.escapegenai.model

import java.util.*

data class GameSubmission(
  var id: UUID,
  var highQuantity: String? = null,
  var highCost: String? = null,
  var overpaidInvoiceId: String? = null,
  var underpaidInvoiceId: String? = null,
  var overpaidEmail: String? = null,
  var underpaidEmail: String? = null,
)
