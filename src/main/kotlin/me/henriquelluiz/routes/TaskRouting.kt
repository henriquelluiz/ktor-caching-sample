package me.henriquelluiz.routes

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.resources.delete
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.henriquelluiz.models.Task
import me.henriquelluiz.models.Tasks
import me.henriquelluiz.repositories.TaskRepository
import org.bson.types.ObjectId
import org.koin.ktor.ext.inject

fun Application.configureTaskRoutes() {

    install(Resources)
    install(ContentNegotiation) { json() }
    install(CORS) {
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowHost("localhost")
    }
    val repository by inject<TaskRepository>()

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
            call.respond(
                status = HttpStatusCode.OK,
                message = repository.getAll()
            )
        }

        get<Tasks.Name> {
            val name = call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val task = repository.getByName(name) ?: call.respond(HttpStatusCode.NotFound)
            call.respond(HttpStatusCode.OK, task)
        }

        put<Tasks.Edit> {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val task = call.receive<Task>()
            val modifiedCount = repository.update(ObjectId(id), task)
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
            val task = repository.getById(ObjectId(id)) ?: call.respond(HttpStatusCode.NotFound)
            call.respond(HttpStatusCode.OK, task)
        }

        post<Tasks> {
            val task = call.receive<Task>()
            repository.save(task) ?: return@post call.respond(HttpStatusCode.InternalServerError)
            call.respond(HttpStatusCode.Created)
        }
    }
}