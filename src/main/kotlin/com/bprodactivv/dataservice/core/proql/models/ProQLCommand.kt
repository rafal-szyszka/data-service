package com.bprodactivv.dataservice.core.proql.models

class ProQLCommand(
    var type: String,
    var parentProperty: String?,
    var properties: MutableMap<String, Any>?,
    var commands: List<ProQLCommand>?,
) {
}