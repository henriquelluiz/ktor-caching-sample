package me.henriquelluiz.models

import kotlinx.serialization.Serializable
import me.henriquelluiz.models.serealization.LocalDateSerializer
import me.henriquelluiz.models.serealization.ObjectIdSerializer
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

@Serializable
data class Task(
    @BsonId
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId = ObjectId(),

    val name: String,
    val note: String,

    @Serializable(with = LocalDateSerializer::class)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
