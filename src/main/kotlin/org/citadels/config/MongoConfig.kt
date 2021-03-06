package org.citadels.config

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.citadels.RoomRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableReactiveMongoRepositories(basePackageClasses = [RoomRepository::class])
class MongoConfig : AbstractReactiveMongoConfiguration() {

    @Bean
    fun mongoClient(): MongoClient = MongoClients.create()

    @Bean
    override fun reactiveMongoTemplate() = ReactiveMongoTemplate(mongoClient(), databaseName)

    override fun reactiveMongoClient() = mongoClient()

    override fun getDatabaseName() = "reactive"
}
