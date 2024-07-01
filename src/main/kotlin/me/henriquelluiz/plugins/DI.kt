package me.henriquelluiz.plugins

import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.server.application.*
import me.henriquelluiz.repositories.TaskRepository
import me.henriquelluiz.repositories.TaskRepositoryImpl
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single {
                    MongoClient.create(
                        environment.config.propertyOrNull("ktor.mongo.uri")?.getString()
                            ?: throw RuntimeException("Cannot access MongoDB URI")
                    )
                }
                single {
                    get<MongoClient>().getDatabase(
                        environment.config.property("ktor.mongo.database").getString()
                    )
                }
            },

            module { single<TaskRepository> { TaskRepositoryImpl(get()) } }
        )
    }
}