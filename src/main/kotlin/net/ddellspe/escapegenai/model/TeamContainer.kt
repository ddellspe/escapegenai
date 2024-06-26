package net.ddellspe.escapegenai.model

import java.time.OffsetDateTime
import java.util.*

data class TeamContainer(
  var id: UUID? = null,
  var name: String,
  var firstSelected: OffsetDateTime? = null,
  var passwordId: UUID? = null,
  var passwordEntered: OffsetDateTime? = null,
  var wordId: UUID? = null,
  var wordEntered: OffsetDateTime? = null,
  var quoteId: UUID? = null,
  var quoteEntered: OffsetDateTime? = null,
  var funFactType: String? = null,
  var funFactEntered: OffsetDateTime? = null,
)
