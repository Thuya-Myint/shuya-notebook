package com.shuyaServer.shuya_server.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import java.time.Instant

data class Note(
    val title: String="",
    val content: String="",
    val color: Long=0L,
    val createdAt:Instant= Instant.now(),
    val updatedAt: Instant= Instant.now(),
    val ownerId: ObjectId= ObjectId.get(),
    val remindTime: Instant= Instant.now(),
    @Id val id : ObjectId = ObjectId.get()

)