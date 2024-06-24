package net.ddellspe.escapegenai.model

class QuoteContainerWithError(
  var quoteContainer: QuoteContainer? = null,
  var error: Map<String, Any>? = null,
) {}
