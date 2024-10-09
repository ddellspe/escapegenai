package net.ddellspe.escapegenai.model

import java.util.*

data class GameSubmission(
  var id: UUID,
  var highQuantity: String? = null,
  var highCost: String? = null,
  var overpaidInvoiceId: String? = null,
  var underpaidInvoiceId: String? = null,
  var overpaidEmail: String? = null,
  var underpaidEmail: String? = null,
)

data class SubmissionFeedback(var incorrect: String? = null, var correct: String? = null)

data class GameSubmissionResponse(var submission: GameSubmission, var feedback: SubmissionFeedback)

data class VerifyResponse(var verified: Boolean, var incorrectItem: String? = null)
