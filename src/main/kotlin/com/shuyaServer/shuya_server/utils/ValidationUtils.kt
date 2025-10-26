package com.shuyaServer.shuya_server.utils

import org.bson.types.ObjectId

object ValidationUtils {
    fun isValidateObjectId(id:String?): Boolean{
        if(id.isNullOrBlank()) return false
        return ObjectId.isValid(id)
    }
}