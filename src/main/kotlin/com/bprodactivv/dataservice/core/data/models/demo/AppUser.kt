package com.bprodactivv.dataservice.core.data.models.demo

import jakarta.persistence.*

@Entity
@Table(name = "demo_app_user")
open class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "User_GEN")
    @SequenceGenerator(name = "User_GEN", sequenceName = "User_SEQ")
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(nullable = false, unique = true)
    open var email: String? = null

    @Column(nullable = false)
    open var password: String? = null

    @Column(nullable = false)
    open var name: String? = null

    @Column(nullable = false)
    open var lastName: String? = null
}