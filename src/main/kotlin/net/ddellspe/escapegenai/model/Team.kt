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
  @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY) var primaryInvoice: Invoice? = null,
) {
  fun toMinimalTeam(): MinimalTeam {
    return MinimalTeam(this.id, this.name, this.firstSelected)
  }

  fun toTeamContainer(): TeamContainer {
    return TeamContainer(this.id, this.name, this.firstSelected, this.primaryInvoice?.id)
  }
}
