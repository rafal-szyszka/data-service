package com.bprodactivv.dataservice.core.data.metadata

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource(value = ["classpath:DataHeaderExtractor.properties"])
class MetadataExtractorConfiguration {

    @Value("\${modelsBasePath}")
    lateinit var modelsBasePath: String

    @Value("\${constraintsPackage}")
    lateinit var constraintsPackage: String

}