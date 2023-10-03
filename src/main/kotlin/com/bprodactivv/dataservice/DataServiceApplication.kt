package com.bprodactivv.dataservice

import com.bprodactivv.dataservice.core.data.metadata.cashLoader.MetadataCacheLoader
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DataServiceApplication

fun main(args: Array<String>) {
    runApplication<DataServiceApplication>(*args)
}
