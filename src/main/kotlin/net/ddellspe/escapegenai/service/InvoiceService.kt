package net.ddellspe.escapegenai.service

import kotlin.random.Random
import net.ddellspe.escapegenai.model.Invoice
import net.ddellspe.escapegenai.model.InvoiceProduct
import net.ddellspe.escapegenai.repository.InvoiceRepository
import net.ddellspe.escapegenai.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class InvoiceService(
  var invoiceRepository: InvoiceRepository,
  var productRepository: ProductRepository,
) {
  fun getAllInvoices(): List<Invoice> {
    return invoiceRepository.findAll()
  }

  fun createNewInvoice(): Invoice {
    var invoice = Invoice()
    val productCount = Random.nextInt(3, 8)
    val productIds = HashSet<Int>()
    while (productIds.size < productCount) {
      productIds.add(Random.nextInt(1, 20))
    }
    invoice = invoiceRepository.save(invoice)
    val invoiceProducts = invoice.invoiceProducts
    for (productId in productIds) {
      invoiceProducts.add(
        InvoiceProduct(invoice = invoice, product = productRepository.findById(productId).get())
      )
    }
    return invoiceRepository.save(invoice)
  }

  fun createInvoice(invoice: Invoice): Invoice {
    return invoiceRepository.save(invoice)
  }

  fun updateInvoice(invoice: Invoice): Invoice {
    return invoiceRepository.save(invoice)
  }
}
