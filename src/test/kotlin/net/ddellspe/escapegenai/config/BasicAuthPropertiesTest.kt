package net.ddellspe.escapegenai.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.factory.PasswordEncoderFactories

class BasicAuthPropertiesTest {
  @Test
  fun whenUsersIsSet_userDetailsHasValue() {
    val usernameDetail = BasicAuthProperties.UserDetail()
    usernameDetail.role = "ADMIN"
    usernameDetail.password = "password"

    assertEquals("ADMIN", usernameDetail.role)
    assertEquals("password", usernameDetail.password)

    val usersMap = mapOf("username" to usernameDetail)
    val authProperties = BasicAuthProperties()
    authProperties.users = usersMap
    assertEquals(usersMap, authProperties.users)

    val usernameDetails =
      User.withUsername("username")
        .passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder()::encode)
        .password("password")
        .roles("ADMIN")
        .build()
    assertEquals(setOf(usernameDetails), authProperties.getUserDetails())
  }

  @Test
  fun whenUsersIsNotSet_userDetailsHasNoValues() {
    val authProperties = BasicAuthProperties()
    authProperties.users = emptyMap()

    assertEquals(emptySet<UserDetails>(), authProperties.getUserDetails())
  }
}
