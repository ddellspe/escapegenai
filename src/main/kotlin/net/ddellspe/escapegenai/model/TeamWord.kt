package net.ddellspe.escapegenai.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Lob
import java.util.*
import net.ddellspe.escapegenai.util.generateContent
import net.ddellspe.escapegenai.util.generateRandomBs

@Entity
data class TeamWord(
  @Id var id: UUID = UUID.randomUUID(),
  var word: String = generateRandomBs(),
  @Lob var generatedContent: String = generateContent(word, 1000),
)
