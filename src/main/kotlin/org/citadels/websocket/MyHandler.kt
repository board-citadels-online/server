package org.citadels.websocket

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.citadels.domain.Greeting
import org.citadels.domain.HelloMessage
import org.citadels.service.GreetingService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.io.IOException

@Component
class MyHandler(
        private val objectMapper: ObjectMapper,
        private val greetingService: GreetingService
) : WebSocketHandler {

    override fun handle(session: WebSocketSession): Mono<Void> {
        val responseFlux = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(this::deserialize)
                .map(greetingService::toMessage)
                .map(this::serialize)
                .map(session::textMessage)

        return session.send(responseFlux)
    }

    private fun deserialize(json: String): HelloMessage {
        try {
            return objectMapper.readValue(json)
        } catch (e: IOException) {
            throw RuntimeException("Invalid JSON:$json", e)
        }
    }

    private fun serialize(greeting: Greeting): String {
        try {
            return objectMapper.writeValueAsString(greeting)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }
}
