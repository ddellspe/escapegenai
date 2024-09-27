package net.ddellspe.escapegenai.service

import net.ddellspe.escapegenai.model.Invoice
import net.ddellspe.escapegenai.model.InvoiceProduct
import net.ddellspe.escapegenai.model.Product
import net.ddellspe.escapegenai.repository.InvoiceProductRepository
import org.springframework.stereotype.Service

@Service
class InvoiceProductService(var invoiceProductRepository: InvoiceProductRepository) {
  fun getAllInvoiceProducts(): List<InvoiceProduct> {
    return invoiceProductRepository.findAll()
  }

  fun createInvoiceProductForInvoiceAndProduct(invoice: Invoice, product: Product): InvoiceProduct {
    return invoiceProductRepository.save(InvoiceProduct(invoice = invoice, product = product))
  }

  fun createInvoiceProduct(invoiceProduct: InvoiceProduct): InvoiceProduct {
    return invoiceProductRepository.save(invoiceProduct)
  }
}
