package net.ddellspe.escapegenai.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(BasicAuthProperties::class)
class SecurityConfig {

  @Autowired private var props: BasicAuthProperties = BasicAuthProperties()

  @Bean
  fun userDetailsService(): InMemoryUserDetailsManager {
    return InMemoryUserDetailsManager(props.getUserDetails())
  }

  @Bean
  fun userAuthenticationErrorHandler(): AuthenticationEntryPoint {
    val appBasicAuthenticationEntryPoint = AppBasicAuthenticationEntryPoint()
    appBasicAuthenticationEntryPoint.realmName = "Basic Authentication"
    return appBasicAuthenticationEntryPoint
  }

  @Bean
  fun webSecurityCustomizer(): WebSecurityCustomizer {
    return WebSecurityCustomizer { web ->
      web
        .ignoring()
        .requestMatchers(
          "/",
          "/static/**",
          "index.html",
          "manifest.json",
          "favicon**",
          "apple-touch-icon-**",
          "android-chrome-**",
          "robots.txt"
        )
    }
  }

  @Bean
  fun filterChain(http: HttpSecurity): SecurityFilterChain {
    http
      .cors { customizer -> customizer.disable() }
      .csrf { customizer -> customizer.disable() }
      .httpBasic { customizer ->
        customizer.authenticationEntryPoint(userAuthenticationErrorHandler())
      }

    return http.build()
  }
}
