package net.ddellspe.escapegenai.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.factory.PasswordEncoderFactories

@ConfigurationProperties(prefix = "auth")
class BasicAuthProperties {
  var users: Map<String, UserDetail> = emptyMap()

  fun getUserDetails(): Set<UserDetails> {
    return users.entries
      .map { entry ->
        User.withUsername(entry.key)
          .passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder()::encode)
          .password(entry.value.password)
          .roles(entry.value.role)
          .build()
      }
      .toSet()
  }

  class UserDetail {
    var password: String = "password"
    var role: String = "USER"
  }
}
