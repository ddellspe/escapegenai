package net.ddellspe.escapegenai.model

import jakarta.persistence.*
import java.util.*

@Entity
data class Quote(
  @Id var id: UUID = UUID.randomUUID(),
  @Lob var quote: String = "",
  @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE], orphanRemoval = true)
  @JoinColumn(name = "quote_id")
  var quoteParts: MutableList<QuotePart> = mutableListOf(),
) {
  fun toQuoteContainer(): QuoteContainer {
    return QuoteContainer(id, quote)
  }
}
