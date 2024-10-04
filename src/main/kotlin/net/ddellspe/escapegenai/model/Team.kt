package net.ddellspe.escapegenai.model

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Entity(name = "team")
@Table(name = "team")
data class Team(
  @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID = UUID.randomUUID(),
  var name: String = "",
  @Column(name = "first_selected") var firstSelected: OffsetDateTime? = null,
  @OneToMany(
    mappedBy = "team",
    fetch = FetchType.LAZY,
    cascade = [CascadeType.ALL],
    orphanRemoval = true,
  )
  var teamInvoices: MutableList<TeamInvoice> = ArrayList(),
) {
  fun toMinimalTeam(): MinimalTeam {
    return MinimalTeam(this.id, this.name, this.firstSelected)
  }

  fun toTeamContainer(): TeamContainer {
    return TeamContainer(
      this.id,
      this.name,
      this.firstSelected,
      this.teamInvoices
        .stream()
        .filter { teamInvoices -> teamInvoices.firstTask }
        .findFirst()
        .getOrNull()
        ?.id,
    )
  }
}
