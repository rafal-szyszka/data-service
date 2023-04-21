package com.bprodactivv.dataservice.api.queries

import com.bprodactivv.dataservice.core.data.repos.ReadRepo
import com.bprodactivv.dataservice.core.proql.models.ProQLQuery
import org.springframework.stereotype.Service

@Service
class LoadDataService(
    private val reposByType: Map<String, ReadRepo>,
) {

    fun load(query: ProQLQuery): Any? {
        return reposByType[query.type]
            ?.findAll(query)
    }

}
