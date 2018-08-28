package org.citadels.service

import org.citadels.domain.Greeting
import org.citadels.domain.HelloMessage

interface GreetingService {
    fun toMessage(greeting: HelloMessage): Greeting
}
