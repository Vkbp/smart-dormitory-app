package com.ktx.dormitory.core.security

import android.util.Base64
import org.json.JSONObject
import java.util.*

object JwtUtils {
    /**
     * Decodes the role from a JWT token payload.
     * Payload usually contains "role" or "authorities".
     */
    fun getRoleFromToken(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null
            
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val json = JSONObject(payload)
            
            // Backend usually puts role in "role" or as a single item in "authorities"
            when {
                json.has("role") -> json.getString("role")
                json.has("authorities") -> {
                    val auth = json.get("authorities")
                    if (auth is String) auth
                    else if (auth is org.json.JSONArray && auth.length() > 0) auth.getString(0)
                    else null
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}
