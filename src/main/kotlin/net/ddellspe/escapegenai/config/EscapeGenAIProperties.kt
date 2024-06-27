package net.ddellspe.escapegenai.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "escapegenai")
class EscapeGenAIProperties {
  var hostname: String = "http://localhost:8080"
}
