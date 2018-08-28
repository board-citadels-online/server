package org.citadels.config

import org.citadels.websocket.MyHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@Configuration
class WebSocketConfig {

    @Bean
    fun webSocketMapping(myHandler: MyHandler): HandlerMapping {
        return SimpleUrlHandlerMapping().apply {
            urlMap = mapOf("/citadels" to myHandler)
            order = 10
        }
    }

    @Bean
    fun handlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter()
    }

}
