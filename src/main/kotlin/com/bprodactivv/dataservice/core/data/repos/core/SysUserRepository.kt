package com.bprodactivv.dataservice.core.data.repos.core

import com.bprodactivv.dataservice.core.data.models.core.SysUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SysUserRepository : JpaRepository<SysUser, Long> {
}