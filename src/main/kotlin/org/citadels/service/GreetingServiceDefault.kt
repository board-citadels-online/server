package org.citadels.service

import org.citadels.domain.Greeting
import org.citadels.domain.HelloMessage
import org.springframework.stereotype.Service
import org.springframework.web.util.HtmlUtils.htmlEscape

@Service
class GreetingServiceDefault : GreetingService {

    override fun toMessage(greeting: HelloMessage): Greeting {
        val escapedName = htmlEscape(greeting.name)
        return Greeting("""Hello, $escapedName!""")
    }

}
