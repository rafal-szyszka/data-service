package com.bprodactivv.dataservice.core.data.models.demo

import jakarta.persistence.*

@Entity
@Table(
    name = "demo_customer", indexes = [
        Index(name = "idx_customer_name", columnList = "name"),
        Index(name = "idx_customer_taxnumber", columnList = "taxNumber")
    ]
)
open class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Customer_GEN")
    @SequenceGenerator(name = "Customer_GEN", sequenceName = "Customer_SEQ")
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    open var name: String? = null

    open var alias: String? = null

    open var taxNumber: String? = null

    @ManyToMany
    @JoinTable(
        name = "customer_projects",
        joinColumns = [JoinColumn(name = "customer_id")],
        inverseJoinColumns = [JoinColumn(name = "project_id")]
    )
    open var demoProjects: MutableList<DemoProject>? = null
}
