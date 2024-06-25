package net.ddellspe.escapegenai.model

import jakarta.persistence.*
import java.util.*

@Entity
data class Quote(
  @Id var id: UUID = UUID.randomUUID(),
  @Lob var quote: String = "",
  @Lob var extendedQuote: String = "",
  @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE], orphanRemoval = true)
  @JoinColumn(name = "quote_id")
  var quoteParts: MutableList<QuotePart> = mutableListOf(),
  var author: String = "",
  var authorAddress: String = "",
  var authorTitle: String = "",
  var company: String = "",
  var companyAddress: String = "",
  var companyIndustry: String = "",
) {
  fun toQuoteContainer(): QuoteContainer {
    return QuoteContainer(
      id,
      quote,
      extendedQuote,
      author,
      authorAddress,
      authorTitle,
      company,
      companyAddress,
      companyIndustry,
    )
  }
}
