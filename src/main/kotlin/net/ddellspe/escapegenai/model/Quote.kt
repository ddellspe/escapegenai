package net.ddellspe.escapegenai.model

import jakarta.persistence.*
import java.util.*

@Entity
data class Quote(
  @Id var id: UUID = UUID.randomUUID(),
  @Lob var quote: String = "",
  @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
  @JoinColumn(name = "quote_id")
  var quoteParts: List<QuotePart> = emptyList(),
) {
  fun toQuoteContainer(): QuoteContainer {
    return QuoteContainer(id, quote)
  }
}
