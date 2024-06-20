package net.ddellspe.escapegenai.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Lob
import java.util.*
import net.ddellspe.escapegenai.util.generateContent
import net.ddellspe.escapegenai.util.generateRandomBs

@Entity
data class QuotePart(
  @Id var id: UUID = UUID.randomUUID(),
  var part: String = generateRandomBs(),
  @Lob var generatedContent: String = generateContent(part),
)
