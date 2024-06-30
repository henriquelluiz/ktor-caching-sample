package me.henriquelluiz.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class Task(
    @BsonId val id: ObjectId = ObjectId(),
    val name: String,
    val note: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
