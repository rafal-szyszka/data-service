package com.bprodactivv.dataservice.core.data.repos.core

import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.bprodactivv.dataservice.core.data.models.core.SysUser
import com.bprodactivv.dataservice.core.data.repos.AbstractPersistenceService
import com.bprodactivv.dataservice.core.proql.models.ProQLQuery
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import java.util.*

@Service(SysUser.CLASS_TYPE)
class SysUserPersistenceService(
    private val repository: SysUserRepository,
    metadataExtractor: MetadataExtractor,
) : AbstractPersistenceService<SysUser>(
    metadataExtractor,
    SysUser::class.java,
    SysUser.CLASS_TYPE
) {
    override fun save(x: Any): Any {
        return repository.save(x as SysUser)
    }

    override fun getRepository(): JpaRepository<SysUser, Long> {
        return repository
    }

    override fun addJoinsWithPredicates(proQLQuery: ProQLQuery): MutableList<Predicate> {
        return mutableListOf()
    }
}