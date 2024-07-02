package me.henriquelluiz.routes

import io.github.reactivecircus.cache4k.Cache
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
import me.henriquelluiz.utils.generateETag
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
    val cache = Cache.Builder<String, String>().build()

    routing {
        get<Tasks.Paginated> {
            val page = call.parameters["page"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val size = call.parameters["size"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)

            call.respond(
                status = HttpStatusCode.OK,
                message = repository.getAllPaginated(page, size)
            )
        }

        get<Tasks> {
            val cachedTasks = cache.get("tasks")
            if (cachedTasks != null) {
                val etag = generateETag(cachedTasks)
                call.response.header(HttpHeaders.ETag, etag)

                val clientETag = call.request.header(HttpHeaders.IfNoneMatch)
                if (clientETag == etag) {
                    call.respond(HttpStatusCode.NotModified)
                } else {
                    call.respondText(
                        status = HttpStatusCode.OK,
                        text = cachedTasks,
                        contentType = ContentType.Application.Json,
                    )
                }
            } else {
                val tasks = repository.getAll()
                val encodedTasks = Json.encodeToString(ListSerializer(Task.serializer()), tasks)
                cache.put("tasks", encodedTasks)
                call.respond(HttpStatusCode.OK, tasks)
            }
        }

        get<Tasks.Name> {
            val name = call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val task = repository.getByName(name) ?: call.respond(HttpStatusCode.NotFound)
            call.respond(HttpStatusCode.OK, task)
        }

        put<Tasks.Edit> {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val modifiedCount = repository.update(ObjectId(id), call.receive<Task>())
            if (modifiedCount < 1L) {
                return@put call.respond(HttpStatusCode.InternalServerError)
            }
            call.respond(HttpStatusCode.OK)
        }

        delete<Tasks.Delete> {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            repository.delete(ObjectId(id))
            call.respond(HttpStatusCode.OK)
        }

        get<Tasks.Id> {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val cachedTask = cache.get(id)

            if (cachedTask != null) {
                val etag = generateETag(cachedTask)
                call.response.header(HttpHeaders.ETag, etag)

                val clientETag = call.request.header(HttpHeaders.IfNoneMatch)
                if (clientETag == etag) {
                    call.respond(HttpStatusCode.NotModified)
                } else {
                    call.respondText(
                        status = HttpStatusCode.OK,
                        text = cachedTask,
                        contentType = ContentType.Application.Json
                    )
                }

            } else {
                val task = repository.getById(ObjectId(id)) ?: return@get call.respond(HttpStatusCode.NotFound)
                val jsonTask = Json.encodeToString(Task.serializer(), task)
                cache.put(id, jsonTask)
                call.respond(HttpStatusCode.OK, task)
            }
        }

        post<Tasks> {
            val task = call.receive<Task>()
            repository.save(task) ?: return@post call.respond(HttpStatusCode.InternalServerError)
            call.response.header("Location", "/api/tasks/${task.id}")
            call.respond(HttpStatusCode.Created)
        }
    }
}