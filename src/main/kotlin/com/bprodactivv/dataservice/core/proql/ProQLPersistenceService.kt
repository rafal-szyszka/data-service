package com.bprodactivv.dataservice.core.proql

interface ProQLPersistenceService {

    fun save(x: Any): Any
    fun findById(id: Long): Any?
}