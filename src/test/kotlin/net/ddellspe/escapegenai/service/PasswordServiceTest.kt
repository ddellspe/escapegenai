package net.ddellspe.escapegenai.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import net.ddellspe.escapegenai.model.Password
import net.ddellspe.escapegenai.repository.PasswordRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

class PasswordServiceTest {
  private val passwordRepository: PasswordRepository = mockk()
  private val passwordService = PasswordService(passwordRepository)
  private val password: Password = mockk()
  private val id = UUID.randomUUID()

  @Test
  fun dumbCoverageTest() {
    passwordService.passwordRepository = passwordRepository
  }

  @Test
  fun whenGetPassword_hasNoPassword_thenExpectException() {
    every { passwordRepository.findByIdOrNull(id) } returns null

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { passwordService.getPassword(id) }

    verify(exactly = 1) { passwordRepository.findByIdOrNull(id) }
    assertEquals("Password with id=${id} does not exist.", exception.message)
  }

  @Test
  fun whenGetPassword_hasPassword_thenReturnPassword() {
    every { passwordRepository.findByIdOrNull(id) } returns password

    val result: Password = passwordService.getPassword(id)

    verify(exactly = 1) { passwordRepository.findByIdOrNull(id) }
    assertEquals(password, result)
  }
}
