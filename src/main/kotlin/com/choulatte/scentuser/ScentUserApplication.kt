package com.choulatte.scentuser

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import springfox.documentation.swagger2.annotations.EnableSwagger2

@EnableSwagger2
@SpringBootApplication
class ScentUserApplication

fun main(args: Array<String>) {
    runApplication<ScentUserApplication>(*args)
}
