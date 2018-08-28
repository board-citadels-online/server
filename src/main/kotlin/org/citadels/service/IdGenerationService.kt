package org.citadels.service

import org.springframework.stereotype.Service
import java.util.*

interface IdGenerationService {
    fun generate(): String
}

@Service
class IdGenerationServiceDefault : IdGenerationService {
    override fun generate(): String {
        return UUID.randomUUID().toString()
    }
}