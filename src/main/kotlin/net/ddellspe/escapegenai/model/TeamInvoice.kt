package net.ddellspe.escapegenai.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.*

@Entity
@Table(
  name = "team_invoice",
  uniqueConstraints = [UniqueConstraint(columnNames = ["team_id", "invoice_id"])],
)
class TeamInvoice(
  @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID = UUID.randomUUID(),
  @ManyToOne @JoinColumn(name = "team_id", updatable = false) @JsonIgnore var team: Team,
  @ManyToOne @JoinColumn(name = "invoice_id", updatable = false) var invoice: Invoice,
  var firstTask: Boolean = false,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as TeamInvoice

    if (team != other.team) return false
    if (invoice != other.invoice) return false
    if (firstTask != other.firstTask) return false

    return true
  }

  override fun hashCode(): Int {
    var result = team.hashCode()
    result = 31 * result + invoice.hashCode()
    result = 31 * result + firstTask.hashCode()
    return result
  }

  override fun toString(): String {
    return "TeamInvoice(id=$id, invoice=$invoice, firstTask=$firstTask)"
  }
}
