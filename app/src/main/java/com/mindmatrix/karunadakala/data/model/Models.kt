package com.mindmatrix.karunadakala.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class ArtForm(
    @DocumentId val id: String = "",
    val name: String = "",
    val district: String = "",
    val history: String = "",
    val imageUrl: String = "",
    val thumbnailUrl: String = "",
    val category: String = ""   // e.g. "Performing", "Craft", "Textile"
)

data class Artisan(
    @DocumentId val id: String = "",
    val name: String = "",
    val artFormId: String = "",
    val artFormName: String = "",
    val district: String = "",
    val type: String = "",          // "workshop" or "performance"
    val phone: String = "",
    val bio: String = "",
    val photoUrl: String = "",
    val geo: GeoPoint? = null  // ← replaces latitude/longitude
)

data class Event(
    @DocumentId val id: String = "",
    val title: String = "",
    val artFormId: String = "",
    val artFormName: String = "",
    val date: com.google.firebase.Timestamp? = null,
    val venue: String = "",
    val district: String = "",
    val imageUrl: String = ""
)

data class WorkshopSignup(
    val name: String = "",
    val phone: String = "",
    val artFormInterest: String = "",
    val timestamp: com.google.firebase.Timestamp? = null
)
