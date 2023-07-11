package com.bprodactivv.dataservice.core.data.repos.agile

import com.bprodactivv.dataservice.core.data.models.agile.Description
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DescriptionRepository : JpaRepository<Description, Long> {
}