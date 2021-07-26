package com.choulatte.scentuser.config

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GrpcClientConfig(
    @Value("\${grpc.pay.server.name}")
    var grpcPayServer: String,

    @Value("\${grpc.pay.server.port}")
    var grpcPayServerPort: Int,

    @Value("\${grpc.product.server.name}")
    var grpcProductServer: String,

    @Value("\${grpc.product.server.port}")
    var grpcProductServerPort: Int
) {

    @Bean(name = ["pay"])
    fun setPayChannel(): ManagedChannel {
        return ManagedChannelBuilder.forAddress(grpcPayServer, grpcPayServerPort).usePlaintext().build()
    }

    @Bean(name = ["product"])
    fun setProductChannel(): ManagedChannel {
        return ManagedChannelBuilder.forAddress(grpcProductServer, grpcProductServerPort).usePlaintext().build()
    }
}