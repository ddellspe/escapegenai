package net.ddellspe.escapegenai.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import net.ddellspe.escapegenai.model.QuotePart
import net.ddellspe.escapegenai.repository.QuotePartRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

class QuotePartServiceTest {
  private val quotePartRepository: QuotePartRepository = mockk()
  private val quotePartService = QuotePartService(quotePartRepository)
  private val quotePart: QuotePart = mockk()
  private val id = UUID.randomUUID()

  @Test
  fun dumbCoverageTest() {
    quotePartService.quotePartRepository = quotePartRepository
  }

  @Test
  fun whenGetQuotePart_hasNoQuotePart_thenExpectException() {
    every { quotePartRepository.findByIdOrNull(id) } returns null

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { quotePartService.getQuotePart(id) }

    verify(exactly = 1) { quotePartRepository.findByIdOrNull(id) }
    assertEquals("QuotePart with id=${id} does not exist.", exception.message)
  }

  @Test
  fun whenGetQuotePart_hasQuotePart_thenReturnsQuotePart() {
    every { quotePartRepository.findByIdOrNull(id) } returns quotePart

    val result: QuotePart = quotePartService.getQuotePart(id)

    verify(exactly = 1) { quotePartRepository.findByIdOrNull(id) }
    assertEquals(quotePart, result)
  }
}
