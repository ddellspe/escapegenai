package net.ddellspe.escapegenai.controller

import io.mockk.*
import net.ddellspe.escapegenai.model.Invoice
import net.ddellspe.escapegenai.service.InvoiceService
import net.ddellspe.escapegenai.service.ProductService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class InvoiceControllerTest {
  private val productService: ProductService = mockk()
  private val invoiceService: InvoiceService = mockk()
  private val invoiceController = InvoiceController(productService, invoiceService)
  private val invoice: Invoice = mockk()

  @Test
  fun dumbCoverageTests() {
    invoiceController.invoiceService = invoiceService
    invoiceController.productService = productService
  }

  @Test
  fun whenNoInvoices_returnNoInvoices() {
    every { invoiceService.getAllInvoices() } returns emptyList()

    val result: ResponseEntity<List<Invoice>> = invoiceController.getInvoices()

    verify(exactly = 1) { invoiceService.getAllInvoices() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(0, result.body?.size)
  }

  @Test
  fun whenInvoicesPresent_returnInvoices() {
    every { invoiceService.getAllInvoices() } returns listOf(invoice)

    val result: ResponseEntity<List<Invoice>> = invoiceController.getInvoices()

    verify(exactly = 1) { invoiceService.getAllInvoices() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(1, result.body?.size)
    assertEquals(invoice, result.body?.get(0))
  }

  @Test
  fun whenCreateInvoiceWorks_returnInvoice() {
    every { productService.initializeProductDatabase() } just runs
    every { invoiceService.createNewInvoice() } returns invoice

    val result: ResponseEntity<Invoice> = invoiceController.createInvoice()

    verify(exactly = 1) { invoiceService.createNewInvoice() }
    verify(exactly = 1) { productService.initializeProductDatabase() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(invoice, result.body)
  }
}
