package com.bprodactivv.dataservice.core.data.repos.prodactivvity

import com.bprodactivv.dataservice.core.data.models.prodactivvity.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : JpaRepository<Project, Long> {}