package net.ddellspe.escapegenai.model

import java.util.*

data class InvoiceDocument(
  var id: Long,
  var teamName: String,
  var company: String,
  var address: String,
  var total: Long,
  var products: List<InvoiceProductDocument>,
)

data class InvoiceProductDocument(
  var id: UUID,
  var name: String,
  var qty: Int,
  var price: Int,
  var total: Int,
)
