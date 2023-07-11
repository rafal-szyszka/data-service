package com.bprodactivv.dataservice.core.data.repos.agile

import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.bprodactivv.dataservice.core.data.models.agile.*
import com.bprodactivv.dataservice.core.data.models.core.SysUser
import com.bprodactivv.dataservice.core.data.repos.AbstractPersistenceService
import com.bprodactivv.dataservice.core.exceptions.UnknownRelationException
import com.bprodactivv.dataservice.core.proql.models.ProQLQuery
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service(Task.CLASS_TYPE)
class TaskPersistenceService(
    private val repository: TaskRepository,
    metadataExtractor: MetadataExtractor,
) : AbstractPersistenceService<Task>(
    metadataExtractor,
    Task::class.java,
    Task.CLASS_TYPE,
) {
    override fun save(x: Any): Any {
        return repository.save(x as Task)
    }

    override fun getRepository(): JpaRepository<Task, Long> {
        return repository
    }

    override fun addJoinsWithPredicates(proQLQuery: ProQLQuery): MutableList<Predicate> {
        val joinPredicates = mutableListOf<Predicate>()

        proQLQuery.subQueries?.forEach {
            when (it.parentProperty) {
                "descriptionExtension" -> {
                    joinPredicates.addAll(
                        join(it, Description.CLASS_TYPE)
                    )
                }

                "orderer", "performer" -> {
                    joinPredicates.addAll(
                        join(it, SysUser.CLASS_TYPE)
                    )
                }

                "project" -> {
                    joinPredicates.addAll(
                        join(it, Project.CLASS_TYPE)
                    )
                }

                "capacity" -> {
                    joinPredicates.addAll(
                        join(it, Capacity.CLASS_TYPE)
                    )
                }

                "evaluation" -> {
                    joinPredicates.addAll(
                        join(it, Evaluation.CLASS_TYPE)
                    )
                }

                else -> throw UnknownRelationException()
            }
        }

        return joinPredicates
    }
}