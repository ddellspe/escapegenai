package net.ddellspe.escapegenai.service

import java.util.*
import net.ddellspe.escapegenai.model.Password
import net.ddellspe.escapegenai.repository.PasswordRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PasswordService(var passwordRepository: PasswordRepository) {
  fun getPassword(id: UUID): Password {
    return passwordRepository.findByIdOrNull(id)
      ?: throw IllegalArgumentException("Password with id=${id} does not exist.")
  }
}
