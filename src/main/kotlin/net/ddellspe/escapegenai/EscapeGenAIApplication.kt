package net.ddellspe.escapegenai

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class EscapeGenAIApplication

fun main(args: Array<String>) {
  runApplication<EscapeGenAIApplication>(*args)
}
