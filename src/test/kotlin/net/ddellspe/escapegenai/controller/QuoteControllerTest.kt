package net.ddellspe.escapegenai.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class QuoteControllerTest {
  private val quoteService: QuoteService = mockk()
  private val quoteController = QuoteController(quoteService)
  private val quote: Quote = mockk()
  private val quoteContainer: QuoteContainer = mockk()

  @Test
  fun dumbCoverageTest() {
    quoteController.quoteService = quoteService
  }

  @Test
  fun whenListQuotes_hasNoQuotes_returnEmptyList() {
    every { quoteService.getAllQuotes() } returns emptyList()

    val result = quoteController.getQuotes()

    verify(exactly = 1) { quoteService.getAllQuotes() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(emptyList<QuoteContainer>(), result.body)
  }

  @Test
  fun whenListQuotes_hasQuotes_returnListOfQuoteContainers() {
    every { quoteService.getAllQuotes() } returns listOf(quote)
    every { quote.toQuoteContainer() } returns quoteContainer

    val result = quoteController.getQuotes()

    verify(exactly = 1) { quoteService.getAllQuotes() }
    verify(exactly = 1) { quote.toQuoteContainer() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(1, result.body?.size)
    assertEquals(quoteContainer, result.body?.first())
  }

  @Test
  fun whenCreateQuote_hasError_returnErrorInResponse() {
    every { quoteService.createQuote(quoteContainer) } throws IllegalArgumentException("Error")

    val result = quoteController.createQuote(quoteContainer)

    verify(exactly = 1) { quoteService.createQuote(quoteContainer) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(quoteContainer, result.body?.quoteContainer)
    assertEquals(mapOf("error" to true, "message" to "Error"), result.body?.error)
  }

  @Test
  fun whenCreateQuote_hasNoError_returnNoErrorInResponse() {
    every { quoteService.createQuote(quoteContainer) } returns quoteContainer

    val result = quoteController.createQuote(quoteContainer)

    verify(exactly = 1) { quoteService.createQuote(quoteContainer) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(quoteContainer, result.body?.quoteContainer)
    assertEquals(null, result.body?.error)
  }

  @Test
  fun whenUpdateQuote_hasError_returnErrorInResponse() {
    every { quoteService.updateQuote(quoteContainer) } throws IllegalArgumentException("Error")

    val result = quoteController.updateQuote(quoteContainer)

    verify(exactly = 1) { quoteService.updateQuote(quoteContainer) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(quoteContainer, result.body?.quoteContainer)
    assertEquals(mapOf("error" to true, "message" to "Error"), result.body?.error)
  }

  @Test
  fun whenUpdateQuote_hasNoError_returnNoErrorInResponse() {
    every { quoteService.updateQuote(quoteContainer) } returns quoteContainer

    val result = quoteController.updateQuote(quoteContainer)

    verify(exactly = 1) { quoteService.updateQuote(quoteContainer) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(quoteContainer, result.body?.quoteContainer)
    assertEquals(null, result.body?.error)
  }
}
