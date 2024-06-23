package net.ddellspe.escapegenai.model

data class TeamContainerWithError(
  var teamContainer: TeamContainer? = null,
  var error: Map<String, Any>? = null,
)
