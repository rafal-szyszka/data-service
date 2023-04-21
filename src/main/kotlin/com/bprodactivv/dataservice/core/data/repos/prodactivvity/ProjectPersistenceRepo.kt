package com.bprodactivv.dataservice.core.data.repos.prodactivvity

import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.bprodactivv.dataservice.core.data.models.prodactivvity.Project
import com.bprodactivv.dataservice.core.data.repos.PersistenceRepo
import com.bprodactivv.dataservice.core.data.repos.ReadRepo
import com.bprodactivv.dataservice.core.exceptions.UnknownRelationException
import com.bprodactivv.dataservice.core.proql.ProQL
import com.bprodactivv.dataservice.core.proql.models.ProQLQuery
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.metamodel.EntityType
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service("prodactivvity.Project")
@Transactional
class ProjectPersistenceRepo(
    private val repo: ProjectRepository,
    private val metadataExtractor: MetadataExtractor,
) : PersistenceRepo, ReadRepo {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    private lateinit var proQL: ProQL<Project>

    private lateinit var entity: EntityType<Project>

    override fun save(x: Any): Any {
        return repo.save(x as Project)
    }

    override fun findById(id: Int): Any {
        return repo.findById(id.toLong())
    }

    override fun findAll(proQLQuery: ProQLQuery): Any {
        initProQL()

        val predicates = mutableListOf(
            *addJoinsWithPredicates(proQLQuery).toTypedArray(),
            *proQL.predicates(proQLQuery).toTypedArray()
        )

        return proQL.where(predicates)
            .query()
    }

    private fun addJoinsWithPredicates(proQLQuery: ProQLQuery): MutableList<Predicate> {
        val joinPredicates: MutableList<Predicate> = mutableListOf()
        proQLQuery.subQueries?.forEach {
            when (it.parentProperty) {
                "customers" -> {
                    val customer = proQL.root.join(
                        entity.getList(
                            it.parentProperty,
                            metadataExtractor.findClass("organization.Customer")
                        )
                    )
                    joinPredicates.addAll(
                        proQL.predicatesFor(
                            customer,
                            it,
                            metadataExtractor.getClassDeclaredFields("organization.Customer")
                        )
                    )
                }

                else -> throw UnknownRelationException()
            }
        }

        return joinPredicates
    }

    private fun initProQL() {
        entity = entityManager.metamodel.entity(Project::class.java)
        proQL = ProQL.Builder<Project>(entityManager)
            .type(Project::class.java)
            .fields(metadataExtractor.getClassDeclaredFields("prodactivvity.Project"))
            .rootEntity(entityManager.metamodel.entity(Project::class.java))
            .build()
    }
}