package net.ddellspe.escapegenai.controller

import net.ddellspe.escapegenai.model.Invoice
import net.ddellspe.escapegenai.service.InvoiceService
import net.ddellspe.escapegenai.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class InvoiceController(var productService: ProductService, var invoiceService: InvoiceService) {
  @GetMapping("/invoices")
  fun getInvoices(): ResponseEntity<List<Invoice>> {
    val invoices: List<Invoice> = invoiceService.getAllInvoices()
    return ResponseEntity.ok(invoices)
  }

  @PostMapping("/new_invoice")
  fun createInvoice(): ResponseEntity<Invoice> {
    productService.initializeProductDatabase()
    val invoice = invoiceService.createNewInvoice()
    return ResponseEntity.ok(invoice)
  }
}
