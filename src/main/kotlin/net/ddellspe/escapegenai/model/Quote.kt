package net.ddellspe.escapegenai.model

import jakarta.persistence.*
import java.util.*
import net.ddellspe.escapegenai.util.generateParts

@Entity
data class Quote(
  @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID = UUID.randomUUID(),
  @Lob var quote: String = "",
  @OneToMany(fetch = FetchType.LAZY) var quoteParts: List<QuotePart> = generateParts(quote),
)
