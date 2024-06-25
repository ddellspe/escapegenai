package net.ddellspe.escapegenai.model

import java.util.*

data class QuoteContainer(
  var id: UUID? = null,
  var quote: String,
  var extendedQuote: String? = "",
  var author: String? = "",
  var authorAddress: String? = "",
  var authorTitle: String? = "",
  var company: String? = "",
  var companyAddress: String? = "",
  var companyIndustry: String? = "",
)
