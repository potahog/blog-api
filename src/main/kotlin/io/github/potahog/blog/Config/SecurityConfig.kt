package io.github.potahog.blog.Config

import io.github.potahog.blog.security.JwtAuthenticationFilter
import org.springframework.context.annotation.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig (
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
){
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf{ it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .formLogin{ it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        "/api/auth/**",
                        "/api/posts",       // 목록
                        "/api/posts/*"      // 단일조회
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration) : AuthenticationManager = config.authenticationManager
}