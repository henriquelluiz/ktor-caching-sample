package me.henriquelluiz.repositories

import me.henriquelluiz.models.Task
import org.bson.BsonValue
import org.bson.types.ObjectId

interface TaskRepository {
    suspend fun getAll(): List<Task>
    suspend fun getAllPaginated(page: Int, size: Int): List<Task>
    suspend fun getById(id: ObjectId): Task?
    suspend fun getByName(name: String): Task?
    suspend fun save(task: Task): BsonValue?
    suspend fun update(id: ObjectId, task: Task): Long
    suspend fun delete(id: ObjectId): Long
}