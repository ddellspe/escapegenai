package net.ddellspe.escapegenai.model

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity(name = "team")
@Table(name = "team")
data class Team(
  @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID = UUID.randomUUID(),
  var name: String = "",
  @Column(name = "first_selected") var firstSelected: OffsetDateTime? = null,
  @Column(name = "products_identified") var productsIdentified: OffsetDateTime? = null,
  @Column(name = "leakage_identified") var leakageIdentified: OffsetDateTime? = null,
  @Column(name = "suppliers_contacted") var suppliersContacted: OffsetDateTime? = null,
  @OneToMany(
    mappedBy = "team",
    fetch = FetchType.LAZY,
    cascade = [CascadeType.ALL],
    orphanRemoval = true,
  )
  var teamInvoices: MutableList<TeamInvoice> = ArrayList(),
  @Lob @Column(length = 10000) var overpaidEmail: String? = null,
  @Lob @Column(length = 10000) var underpaidEmail: String? = null,
) {
  fun toMinimalTeam(): MinimalTeam {
    return MinimalTeam(
      this.id,
      this.name,
      this.firstSelected,
      this.productsIdentified,
      this.leakageIdentified,
      this.suppliersContacted,
      this.teamInvoices
        .stream()
        .filter { teamInvoices -> teamInvoices.firstTask }
        .findFirst()
        .get()
        .id,
      this.teamInvoices.stream().map { teamInvoice -> teamInvoice.id }.toList(),
      this.underpaidEmail,
      this.overpaidEmail,
    )
  }

  fun toTeamContainer(): TeamContainer {
    return TeamContainer(
      this.id,
      this.name,
      this.firstSelected,
      this.productsIdentified,
      this.leakageIdentified,
      this.suppliersContacted,
    )
  }
}
