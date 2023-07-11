package com.bprodactivv.dataservice.core.data.models.demo

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

@Entity
@Table(name = "demo_project")
@JsonInclude(JsonInclude.Include.NON_NULL)
open class DemoProject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Project_GEN")
    @SequenceGenerator(name = "Project_GEN", sequenceName = "Project_SEQ")
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    open var name: String? = null

    @ManyToMany(mappedBy = "demoProjects")
    @Fetch(FetchMode.SELECT)
    @JsonBackReference
    open var customers: MutableList<Customer>? = null
}