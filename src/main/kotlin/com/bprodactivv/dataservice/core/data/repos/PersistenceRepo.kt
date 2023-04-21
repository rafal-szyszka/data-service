package com.bprodactivv.dataservice.core.data.repos

interface PersistenceRepo {

    fun save(x: Any): Any
    fun findById(id: Int): Any

}