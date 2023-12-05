package com.bprodactivv.dataservice.redis

import com.bprodactivv.dataservice.redis.config.RedisConfig
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import redis.clients.jedis.Jedis

@Component
class RedisComponent(
    private val config: RedisConfig
) {

    @Bean
    fun redis(): Jedis {
        return Jedis(config.host, config.port.toInt())
    }
}