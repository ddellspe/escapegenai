package net.ddellspe.escapegenai.service

import io.mockk.*
import java.util.*
import net.ddellspe.escapegenai.model.Quote
import net.ddellspe.escapegenai.model.QuoteContainer
import net.ddellspe.escapegenai.model.QuotePart
import net.ddellspe.escapegenai.repository.QuoteRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

class QuoteServiceTest {
  private val quoteRepository: QuoteRepository = mockk()
  private val quoteService = QuoteService(quoteRepository)
  private val quoteSlot = slot<Quote>()
  private val quote: Quote = mockk()
  private val id = UUID.randomUUID()

  @Test
  fun dumbCoverageTest() {
    quoteService.quoteRepository = quoteRepository
  }

  @Test
  fun whenGetQuote_hasNoQuote_thenExpectException() {
    every { quoteRepository.findByIdOrNull(id) } returns null

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { quoteService.getQuote(id) }

    verify(exactly = 1) { quoteRepository.findByIdOrNull(id) }
    assertEquals("Quote with id=${id} does not exist.", exception.message)
  }

  @Test
  fun whenGetQuote_hasQuote_thenReturnsQuote() {
    every { quoteRepository.findByIdOrNull(id) } returns quote

    val result: Quote = quoteService.getQuote(id)

    verify(exactly = 1) { quoteRepository.findByIdOrNull(id) }
    assertEquals(quote, result)
  }

  @Test
  fun whenCreateQuote_hasQuoteIdWithQuote_thenExpectException() {
    every { quoteRepository.findByIdOrNull(id) } returns quote
    val quoteContainer = QuoteContainer(id, "quote")

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { quoteService.createQuote(quoteContainer) }

    verify(exactly = 1) { quoteRepository.findByIdOrNull(id) }
    assertEquals("Quote with id=${id} already exists, please update.", exception.message)
  }

  @Test
  fun whenCreateQuote_hasQuoteIdNotInRepository_thenAddAndReturnQuote() {
    val quoteContainer = QuoteContainer(id, "quote")
    every { quoteRepository.findByIdOrNull(id) } returns null
    every { quoteRepository.save(capture(quoteSlot)) } returns quote
    every { quote.toQuoteContainer() } returns quoteContainer

    val result: QuoteContainer = quoteService.createQuote(quoteContainer)

    verify(exactly = 1) { quoteRepository.findByIdOrNull(id) }
    verify(exactly = 1) { quoteRepository.save(any<Quote>()) }
    assertEquals(quoteContainer, result)
    assertEquals(id, quoteSlot.captured.id)
    assertEquals("quote", quoteSlot.captured.quote)
    assertEquals(1, quoteSlot.captured.quoteParts.size)
  }

  @Test
  fun whenCreateQuote_hasNoQuoteIdNotInRepository_thenAddAndReturnQuote() {
    val quoteContainer = QuoteContainer(null, "quote")
    every { quoteRepository.save(capture(quoteSlot)) } returns quote
    every { quote.toQuoteContainer() } returns quoteContainer

    val result: QuoteContainer = quoteService.createQuote(quoteContainer)

    verify(exactly = 1) { quoteRepository.save(any<Quote>()) }
    assertEquals(quoteContainer, result)
    assertNotEquals(id, quoteSlot.captured.id)
    assertEquals("quote", quoteSlot.captured.quote)
    assertEquals(1, quoteSlot.captured.quoteParts.size)
  }

  @Test
  fun whenUpdateQuote_hasNoQuoteIdWithNoQuote_thenExpectException() {
    val quoteContainer = QuoteContainer(null, "quote")

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { quoteService.updateQuote(quoteContainer) }

    assertEquals("Quote does not exist, please create instead.", exception.message)
  }

  @Test
  fun whenUpdateQuote_hasQuoteIdWithNoQuote_thenExpectException() {
    every { quoteRepository.findByIdOrNull(id) } returns null
    val quoteContainer = QuoteContainer(id, "quote")

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { quoteService.updateQuote(quoteContainer) }

    verify(exactly = 1) { quoteRepository.findByIdOrNull(id) }
    assertEquals("Quote with id=${id} does not exist, please create instead.", exception.message)
  }

  @Test
  fun whenUpdateQuote_hasQuoteNotTheSame_thenUpdateAndReturnQuote() {
    val quoteContainer = QuoteContainer(id, "quote")
    val quotePartCapture = slot<List<QuotePart>>()
    every { quoteRepository.findByIdOrNull(id) } returns quote
    every { quote.quote } returns "quote2"
    every { quote.quote = "quote" } just runs
    every { quote.quoteParts = capture(quotePartCapture) } just runs
    every { quoteRepository.save(quote) } returns quote
    every { quote.toQuoteContainer() } returns quoteContainer

    val result: QuoteContainer = quoteService.updateQuote(quoteContainer)

    verify(exactly = 1) { quoteRepository.findByIdOrNull(id) }
    verify(exactly = 2) { quote.quote }
    verify(exactly = 1) { quote.quote = "quote" }
    verify(exactly = 1) { quote.quoteParts = any<List<QuotePart>>() }
    verify(exactly = 1) { quoteRepository.save(quote) }
    verify(exactly = 1) { quote.toQuoteContainer() }
    assertEquals(quoteContainer, result)
    assertEquals(1, quotePartCapture.captured.size)
  }

  @Test
  fun whenUpdateQuote_hasQuoteTheSame_thenReturnQuote() {
    val quoteContainer = QuoteContainer(id, "quote")
    every { quoteRepository.findByIdOrNull(id) } returns quote
    every { quote.quote } returns "quote"
    every { quoteRepository.save(quote) } returns quote
    every { quote.toQuoteContainer() } returns quoteContainer

    val result: QuoteContainer = quoteService.updateQuote(quoteContainer)

    verify(exactly = 1) { quoteRepository.findByIdOrNull(id) }
    verify(exactly = 1) { quote.quote }
    verify(exactly = 1) { quoteRepository.save(quote) }
    verify(exactly = 1) { quote.toQuoteContainer() }
    assertEquals(quoteContainer, result)
  }
}
