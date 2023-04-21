package com.bprodactivv.dataservice.core.data.models.prodactivvity

import com.bprodactivv.dataservice.core.data.models.organization.Customer
import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
open class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Project_GEN")
    @SequenceGenerator(name = "Project_GEN", sequenceName = "Project_SEQ")
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    open var name: String? = null

    @ManyToMany(mappedBy = "projects")
    @Fetch(FetchMode.SELECT)
    @JsonBackReference
    open var customers: MutableList<Customer>? = null
}