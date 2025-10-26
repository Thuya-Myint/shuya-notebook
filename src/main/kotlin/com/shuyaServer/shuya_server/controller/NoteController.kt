package com.shuyaServer.shuya_server.controller
import com.shuyaServer.shuya_server.database.model.Note
import com.shuyaServer.shuya_server.database.repository.NoteRepository
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import com.shuyaServer.shuya_server.dto.ApiResponse
import com.shuyaServer.shuya_server.utils.ValidationUtils
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/notes")

class NoteController(private val repository: NoteRepository) {

    data class NoteRequest(
        val id:String?,
        val title:String,
        val content:String,
        val color:Long,
        val ownerId:String?,
        val remindTime: Instant,
    )
    data class NoteResponse(
        val id:String?,
        val title:String,
        val content:String,
        val createdAt: Instant,
        val updatedAt:Instant,
        val remindTime:Instant,
        val color:Long
    )

    private val log= LoggerFactory.getLogger(NoteController::class.java)

    @PostMapping
    fun save(@RequestBody body:NoteRequest): ResponseEntity<ApiResponse<NoteResponse>>{
        if( !ValidationUtils.isValidateObjectId(body.ownerId)){
            log.warn("ownerId is not valid Object Id ${body.ownerId}")
           return ResponseEntity
               .badRequest()
               .body(ApiResponse(success = false, message = "Failed to create note!",data=null))
        }
        return try{
            val note = repository.save(
                Note(
                    id=body.id?.let{ObjectId(it)}?:ObjectId.get(),
                    title=body.title,
                    content=body.content,
                    color=body.color,
                    createdAt=Instant.now(),
                    updatedAt = Instant.now(),
                    remindTime = body.remindTime,
                    ownerId=ObjectId(body.ownerId)
                )
            )
            log.info("Successfully created note with id=${note.id}")
            ResponseEntity
                .status(200)
                .body(ApiResponse(success = true, message="Successfully created note!", data=note.toResponse()))
        }catch (ex: Exception){
            log.error("Error Creating note ${ex.message}", ex)
            ResponseEntity
                .internalServerError()
                .body(ApiResponse(success = false, message="Internal server Error! Failed to create note!"))
        }
    }
    @GetMapping
    fun findByOwnerId(@RequestParam(required = true)ownerId: String?): ResponseEntity<ApiResponse<List<NoteResponse>>>{
        if(!ObjectId.isValid(ownerId)){
            return ResponseEntity
                .badRequest()
                .body(ApiResponse(success = false, message="Invalid owner id!", data=null))
        }
        val note= repository.findByOwnerId(ObjectId(ownerId)).map{ it.toResponse()}
        return ResponseEntity
            .status(200)
            .body(ApiResponse(success = true, message="Successfully fetch data!", data = note))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id:String, @RequestBody body: NoteRequest): ResponseEntity<ApiResponse<NoteResponse>>{
        if(!ValidationUtils.isValidateObjectId(id)){
            return ResponseEntity
                .badRequest()
                .body(ApiResponse(success = false, message="Invalid id!", data=null))
        }
        val noteId=ObjectId(id)
        val existingNote=repository.findById(noteId).orElse(null)?: return ResponseEntity
            .badRequest()
            .body(ApiResponse(
            success=false,
            message="Note not found!",
            data=null
        ))
        val updatedNote=repository.save(
            existingNote.copy(
                title=body.title,
                content=body.content,
                color=body.color,
                updatedAt = Instant.now()
            )
        )
        return ResponseEntity
            .badRequest()
            .body(ApiResponse(success = true, message="Successfully updated note!", data=updatedNote.toResponse()))
    }
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id:String): ResponseEntity<ApiResponse<NoteResponse>>{
        if(!ValidationUtils.isValidateObjectId(id)){
            return ResponseEntity
                .badRequest()
                .body(ApiResponse(success = false, message="Invalid id!", data=null))
        }
        val noteId= ObjectId(id)
        val existingNote=repository.findById(noteId).orElse(null)?: return ResponseEntity
            .badRequest()
            .body(ApiResponse(
            success=false,
            message="Note not found!",
            data=null
        ))
        val deletedNote=repository.delete(existingNote)
        return ResponseEntity
            .status(200)
            .body(ApiResponse(success = true, message="Note deleted successfully!"))
    }
    private fun Note.toResponse(): NoteResponse{
        return NoteResponse(
            id=id.toHexString(),
            title=title,
            content=content,
            createdAt = createdAt,
            updatedAt=updatedAt,
            color=color,
            remindTime = remindTime
        )
    }
}