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

    assertEquals(null, gameSubmission.highCost)
  }

  @Test
  fun testToMinimalTeam() {
    val team = Team()
    val teamInvoice: TeamInvoice = mockk()
    val uuid = UUID.randomUUID()
    val teamInvoice2: TeamInvoice = mockk()
    val uuid2 = UUID.randomUUID()
    every { teamInvoice.id } returns uuid
    every { teamInvoice.firstTask } returns true
    every { teamInvoice2.id } returns uuid2
    every { teamInvoice2.firstTask } returns false
    team.firstSelected = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
    team.productsIdentified = OffsetDateTime.of(2024, 1, 1, 1, 0, 0, 0, ZoneOffset.UTC)
    team.leakageIdentified = OffsetDateTime.of(2024, 1, 1, 2, 0, 0, 0, ZoneOffset.UTC)
    team.suppliersContacted = OffsetDateTime.of(2024, 1, 1, 3, 0, 0, 0, ZoneOffset.UTC)
    team.overpaidEmail = "email1"
    team.underpaidEmail = "email2"
    team.teamInvoices = mutableListOf(teamInvoice2, teamInvoice)

    val minimalTeam = team.toMinimalTeam()

    val expected =
      MinimalTeam(
        id = team.id,
        name = team.name,
        firstSelected = team.firstSelected,
        productsIdentified = team.productsIdentified,
        leakageIdentified = team.leakageIdentified,
        suppliersContacted = team.suppliersContacted,
        primaryInvoiceId = uuid,
        invoiceIds = listOf(uuid2, uuid),
      )
    MinimalTeam(id = team.id, name = team.name, primaryInvoiceId = uuid, invoiceIds = listOf(uuid))
    assertEquals(expected, minimalTeam)
    verify(exactly = 2) { teamInvoice.id }
    verify(exactly = 1) { teamInvoice.firstTask }
    verify(exactly = 1) { teamInvoice2.id }
    verify(exactly = 1) { teamInvoice2.firstTask }
  }

  @Test
  fun toTeamContainerPrimaryInvoiceNotPresent() {
    val team = Team()
    team.firstSelected = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
    team.productsIdentified = OffsetDateTime.of(2024, 1, 1, 1, 0, 0, 0, ZoneOffset.UTC)
    team.leakageIdentified = OffsetDateTime.of(2024, 1, 1, 2, 0, 0, 0, ZoneOffset.UTC)
    team.suppliersContacted = OffsetDateTime.of(2024, 1, 1, 3, 0, 0, 0, ZoneOffset.UTC)

    val teamContainer = team.toTeamContainer()

    val expected =
      TeamContainer(
        team.id,
        team.name,
        team.firstSelected,
        team.productsIdentified,
        team.leakageIdentified,
        team.suppliersContacted,
      )
    assertEquals(expected, teamContainer)
  }

  @Test
  fun toTeamContainerPrimaryInvoicePresent() {
    val team = Team()
    val teamInvoice: TeamInvoice = mockk()
    val uuid = UUID.randomUUID()
    every { teamInvoice.id } returns uuid
    every { teamInvoice.firstTask } returns true
    team.firstSelected = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
    team.productsIdentified = OffsetDateTime.of(2024, 1, 1, 1, 0, 0, 0, ZoneOffset.UTC)
    team.leakageIdentified = OffsetDateTime.of(2024, 1, 1, 2, 0, 0, 0, ZoneOffset.UTC)
    team.suppliersContacted = OffsetDateTime.of(2024, 1, 1, 3, 0, 0, 0, ZoneOffset.UTC)
    team.teamInvoices = mutableListOf(teamInvoice)

    val teamContainer = team.toTeamContainer()

    val expected =
      TeamContainer(
        team.id,
        team.name,
        team.firstSelected,
        team.productsIdentified,
        team.leakageIdentified,
        team.suppliersContacted,
        uuid,
      )
    assertEquals(expected, teamContainer)
    verify(exactly = 1) { teamInvoice.id }
    verify(exactly = 1) { teamInvoice.firstTask }
  }

  @Test
  fun testInvoice() {
    val invoiceProduct: InvoiceProduct = mockk()
    val invoice =
      Invoice(
        id = 1L,
        date = LocalDate.now(),
        company = "Company Name",
        address = "Company, Address, place",
        difference = 1,
        invoiceProducts = ArrayList(),
      )
    val invoice2 =
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
    invoice2.invoiceProducts.add(invoiceProduct)
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

    assertTrue(invoiceProduct2.quantity >= 10)
    assertTrue(invoiceProduct2.quantity <= 50)
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

  @Test
  fun testTeamInvoice() {
    val team: Team = mockk()
    val team2: Team = mockk()
    val invoice: Invoice = mockk()
    val invoice2: Invoice = mockk()
    val uuid = UUID.randomUUID()
    val teamInvoice = TeamInvoice(id = uuid, team = team, invoice = invoice, firstTask = false)
    val teamInvoice2 = TeamInvoice(team = team, invoice = invoice)

    assertFalse(teamInvoice.equals(null))
    assertFalse(teamInvoice.equals(""))
    assertTrue(teamInvoice.equals(teamInvoice))
    assertTrue(teamInvoice.equals(teamInvoice2))
    assertTrue(teamInvoice.hashCode() == teamInvoice2.hashCode())
    teamInvoice2.team = team2
    assertFalse(teamInvoice == teamInvoice2)
    teamInvoice2.team = team
    teamInvoice2.invoice = invoice2
    assertFalse(teamInvoice == teamInvoice2)
    teamInvoice2.invoice = invoice
    teamInvoice2.firstTask = true
    assertFalse(teamInvoice == teamInvoice2)
    assertEquals("TeamInvoice(id=$uuid, invoice=$invoice, firstTask=false)", teamInvoice.toString())
  }

  @Test
  fun testInvoiceDocument() {
    val uuid = UUID.randomUUID()
    val invoiceProductDocument =
      InvoiceProductDocument(id = uuid, name = "product", qty = 1, price = 2, total = 2)
    val invoiceDocument =
      InvoiceDocument(
        id = 1L,
        teamName = "name",
        company = "company",
        address = "address",
        total = 2L,
        products = listOf(invoiceProductDocument),
      )
    invoiceProductDocument.id = uuid
    invoiceProductDocument.name = "product"
    invoiceProductDocument.qty = 1
    invoiceProductDocument.price = 2
    invoiceProductDocument.total = 2
    invoiceDocument.id = 1L
    invoiceDocument.teamName = "name"
    invoiceDocument.company = "company"
    invoiceDocument.address = "address"
    invoiceDocument.total = 2L
    invoiceDocument.products = listOf(invoiceProductDocument)

    assertEquals(uuid, invoiceProductDocument.id)
    assertEquals("product", invoiceProductDocument.name)
    assertEquals(1, invoiceProductDocument.qty)
    assertEquals(2, invoiceProductDocument.price)
    assertEquals(2, invoiceProductDocument.total)
    assertEquals(1L, invoiceDocument.id)
    assertEquals("name", invoiceDocument.teamName)
    assertEquals("company", invoiceDocument.company)
    assertEquals("address", invoiceDocument.address)
    assertEquals(2L, invoiceDocument.total)
    assertEquals(invoiceProductDocument, invoiceDocument.products.first())
  }
}
