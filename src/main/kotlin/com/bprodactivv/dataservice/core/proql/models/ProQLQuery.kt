package com.bprodactivv.dataservice.core.proql.models

open class ProQLQuery(
    open var type: String,
    open var properties: MutableMap<String, Any>?,
    open var subQueries: List<ProQLSubQuery>?,
)

class ProQLSubQuery(
    override var type: String,
    override var properties: MutableMap<String, Any>?,
    override var subQueries: List<ProQLSubQuery>?,
    var parentProperty: String,
) : ProQLQuery(type, properties, subQueries)
