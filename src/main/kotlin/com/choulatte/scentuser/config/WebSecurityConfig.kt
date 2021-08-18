package com.choulatte.scentuser.config

import com.choulatte.scentuser.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder

@EnableWebSecurity
class WebSecurityConfig (
    private val userService: UserService
): WebSecurityConfigurerAdapter() {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userService).passwordEncoder(this.passwordEncoder())
    }

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
            .cors().and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
            .antMatchers("/actuator/**").permitAll()
            .antMatchers(HttpMethod.GET, "/users").permitAll()
            .antMatchers(HttpMethod.POST, "/users", "/users/join").permitAll()
            .antMatchers(HttpMethod.PUT, "/users").permitAll()
            .antMatchers(HttpMethod.DELETE, "/users").permitAll()
            .antMatchers(HttpMethod.GET, "/users/refresh").permitAll()
            .antMatchers("/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui/**").permitAll()
            .anyRequest().authenticated()
    }
}