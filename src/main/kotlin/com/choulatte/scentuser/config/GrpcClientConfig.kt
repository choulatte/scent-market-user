package com.choulatte.scentuser.config

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GrpcClientConfig(
    @Value("\${grpc.pay.server.host}")
    var grpcPayServerHost: String,

    @Value("\${grpc.pay.server.port}")
    var grpcPayServerPort: Int,

    @Value("\${grpc.product.server.host}")
    var grpcProductServerHost: String,

    @Value("\${grpc.product.server.port}")
    var grpcProductServerPort: Int
) {

    @Bean(name = ["pay"])
    fun setPayChannel(): ManagedChannel = ManagedChannelBuilder.forAddress(grpcPayServerHost, grpcPayServerPort).usePlaintext().build()

    @Bean(name = ["product"])
    fun setProductChannel(): ManagedChannel = ManagedChannelBuilder.forAddress(grpcProductServerHost, grpcProductServerPort).usePlaintext().build()
}