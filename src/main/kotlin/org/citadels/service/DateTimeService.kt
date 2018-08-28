package org.citadels.service

import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface DateTimeService {
    fun now() : LocalDateTime
}

@Service
class DateTimeServiceDefault : DateTimeService {

    override fun now(): LocalDateTime {
        return LocalDateTime.now()
    }

}