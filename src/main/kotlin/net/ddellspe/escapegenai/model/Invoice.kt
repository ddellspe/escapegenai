package net.ddellspe.escapegenai.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "invoice")
class Invoice(
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_id")
  @SequenceGenerator(
    sequenceName = "invoiceSeq",
    initialValue = 123456789,
    name = "invoice_id",
    allocationSize = 10,
  )
  var id: Long? = null,
  var date: LocalDate = LocalDate.now(),
  var difference: Int = 0,
  @OneToMany(
    mappedBy = "invoice",
    fetch = FetchType.LAZY,
    cascade = [CascadeType.ALL],
    orphanRemoval = true,
  )
  var invoiceProducts: MutableList<InvoiceProduct> = ArrayList(),
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Invoice

    if (date != other.date) return false
    if (difference != other.difference) return false
    if (invoiceProducts != other.invoiceProducts) return false

    return true
  }

  override fun hashCode(): Int {
    var result = date.hashCode()
    result = 31 * result + difference
    result = 31 * result + invoiceProducts.hashCode()
    return result
  }

  override fun toString(): String {
    return "Invoice(id=$id, date=$date, difference=$difference, invoiceProducts=$invoiceProducts)"
  }
}