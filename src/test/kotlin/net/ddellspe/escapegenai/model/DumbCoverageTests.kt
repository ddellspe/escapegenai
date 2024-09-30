package net.ddellspe.escapegenai.model

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DumbCoverageTests {

  @Test
  fun testContainerWithErrorTests() {
    val teamContainerWithError = TeamContainerWithError()

    assertNull(teamContainerWithError.teamContainer)
    assertNull(teamContainerWithError.error)
  }

  @Test
  fun gameSubmissionTest() {
    val gameSubmission = GameSubmission(UUID.randomUUID())

    assertEquals(null, gameSubmission.fact)
  }

  @Test
  fun testToMinimalTeam() {
    val team = Team()
    team.firstSelected = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)

    val minimalTeam = team.toMinimalTeam()

    val expected = MinimalTeam(team.id, team.name, team.firstSelected)
    assertEquals(expected, minimalTeam)
  }

  @Test
  fun toTeamContainerPrimaryInvoiceNotPresent() {
    val team = Team()
    team.firstSelected = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)

    val teamContainer = team.toTeamContainer()

    val expected = TeamContainer(team.id, team.name, team.firstSelected)
    assertEquals(expected, teamContainer)
  }

  @Test
  fun toTeamContainerPrimaryInvoicePresent() {
    val team = Team()
    val invoice: Invoice = mockk()
    every { invoice.id } returns 1L
    team.firstSelected = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
    team.primaryInvoice = invoice

    val teamContainer = team.toTeamContainer()

    val expected = TeamContainer(team.id, team.name, team.firstSelected, 1L)
    assertEquals(expected, teamContainer)
    verify(exactly = 1) { invoice.id }
  }

  @Test
  fun testInvoice() {
    val invoice =
      Invoice(1L, LocalDate.now(), "Company Name", "Company, Address, place", 1, ArrayList())
    var invoice2 =
      Invoice(
        date = LocalDate.now(),
        company = "Company Name",
        address = "Company, Address, place",
        difference = 1,
      )
    assertFalse(invoice.equals(null))
    assertFalse(invoice.equals(""))
    assertTrue(invoice.equals(invoice))
    assertTrue(invoice == invoice2)
    assertTrue(invoice.hashCode() == invoice2.hashCode())
    invoice2.date = invoice.date.plusDays(1)
    assertFalse(invoice == invoice2)
    invoice2.date = invoice.date
    invoice2.company = "Company Name 2"
    assertFalse(invoice == invoice2)
    invoice2.company = invoice.company
    invoice2.address = "Company 2, Address, place"
    assertFalse(invoice == invoice2)
    invoice2.address = invoice.address
    invoice2.difference = 0
    assertFalse(invoice == invoice2)
    invoice2.difference = invoice.difference
    invoice2.invoiceProducts.add(InvoiceProduct(invoice = invoice, product = Product()))
    assertFalse(invoice == invoice2)
    assertEquals(
      "Invoice(id=1, date=${LocalDate.now()}, company=Company Name, address=Company, Address, place, difference=1, invoiceProducts=[])",
      invoice.toString(),
    )
  }

  @Test
  fun testProduct() {
    val product = Product(1L, "Product", 30)
    val product2 = Product(name = "Product")

    assertTrue(product2.price >= 10)
    assertTrue(product2.price <= 300)
    product2.price = 30
    assertFalse(product.equals(null))
    assertFalse(product.equals(""))
    assertTrue(product.equals(product))
    assertTrue(product == product2)
    assertTrue(product.hashCode() == product2.hashCode())
    product2.name = "Product2"
    assertFalse(product == product2)
    product2.name = "Product"
    product2.price = 40
    assertFalse(product == product2)
    assertEquals("Product(id=1, name='Product', price=30)", product.toString())
  }

  @Test
  fun testInvoiceProduct() {
    val invoice: Invoice = mockk()
    val invoice2: Invoice = mockk()
    val product: Product = mockk()
    val product2: Product = mockk()
    val uuid = UUID.randomUUID()
    val invoiceProduct = InvoiceProduct(uuid, invoice, product, 1)
    val invoiceProduct2 = InvoiceProduct(invoice = invoice, product = product)

    assertTrue(invoiceProduct2.quantity >= 1)
    assertTrue(invoiceProduct2.quantity <= 10)
    invoiceProduct2.quantity = 1
    assertFalse(invoiceProduct.equals(null))
    assertFalse(invoiceProduct.equals(""))
    assertTrue(invoiceProduct.equals(invoiceProduct))
    assertTrue(invoiceProduct.equals(invoiceProduct2))
    assertTrue(invoiceProduct.hashCode() == invoiceProduct2.hashCode())
    invoiceProduct2.invoice = invoice2
    assertFalse(invoiceProduct == invoiceProduct2)
    invoiceProduct2.invoice = invoiceProduct.invoice
    invoiceProduct2.product = product2
    assertFalse(invoiceProduct == invoiceProduct2)
    invoiceProduct2.product = product
    invoiceProduct2.quantity = 2
    assertFalse(invoiceProduct == invoiceProduct2)
    assertEquals(
      "InvoiceProduct(id=$uuid, product=$product, quantity=1)",
      invoiceProduct.toString(),
    )
  }
}
