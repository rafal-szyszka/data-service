package com.bprodactivv.dataservice.core.utilities

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean

class Utils {

    @Bean
    fun getJackson(): ObjectMapper {
        return jacksonObjectMapper()
    }

}