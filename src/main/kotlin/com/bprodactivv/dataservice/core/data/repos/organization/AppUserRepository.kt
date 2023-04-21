package com.bprodactivv.dataservice.core.data.repos.organization

import com.bprodactivv.dataservice.core.data.models.organization.AppUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AppUserRepository : JpaRepository<AppUser, Long> {}