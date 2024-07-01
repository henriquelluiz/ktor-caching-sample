package me.henriquelluiz.models

import io.ktor.resources.*
import kotlinx.serialization.Serializable
import me.henriquelluiz.models.serealization.ObjectIdSerializer
import org.bson.types.ObjectId

@Suppress("unused")
@Resource("/api/tasks")
class Tasks {

    @Resource("paginated{page}{size}")
    class Paginated(
        val parent: Tasks = Tasks(),
        val page: Int? = 0,
        val size: Int? = 1
    )

    @Resource("new")
    class New(val parent: Tasks = Tasks())

    @Resource("{name}")
    class Name(val parent: Tasks = Tasks(), val name: String)

    @Resource("{id}")
    class Id(
        val parent: Tasks = Tasks(),
        @Serializable(with = ObjectIdSerializer::class) val id: ObjectId
    )

    @Resource("edit{id}")
    class Edit(
        val parent: Tasks = Tasks(),
        @Serializable(with = ObjectIdSerializer::class) val id: ObjectId
    )

    @Resource("delete{id}")
    class Delete(
        val parent: Tasks = Tasks(),
        @Serializable(with = ObjectIdSerializer::class) val id: ObjectId
    )
}
