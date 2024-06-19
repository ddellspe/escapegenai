package net.ddellspe.escapegenai.config

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.io.PrintWriter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.AuthenticationException

class AppBasicAuthenticationEntryPointTest {
  private val appBasicAuthenticationEntryPoint = AppBasicAuthenticationEntryPoint()

  @BeforeEach
  fun setup() {
    appBasicAuthenticationEntryPoint.realmName = "realm"
  }

  @Test
  fun whenResponseIsNull_ExpectNoCalls() {
    appBasicAuthenticationEntryPoint.commence(null, null, null)
  }

  @Test
  fun whenResponseIsNonNull_andWriterNull_ExpectCalls() {
    val response: HttpServletResponse = mockk()
    justRun { response.addHeader("WWW-Authenticate", "Basic realm=realm") }
    justRun { response.status = HttpServletResponse.SC_UNAUTHORIZED }
    every { response.writer } returns null

    appBasicAuthenticationEntryPoint.commence(null, response, null)

    verify(exactly = 1) { response.addHeader("WWW-Authenticate", "Basic realm=realm") }
    verify(exactly = 1) { response.status = HttpServletResponse.SC_UNAUTHORIZED }
    verify(exactly = 1) { response.writer }
  }

  @Test
  fun whenResponseIsNonNull_andWriterNotNull_ExpectCalls() {
    val response: HttpServletResponse = mockk()
    val writer: PrintWriter = mockk()
    justRun { response.addHeader("WWW-Authenticate", "Basic realm=realm") }
    justRun { response.status = HttpServletResponse.SC_UNAUTHORIZED }
    every { response.writer } returns writer
    justRun { writer.println("HTTP Status 401 - null") }

    appBasicAuthenticationEntryPoint.commence(null, response, null)

    verify(exactly = 1) { response.addHeader("WWW-Authenticate", "Basic realm=realm") }
    verify(exactly = 1) { response.status = HttpServletResponse.SC_UNAUTHORIZED }
    verify(exactly = 1) { response.writer }
    verify(exactly = 1) { writer.println("HTTP Status 401 - null") }
  }

  @Test
  fun whenResponseIsNonNull_andWriterNotNull_authExceptionNonNull_ExpectCalls() {
    val exception: AuthenticationException = mockk()
    val response: HttpServletResponse = mockk()
    val writer: PrintWriter = mockk()
    justRun { response.addHeader("WWW-Authenticate", "Basic realm=realm") }
    justRun { response.status = HttpServletResponse.SC_UNAUTHORIZED }
    every { response.writer } returns writer
    justRun { writer.println("HTTP Status 401 - message") }
    every { exception.message } returns "message"

    appBasicAuthenticationEntryPoint.commence(null, response, exception)

    verify(exactly = 1) { response.addHeader("WWW-Authenticate", "Basic realm=realm") }
    verify(exactly = 1) { response.status = HttpServletResponse.SC_UNAUTHORIZED }
    verify(exactly = 1) { response.writer }
    verify(exactly = 1) { writer.println("HTTP Status 401 - message") }
    verify(exactly = 1) { exception.message }
  }

  @Test
  fun whenRequestIsNonNull_addWwwAuthenticateHeader_ExpectCalls() {
    val exception: AuthenticationException = mockk()
    val response: HttpServletResponse = mockk()
    val request: HttpServletRequest = mockk()
    val writer: PrintWriter = mockk()
    justRun { response.addHeader("WWW-Authenticate", "Basic realm=realm") }
    justRun { response.status = HttpServletResponse.SC_UNAUTHORIZED }
    every { request.servletPath } returns "/"
    every { response.writer } returns writer
    justRun { writer.println("HTTP Status 401 - message") }
    every { exception.message } returns "message"

    appBasicAuthenticationEntryPoint.commence(request, response, exception)

    verify(exactly = 1) { response.addHeader("WWW-Authenticate", "Basic realm=realm") }
    verify(exactly = 1) { response.status = HttpServletResponse.SC_UNAUTHORIZED }
    verify(exactly = 1) { response.writer }
    verify(exactly = 1) { request.servletPath }
    verify(exactly = 1) { writer.println("HTTP Status 401 - message") }
    verify(exactly = 1) { exception.message }
  }

  @Test
  fun whenRequestIsNonNull_andPathSession_dontAddWwwAuthenticateHeader_ExpectCalls() {
    val exception: AuthenticationException = mockk()
    val response: HttpServletResponse = mockk()
    val request: HttpServletRequest = mockk()
    val writer: PrintWriter = mockk()
    justRun { response.status = HttpServletResponse.SC_UNAUTHORIZED }
    every { request.servletPath } returns "/session"
    every { response.writer } returns writer
    justRun { writer.println("HTTP Status 401 - message") }
    every { exception.message } returns "message"

    appBasicAuthenticationEntryPoint.commence(request, response, exception)

    verify(exactly = 1) { response.status = HttpServletResponse.SC_UNAUTHORIZED }
    verify(exactly = 1) { response.writer }
    verify(exactly = 1) { request.servletPath }
    verify(exactly = 1) { writer.println("HTTP Status 401 - message") }
    verify(exactly = 1) { exception.message }
  }
}
