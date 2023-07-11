package com.bprodactivv.dataservice.core.data.repos.demo

import com.bprodactivv.dataservice.core.data.models.demo.DemoProject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : JpaRepository<DemoProject, Long> {}