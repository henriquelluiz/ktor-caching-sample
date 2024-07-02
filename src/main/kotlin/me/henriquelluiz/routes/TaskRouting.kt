package me.henriquelluiz.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import me.henriquelluiz.models.Task
import me.henriquelluiz.models.Tasks
import me.henriquelluiz.repositories.TaskRepository
import me.henriquelluiz.utils.CacheManager
import org.bson.types.ObjectId
import org.koin.ktor.ext.inject

fun Application.configureTaskRoutes() {

    install(Resources)
    install(ContentNegotiation) {
        json(Json { prettyPrint = true })
    }

    install(CORS) {
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowHeader("If-None-Match")
        allowHeader("If-Modified-Since")
        allowHost("localhost")
    }

    install(CachingHeaders) {
        options { _, content ->
            when (content.contentType?.withoutParameters()) {
                ContentType.Application.Json -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 120))
                else -> null
            }
        }
    }

    val repository by inject<TaskRepository>()
    val cache by inject<CacheManager>()

    routing {
        get<Tasks.Paginated> {
            val page = call.parameters["page"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val size = call.parameters["size"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)

            cache.handleReadOperations(call, "paginatedTasks", ListSerializer(Task.serializer())) {
                repository.getAllPaginated(page, size)
            }
        }

        get<Tasks> {
            cache.handleReadOperations(call, "tasks", ListSerializer(Task.serializer())) {
                repository.getAll()
            }

        }

        get<Tasks.Name> {
            val name = call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            cache.handleReadOperations(call, name, Task.serializer()) {
                repository.getByName(name)
            }
        }

        put<Tasks.Edit> {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val modifiedCount = repository.update(ObjectId(id), call.receive<Task>())

            if (modifiedCount.toInt() != 1) {
                return@put call.respond(HttpStatusCode.ExpectationFailed)
            } else {
                cache.handleWriteOperations(
                    call = call,
                    statusCode = HttpStatusCode.OK,
                    key = "tasks",
                    serializer = ListSerializer(Task.serializer())
                ) {
                    repository.getAll()
                }
            }
        }

        delete<Tasks.Delete> {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val deletedCount = repository.delete(ObjectId(id))
            if (deletedCount.toInt() != 1) {
                call.respond(HttpStatusCode.ExpectationFailed)
            } else {
                cache.handleWriteOperations(
                    call = call,
                    statusCode = HttpStatusCode.OK,
                    key = "tasks",
                    serializer = ListSerializer(Task.serializer())
                ) {
                    repository.getAll()
                }
            }

        }

        get<Tasks.Id> {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            cache.handleReadOperations(call, id, Task.serializer()) {
                repository.getById(ObjectId(id))
            }
        }

        post<Tasks> {
            val task = call.receive<Task>()
            repository.save(task) ?: return@post call.respond(HttpStatusCode.InternalServerError)

            call.response.header("Location", "/api/tasks/${task.id}")

            cache.handleWriteOperations(
                call = call,
                statusCode = HttpStatusCode.Created,
                key = "tasks",
                serializer = ListSerializer(Task.serializer())
            ) {
                repository.getAll()
            }
        }
    }
}