package org.citadels

import org.citadels.service.DateTimeService
import org.citadels.service.IdGenerationService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.ConnectableFlux
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import javax.annotation.PostConstruct

data class SlotAvailability(val free: Int, val total: Int) {
    val occupied = total - free //TODO ignore from json
}

@Document
data class Room(
        val id: String,
        val created: LocalDateTime,
        val name: String,
        val slots: SlotAvailability
)

@Repository
interface RoomRepository : ReactiveMongoRepository<Room, String> {
    fun findBy(page: Pageable): Flux<Room>
}

interface RoomService {
    fun create(creationRequest: RoomCreationRequest): Mono<Room>

    fun findOne(id: String): Mono<Room>

    fun findAll(pageNumber: Int): Flux<Room>

    fun findAllAndSubscribe(): Flux<Room>
}

@Service
class RoomServiceDefault(
        private val repository: RoomRepository,
        private val idGenerationService: IdGenerationService,
        private val dateTimeService: DateTimeService
) : RoomService {

    companion object {
        const val pageSize = 10
    }

    @PostConstruct
    fun init() {
        upcomingFlux.connect()
    }

    override fun create(creationRequest: RoomCreationRequest): Mono<Room> {
        val newRoom = Room(
                idGenerationService.generate(),
                dateTimeService.now(),
                creationRequest.name,
                SlotAvailability(0, 10)
        )

        return repository.save(newRoom)
    }

    override fun findOne(id: String): Mono<Room> {
        return repository.findById(id)
    }

    override fun findAll(pageNumber: Int): Flux<Room> {
        return repository.findBy(PageRequest.of(pageNumber, pageSize))
    }

    override fun findAllAndSubscribe(): Flux<Room> {
        return repository.findAll()
                .concatWith(upcomingFlux)
    }

    private val upcomingFlux: ConnectableFlux<Room> = Flux.empty<Room>().publish()
            /*Flux.interval(Duration.ofSeconds(5))
                    .flatMap {
                        Mono.fromCallable {
                            Room(it.toString(), dateTimeService.now())
                        }
                    }
                    .publish()*/
}

data class RoomCreationRequest(val name: String)

@RestController
@RequestMapping("room")
class RoomController(private val service: RoomService) {

    @PostMapping
    fun create(@RequestBody creationRequest: RoomCreationRequest): Mono<Void> {
        return service.create(creationRequest)
                .then()
    }

    @GetMapping(
            value = ["stream"],
            produces = [MediaType.APPLICATION_STREAM_JSON_VALUE]
    )
    fun findAllAndSubscribe(): Flux<Room> {
        return service.findAllAndSubscribe()
    }

    @GetMapping
    fun findAll(
            @RequestParam(value = "page", defaultValue = "0") pageNumber: Int
    ): Flux<Room> {
        return service.findAll(pageNumber)
    }

    @GetMapping("{id}")
    fun findOne(@PathVariable("id") id: String): Mono<Room> {
        return service.findOne(id)
    }

}
