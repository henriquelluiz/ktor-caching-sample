package me.henriquelluiz.repositories

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import me.henriquelluiz.models.Task
import org.bson.BsonValue
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory

class TaskRepositoryImpl(
    private val db: MongoDatabase
) : TaskRepository {

    companion object {
        const val COLLECTION = "tasks"
    }

    private val collection by lazy { db.getCollection<Task>(COLLECTION) }

    override suspend fun getAll(): List<Task> = handleDatabaseOperation {
        collection.find<Task>().toList()
    } ?: emptyList()

    override suspend fun getAllPaginated(page: Int, size: Int): List<Task> = handleDatabaseOperation {
        collection.find<Task>()
            .skip(page * size)
            .limit(size)
            .toList()
    } ?: emptyList()

    override suspend fun getById(id: ObjectId): Task? = handleDatabaseOperation {
        collection.find<Task>(
            Filters.eq("_id", id)
        ).firstOrNull()
    }

    override suspend fun getByName(name: String): Task? = handleDatabaseOperation {
        collection.find<Task>(
            Filters.eq("name", name)
        ).firstOrNull()
    }

    override suspend fun save(task: Task): BsonValue? = handleDatabaseOperation {
        collection.insertOne(task)
            .insertedId
    }

    override suspend fun update(id: ObjectId, task: Task): Long = handleDatabaseOperation {
        val filter = Filters.eq("_id", id)
        val updates = mutableListOf<Bson>()
        updates.add(Updates.set(Task::name.name, task.name))
        updates.add(Updates.set(Task::note.name, task.note))
        if (task.createdAt != null) { Updates.set(Task::createdAt.name, task.createdAt) }

        collection.updateOne(filter, Updates.combine(updates))
            .modifiedCount
    } ?: 0

    override suspend fun delete(id: ObjectId): Long = handleDatabaseOperation {
        collection.deleteOne(Filters.eq("_id", id))
            .deletedCount
    } ?: 0

    private inline fun <T> handleDatabaseOperation(operation: () -> T): T? {
        val log = LoggerFactory.getLogger(this::class.java)
        return try {
            log.info("An operation has executed in database")
            operation()

        } catch (ex: Exception) {
            log.error("Database operation failed: ${ex.message}")
            null
        }
    }
}