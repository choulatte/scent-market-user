package com.choulatte.scentuser.controller

import com.choulatte.scentuser.application.UserService
import com.choulatte.scentuser.dto.UserDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController(value = "/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping(value = [""])
    fun join(@RequestBody userDTO: UserDTO): ResponseEntity<Long> {
        return ResponseEntity.ok(userService.join(userDTO))
    }
}