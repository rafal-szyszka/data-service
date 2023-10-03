package com.bprodactivv.dataservice.api.commands

import com.bprodactivv.dataservice.core.proql.ProQLCommandHandler
import com.bprodactivv.dataservice.core.proql.models.ProQLCommand
import org.springframework.cache.annotation.CachePut
import org.springframework.stereotype.Service

@Service
class SaveDataService(
    private val proQLCommandHandler: ProQLCommandHandler
) {
    fun saveData(command: ProQLCommand): Any? {
        return proQLCommandHandler.save(command)
    }

    fun saveAllData(commands: List<ProQLCommand>): List<Any>? {
        return commands.mapNotNull { saveData(it) }
    }

}