package net.ddellspe.escapegenai.controller

import net.ddellspe.escapegenai.model.QuoteContainer
import net.ddellspe.escapegenai.model.QuoteContainerWithError
import net.ddellspe.escapegenai.service.QuoteService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class QuoteController(var quoteService: QuoteService) {

  @GetMapping("/quotes")
  fun getQuotes(): ResponseEntity<List<QuoteContainer>> {
    val quotes = quoteService.getAllQuotes().map { q -> q.toQuoteContainer() }.toList()
    return ResponseEntity(quotes, HttpStatus.OK)
  }

  @PostMapping("/quotes")
  fun createQuote(quoteContainer: QuoteContainer): ResponseEntity<QuoteContainerWithError> {
    try {
      val quote = quoteService.createQuote(quoteContainer)
      return ResponseEntity.ok(QuoteContainerWithError(quote, null))
    } catch (e: IllegalArgumentException) {
      val errorMap: MutableMap<String, Any> = HashMap()
      errorMap["error"] = true
      errorMap["message"] = e.message!!
      return ResponseEntity(
          QuoteContainerWithError(quoteContainer = quoteContainer, error = errorMap),
          HttpStatus.BAD_REQUEST,
      )
    }
  }

  @PutMapping("/quotes")
  fun updateQuote(quoteContainer: QuoteContainer): ResponseEntity<QuoteContainerWithError> {
    try {
      val quote = quoteService.updateQuote(quoteContainer)
      return ResponseEntity.ok(QuoteContainerWithError(quote, null))
    } catch (e: IllegalArgumentException) {
      val errorMap: MutableMap<String, Any> = HashMap()
      errorMap["error"] = true
      errorMap["message"] = e.message!!
      return ResponseEntity(
          QuoteContainerWithError(quoteContainer = quoteContainer, error = errorMap),
          HttpStatus.BAD_REQUEST,
      )
    }
  }
}
