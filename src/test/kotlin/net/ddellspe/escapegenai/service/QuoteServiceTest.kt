package net.ddellspe.escapegenai.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import net.ddellspe.escapegenai.model.Quote
import net.ddellspe.escapegenai.repository.QuoteRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

class QuoteServiceTest {
  private val quoteRepository: QuoteRepository = mockk()
  private val quoteService = QuoteService(quoteRepository)
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
}
