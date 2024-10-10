package net.ddellspe.escapegenai.service

import io.mockk.*
import java.util.*
import net.ddellspe.escapegenai.model.Invoice
import net.ddellspe.escapegenai.model.Invoice.InvoiceConstants.MAX_INVOICE_PRODUCT_COUNT
import net.ddellspe.escapegenai.model.Invoice.InvoiceConstants.MIN_INVOICE_PRODUCT_COUNT
import net.ddellspe.escapegenai.model.InvoiceProduct
import net.ddellspe.escapegenai.model.Product
import net.ddellspe.escapegenai.repository.InvoiceRepository
import net.ddellspe.escapegenai.repository.ProductRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

class InvoiceServiceTest {
  private val invoiceRepository: InvoiceRepository = mockk()
  private val productRepository: ProductRepository = mockk()
  private val productService: ProductService = mockk()
  private val invoiceService = InvoiceService(invoiceRepository, productRepository, productService)
  private val invoice: Invoice = mockk()
  private val product: Product = mockk()
  private val invoiceSlot = slot<Invoice>()
  private val invoiceProductList: MutableList<InvoiceProduct> = mockk()
  private val id = 1L

  @Test
  fun dumbCoverageTests() {
    invoiceService.invoiceRepository = invoiceRepository
    invoiceService.productRepository = productRepository
    invoiceService.productService = productService
  }

  @Test
  fun whenNoInvoices_returnNoInvoicesFound() {
    every { invoiceRepository.findAll() } returns emptyList()

    val result = invoiceService.getAllInvoices()

    verify(exactly = 1) { invoiceRepository.findAll() }
    assertEquals(0, result.size)
  }

  @Test
  fun whenInvoicesPresent_returnInvoices() {
    every { invoiceRepository.findAll() } returns listOf(invoice)

    val result = invoiceService.getAllInvoices()

    verify(exactly = 1) { invoiceRepository.findAll() }
    assertEquals(1, result.size)
    assertEquals(invoice, result.get(0))
  }

  @Test
  fun whenGetInvoiceNotFound_returnError() {
    every { invoiceRepository.findByIdOrNull(id) } returns null

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { invoiceService.getInvoice(id) }

    verify(exactly = 1) { invoiceRepository.findByIdOrNull(id) }
    assertEquals("Invoice with id=1 does not exist.", exception.message)
  }

  @Test
  fun whenGetInvoiceFound_returnsInvoice() {
    every { invoiceRepository.findByIdOrNull(id) } returns invoice

    val result = invoiceService.getInvoice(id)

    verify(exactly = 1) { invoiceRepository.findByIdOrNull(id) }
    assertEquals(invoice, result)
  }

  @Test
  fun whenCreateNewInvoiceSpecifiesNoDifference_newInvoiceCreated() {
    every { productService.initializeProductDatabase() } just runs
    every { invoiceRepository.save(capture(invoiceSlot)) } returns invoice
    every { invoiceRepository.save(invoice) } returns invoice
    every { invoice.invoiceProducts } returns invoiceProductList
    every { invoiceProductList.add(any()) } returns true
    every { productRepository.findById(any()) } returns Optional.of(product)

    val result = invoiceService.createNewInvoice()

    verify(exactly = 1) { productService.initializeProductDatabase() }
    verify(exactly = 2) { invoiceRepository.save(any()) }
    verify(exactly = 1) { invoiceRepository.save(invoice) }
    verify(atLeast = MIN_INVOICE_PRODUCT_COUNT, atMost = MAX_INVOICE_PRODUCT_COUNT) {
      invoiceProductList.add(any())
    }
    verify(atLeast = MIN_INVOICE_PRODUCT_COUNT, atMost = MAX_INVOICE_PRODUCT_COUNT) {
      productRepository.findById(any())
    }
    assertEquals(invoice, result)
    assertEquals(true, invoiceSlot.isCaptured)
    assertEquals(0, invoiceSlot.captured.difference)
  }

  @Test
  fun whenCreateNewInvoiceSpecifiesADifference_newInvoiceCreatedWithDifference() {
    every { productService.initializeProductDatabase() } just runs
    every { invoiceRepository.save(capture(invoiceSlot)) } returns invoice
    every { invoiceRepository.save(invoice) } returns invoice
    every { invoice.invoiceProducts } returns invoiceProductList
    every { invoiceProductList.add(any()) } returns true
    every { productRepository.findById(any()) } returns Optional.of(product)

    val result = invoiceService.createNewInvoice(3)

    verify(exactly = 1) { productService.initializeProductDatabase() }
    verify(exactly = 2) { invoiceRepository.save(any()) }
    verify(exactly = 1) { invoiceRepository.save(invoice) }
    verify(atLeast = MIN_INVOICE_PRODUCT_COUNT, atMost = MAX_INVOICE_PRODUCT_COUNT) {
      invoiceProductList.add(any())
    }
    verify(atLeast = MIN_INVOICE_PRODUCT_COUNT, atMost = MAX_INVOICE_PRODUCT_COUNT) {
      productRepository.findById(any())
    }
    assertEquals(invoice, result)
    assertEquals(true, invoiceSlot.isCaptured)
    assertEquals(3, invoiceSlot.captured.difference)
  }
}
