package net.ddellspe.escapegenai.model

import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class QuoteTest {
  @Test
  fun toQuoteContainerTest() {
    val quote = Quote(UUID.randomUUID(), "quote")

    val quoteContainer = QuoteContainer(quote.id, "quote")

    assertEquals(quoteContainer, quote.toQuoteContainer())
  }
}
