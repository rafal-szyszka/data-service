package com.bprodactivv.dataservice.core.data.repos.organization

import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.bprodactivv.dataservice.core.data.models.organization.AppUser
import com.bprodactivv.dataservice.core.data.repos.PersistenceRepo
import com.bprodactivv.dataservice.core.data.repos.ReadRepo
import com.bprodactivv.dataservice.core.exceptions.EntityNotFoundSupplier
import com.bprodactivv.dataservice.core.proql.ProQL
import com.bprodactivv.dataservice.core.proql.models.ProQLQuery
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.metamodel.EntityType
import org.springframework.stereotype.Service


@Service("organization.AppUser")
class AppUserPersistenceRepo(
    private val repo: AppUserRepository,
    private val metadataExtractor: MetadataExtractor,
) : PersistenceRepo, ReadRepo {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    private lateinit var proQL: ProQL<AppUser>

    private lateinit var entity: EntityType<AppUser>

    override fun save(x: Any): Any {
        return repo.save(x as AppUser)
    }

    override fun findById(id: Int): Any {
        return repo.findById(id.toLong()).orElseThrow(EntityNotFoundSupplier())
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
        return mutableListOf()
    }

    private fun initProQL() {
        entity = entityManager.metamodel.entity(AppUser::class.java)
        proQL = ProQL.Builder<AppUser>(entityManager)
            .type(AppUser::class.java)
            .fields(metadataExtractor.getClassDeclaredFields("organization.Customer"))
            .rootEntity(entityManager.metamodel.entity(AppUser::class.java))
            .build()
    }

}