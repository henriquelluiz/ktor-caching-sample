package me.henriquelluiz.models

import io.ktor.resources.*
import kotlinx.serialization.Contextual
import org.bson.types.ObjectId

@Resource("/api/tasks")
class Tasks(
    val page: Int? = 0,
    val size: Int? = 1,
) {

    @Resource("new")
    class New(val parent: Tasks = Tasks())

    @Resource("{name}")
    class Name(val parent: Tasks = Tasks(), val name: String)

    @Resource("{id}")
    class Id(val parent: Tasks = Tasks(), @Contextual val id: ObjectId) {
        class Edit(val parent: Id)
        class Delete(val parent: Id)
    }
}
