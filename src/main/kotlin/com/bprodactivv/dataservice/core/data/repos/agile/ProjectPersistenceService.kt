package com.bprodactivv.dataservice.core.data.repos.agile

import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.bprodactivv.dataservice.core.data.models.agile.Project
import com.bprodactivv.dataservice.core.data.models.core.Client
import com.bprodactivv.dataservice.core.data.repos.AbstractPersistenceService
import com.bprodactivv.dataservice.core.exceptions.UnknownRelationException
import com.bprodactivv.dataservice.core.proql.models.ProQLQuery
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service(Project.CLASS_TYPE)
class ProjectPersistenceService(
    private val repository: ProjectRepository,
    metadataExtractor: MetadataExtractor
) : AbstractPersistenceService<Project>(
    metadataExtractor,
    Project::class.java,
    Project.CLASS_TYPE
) {
    override fun save(x: Any): Any {
        return repository.save(x as Project)
    }

    override fun getRepository(): JpaRepository<Project, Long> {
        return repository
    }

    override fun addJoinsWithPredicates(proQLQuery: ProQLQuery): MutableList<Predicate> {
        val joinPredicates = mutableListOf<Predicate>()
        proQLQuery.subQueries?.forEach {
            when (it.parentProperty) {
                "client", "orderingClient" -> {
                    joinPredicates.addAll(
                        join(it, Client.CLASS_TYPE, isSingular = true)
                    )
                }

                else -> throw UnknownRelationException()
            }
        }

        return joinPredicates
    }
}