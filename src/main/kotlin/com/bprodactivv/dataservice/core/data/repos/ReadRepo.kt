package com.bprodactivv.dataservice.core.data.repos

import com.bprodactivv.dataservice.core.proql.models.ProQLQuery

interface ReadRepo {

    fun findAll(proQLQuery: ProQLQuery): Any

}