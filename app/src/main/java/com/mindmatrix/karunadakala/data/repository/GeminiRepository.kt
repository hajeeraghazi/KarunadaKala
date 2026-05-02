package com.mindmatrix.karunadakala.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRepository @Inject constructor(
    private val generativeModel: GenerativeModel
) {
    suspend fun generateArtFormDescription(artFormName: String): Result<String> = try {
        val prompt = """
            Write a 3-sentence cultural description of $artFormName from Karnataka, India, 
            suitable for a heritage discovery app. Include its historical origin, what makes 
            it unique, and why it matters for Karnataka's identity. 
            Write in simple, engaging English. Do not use bullet points.
        """.trimIndent()
        val response = generativeModel.generateContent(prompt)
        val text = response.text
        if (!text.isNullOrBlank()) Result.Success(text.trim())
        else Result.Error("Empty response from Gemini")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Gemini API call failed")
    }
}
