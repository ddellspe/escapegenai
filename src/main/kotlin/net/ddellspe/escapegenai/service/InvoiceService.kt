package net.ddellspe.escapegenai.service

import net.ddellspe.escapegenai.model.Invoice
import net.ddellspe.escapegenai.model.Invoice.InvoiceConstants.MAX_INVOICE_PRODUCT_COUNT
import net.ddellspe.escapegenai.model.Invoice.InvoiceConstants.MIN_INVOICE_PRODUCT_COUNT
import net.ddellspe.escapegenai.model.InvoiceProduct
import net.ddellspe.escapegenai.model.Product.ProductConstants.MAX_PRODUCT_COUNT
import net.ddellspe.escapegenai.repository.InvoiceRepository
import net.ddellspe.escapegenai.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class InvoiceService(
  var invoiceRepository: InvoiceRepository,
  var productRepository: ProductRepository,
) {
  fun getAllInvoices(): List<Invoice> {
    return invoiceRepository.findAll()
  }

  fun createNewInvoice(difference: Int = 0): Invoice {
    var invoice = Invoice(difference = difference)
    val productCount = (MIN_INVOICE_PRODUCT_COUNT..MAX_INVOICE_PRODUCT_COUNT).random()
    val productIds = HashSet<Int>()
    while (productIds.size < productCount) {
      productIds.add((1..MAX_PRODUCT_COUNT).random())
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

  fun getInvoice(id: Long): Invoice {
    return invoiceRepository.findByIdOrNull(id)
      ?: throw IllegalArgumentException("Invoice with id=${id} does not exist.")
  }
}
