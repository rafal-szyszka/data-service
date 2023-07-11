package com.bprodactivv.dataservice.core.data.repos.agile

import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.bprodactivv.dataservice.core.data.models.agile.Description
import com.bprodactivv.dataservice.core.data.repos.AbstractPersistenceService
import com.bprodactivv.dataservice.core.proql.models.ProQLQuery
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service(Description.CLASS_TYPE)
class DescriptionPersistenceService(
    private val repository: DescriptionRepository,
    metadataExtractor: MetadataExtractor
) : AbstractPersistenceService<Description>(
    metadataExtractor,
    Description::class.java,
    Description.CLASS_TYPE
) {
    override fun save(x: Any): Any {
        return repository.save(x as Description)
    }

    override fun getRepository(): JpaRepository<Description, Long> {
        return repository
    }

    override fun addJoinsWithPredicates(proQLQuery: ProQLQuery): MutableList<Predicate> {
        return mutableListOf()
    }
}