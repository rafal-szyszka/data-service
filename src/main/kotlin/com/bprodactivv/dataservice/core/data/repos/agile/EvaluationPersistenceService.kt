package com.bprodactivv.dataservice.core.data.repos.agile

import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.bprodactivv.dataservice.core.data.models.agile.Evaluation
import com.bprodactivv.dataservice.core.data.repos.AbstractPersistenceService
import com.bprodactivv.dataservice.core.proql.models.ProQLQuery
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service(Evaluation.CLASS_TYPE)
class EvaluationPersistenceService(
    private val repository: EvaluationRepository,
    metadataExtractor: MetadataExtractor
) : AbstractPersistenceService<Evaluation>(
    metadataExtractor,
    Evaluation::class.java,
    Evaluation.CLASS_TYPE
) {
    override fun save(x: Any): Any {
        return repository.save(x as Evaluation)
    }

    override fun getRepository(): JpaRepository<Evaluation, Long> {
        return repository
    }

    override fun addJoinsWithPredicates(proQLQuery: ProQLQuery): MutableList<Predicate> {
        return mutableListOf()
    }
}