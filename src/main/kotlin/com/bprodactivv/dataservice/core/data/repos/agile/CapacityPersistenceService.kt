package com.bprodactivv.dataservice.core.data.repos.agile

import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.bprodactivv.dataservice.core.data.models.agile.Capacity
import com.bprodactivv.dataservice.core.data.repos.AbstractPersistenceService
import com.bprodactivv.dataservice.core.proql.models.ProQLQuery
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service(Capacity.CLASS_TYPE)
class CapacityPersistenceService(
    private val repository: CapacityRepository,
    metadataExtractor: MetadataExtractor
) : AbstractPersistenceService<Capacity>(
    metadataExtractor,
    Capacity::class.java,
    Capacity.CLASS_TYPE
) {
    override fun save(x: Any): Any {
        return repository.save(x as Capacity)
    }

    override fun getRepository(): JpaRepository<Capacity, Long> {
        return repository
    }

    override fun addJoinsWithPredicates(proQLQuery: ProQLQuery): MutableList<Predicate> {
        return mutableListOf()
    }
}