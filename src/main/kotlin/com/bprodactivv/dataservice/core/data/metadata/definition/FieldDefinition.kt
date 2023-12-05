package com.bprodactivv.dataservice.core.data.metadata.definition

class FieldDefinition(
    val name: String,
    val type: String,
    val constraints: List<Constraints>,
    val multiplicity: String,
) {
}