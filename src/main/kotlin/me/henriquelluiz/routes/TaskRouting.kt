package me.henriquelluiz.routes

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import me.henriquelluiz.models.Task
import me.henriquelluiz.routes.typesafe.Tasks
import me.henriquelluiz.models.setId
import me.henriquelluiz.repositories.TaskRepository
import me.henriquelluiz.utils.CacheManager
import org.bson.types.ObjectId
import org.koin.ktor.ext.inject
import java.time.LocalDateTime

fun Application.configureTaskRoutes() {

    install(ContentNegotiation) {
        json(Json { prettyPrint = true })
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
            val task = repository.getByName(name) ?: return@get call.respond(HttpStatusCode.NotFound)
            cache.handleReadOperations(call, task.id.toString(), Task.serializer()) { task }
        }

        put<Tasks.Edit> {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val task = call.receive<Task>()
            val objectId = ObjectId(id)
            val modifiedCount = repository.update(objectId, task)

            if (modifiedCount.toInt() != 1) {
                return@put call.respond(HttpStatusCode.ExpectationFailed)
            } else {
                task.setId(objectId)
                cache.updateSingleData(
                    key = id,
                    serializer = Task.serializer(),
                    data = task
                )

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
                cache.invalidateData(id)
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
            if (task.createdAt == null) {
                task.createdAt = LocalDateTime.now()
            }
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