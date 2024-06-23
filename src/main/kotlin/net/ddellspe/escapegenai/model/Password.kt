package net.ddellspe.escapegenai.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.*
import net.ddellspe.escapegenai.util.passwordGenerator
import net.ddellspe.escapegenai.util.passwordPageGenerator

@Entity
data class Password(
  @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID = UUID.randomUUID(),
  var password: String = passwordGenerator(),
  var pageContent: String = passwordPageGenerator(password),
)
