package net.ddellspe.escapegenai.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.*
import kotlin.random.Random

@Entity
@Table(
  name = "invoice_product",
  uniqueConstraints = [UniqueConstraint(columnNames = ["invoice_id", "product_id"])],
)
class InvoiceProduct(
  @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID = UUID.randomUUID(),
  @ManyToOne @JoinColumn(name = "invoice_id", updatable = false) @JsonIgnore var invoice: Invoice,
  @ManyToOne @JoinColumn(name = "product_id", updatable = false) var product: Product,
  @Column(name = "qty") var quantity: Int = Random.nextInt(1, 10),
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as InvoiceProduct

    if (invoice != other.invoice) return false
    if (product != other.product) return false
    if (quantity != other.quantity) return false

    return true
  }

  override fun hashCode(): Int {
    var result = invoice.hashCode()
    result = 31 * result + product.hashCode()
    result = 31 * result + quantity
    return result
  }

  override fun toString(): String {
    return "InvoiceProduct(id=$id, product=$product, quantity=$quantity)"
  }
}
