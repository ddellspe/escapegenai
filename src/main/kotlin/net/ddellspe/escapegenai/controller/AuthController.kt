package net.ddellspe.escapegenai.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class AuthController {

  @GetMapping("/session")
  fun getSession(): ResponseEntity<Map<String, Any>> {
    return ResponseEntity.ok(mapOf("authenticated" to true))
  }
}
