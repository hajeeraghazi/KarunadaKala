package com.mindmatrix.karunadakala.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.mindmatrix.karunadakala.data.model.ArtForm
import com.mindmatrix.karunadakala.data.model.Artisan
import com.mindmatrix.karunadakala.data.model.Event
import com.mindmatrix.karunadakala.data.model.WorkshopSignup
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

@Singleton
class KarunadaRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // ── Art Forms ────────────────────────────────────────────────────────────

    suspend fun getArtForms(): Result<List<ArtForm>> = try {
        val snapshot = firestore.collection("artforms").get().await()
        Result.Success(snapshot.toObjects(ArtForm::class.java))
    } catch (e: Exception) {
        Result.Error(e.message ?: "Failed to load art forms")
    }

    suspend fun getArtFormById(id: String): Result<ArtForm> = try {
        val doc = firestore.collection("artforms").document(id).get().await()
        val artForm = doc.toObject(ArtForm::class.java)
        if (artForm != null) Result.Success(artForm)
        else Result.Error("Art form not found")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Failed to load art form")
    }

    // ── Artisans ─────────────────────────────────────────────────────────────

    suspend fun getArtisans(): Result<List<Artisan>> = try {
        val snapshot = firestore.collection("artisans").get().await()
        Result.Success(snapshot.toObjects(Artisan::class.java))
    } catch (e: Exception) {
        Result.Error(e.message ?: "Failed to load artisans")
    }

    suspend fun getArtisanById(id: String): Result<Artisan> = try {
        val doc = firestore.collection("artisans").document(id).get().await()
        val artisan = doc.toObject(Artisan::class.java)
        if (artisan != null) Result.Success(artisan)
        else Result.Error("Artisan not found")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Failed to load artisan")
    }

    suspend fun getArtisansByArtForm(artFormId: String): Result<List<Artisan>> = try {
        val snapshot = firestore.collection("artisans")
            .whereEqualTo("artFormId", artFormId).get().await()
        Result.Success(snapshot.toObjects(Artisan::class.java))
    } catch (e: Exception) {
        Result.Error(e.message ?: "Failed to load artisans")
    }

    // ── Events ───────────────────────────────────────────────────────────────

    suspend fun getEvents(): Result<List<Event>> = try {
        val snapshot = firestore.collection("events")
            .orderBy("date", Query.Direction.ASCENDING).get().await()
        Result.Success(snapshot.toObjects(Event::class.java))
    } catch (e: Exception) {
        Result.Error(e.message ?: "Failed to load events")
    }

    // ── Workshop Signup ──────────────────────────────────────────────────────

    suspend fun submitSignup(signup: WorkshopSignup): Result<Unit> = try {
        val data = signup.copy(timestamp = Timestamp.now())
        firestore.collection("signups").add(data).await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Failed to submit signup")
    }

    // ── Seed Data (call once from admin screen, then remove) ─────────────────

    suspend fun seedFirestore() {
        val db    = firestore
        val batch = db.batch()

        // ── Art Forms ──────────────────────────────────────────────────────
        val artForms = listOf(
            mapOf("name" to "Yakshagana", "district" to "Uttara Kannada",
                "category" to "Performing",
                "history" to "Yakshagana is a traditional theatre form that combines dance, music, dialogue, costume, makeup, and stage techniques with a unique style and form. It is native to coastal Karnataka and has a history spanning several centuries. The art form is typically performed overnight in open-air stages and draws from epics like Mahabharata and Ramayana. The elaborate headgear (mundaasae), vibrant costumes, and dramatic face paint make it visually stunning. It is recognised by the Sangeet Natak Akademi as one of India's important classical theatre forms.",
                "imageUrl"     to "https://img.youtube.com/vi/kWEfDsqYtyI/maxresdefault.jpg",
                "thumbnailUrl" to "https://img.youtube.com/vi/kWEfDsqYtyI/maxresdefault.jpg"),
            mapOf("name" to "Bidriware", "district" to "Bidar",
                "category" to "Craft",
                "history" to "Bidriware is a metal handicraft from Bidar, Karnataka, India. It involves casting an alloy of zinc and copper, then inlaying it with pure silver. The blackened surface contrasts strikingly with the bright silver inlay patterns. This craft dates back to the 14th century CE during the Bahmani Sultanate and was brought to India by Iranian artisans. Today Bidriware holds a Geographical Indication (GI) tag and is exported globally. The motifs typically include floral and geometric patterns derived from Persian art.",
                "imageUrl"     to "https://img.youtube.com/vi/WztQifnSdNg/maxresdefault.jpg",
                "thumbnailUrl" to "https://img.youtube.com/vi/WztQifnSdNg/maxresdefault.jpg"),
            mapOf("name" to "Kinnala Toys", "district" to "Koppal",
                "category" to "Craft",
                "history" to "Kinnala toys are hand-crafted wooden toys made in the village of Kinnala in Koppal district, Karnataka. The toys are made from soft white wood (Hale mara), painted in bright natural colours, and are known for their distinctive style featuring animals, birds, and mythological figures. The craft has been passed down through generations of the Chitari community. Kinnala toys received the Geographical Indication (GI) tag in 2019. They are eco-friendly and non-toxic, making them popular with parents as alternatives to plastic toys.",
                "imageUrl"     to "https://img.youtube.com/vi/6TrVy2SleyM/maxresdefault.jpg",
                "thumbnailUrl" to "https://img.youtube.com/vi/6TrVy2SleyM/maxresdefault.jpg"),
            mapOf("name" to "Ilkal Saree", "district" to "Bagalkot",
                "category" to "Textile",
                "history" to "The Ilkal saree is a handloom saree produced in Ilkal town, Bagalkot district, Karnataka. It is uniquely identified by its distinctive 'tope teni' border joining technique, where the body and the pallu are woven separately and then joined. The saree uses a combination of silk and cotton threads. The characteristic red-and-white contrast, combined with kasuti embroidery, makes it one of Karnataka's most recognised traditional garments. Ilkal sarees hold a Geographical Indication tag and are worn across generations for festivals and religious occasions.",
                "imageUrl"     to "https://img.youtube.com/vi/_IlI1u_Uwsg/maxresdefault.jpg",
                "thumbnailUrl" to "https://img.youtube.com/vi/_IlI1u_Uwsg/maxresdefault.jpg"),
            mapOf("name" to "Dollu Kunitha", "district" to "Tumkur",
                "category" to "Performing",
                "history" to "Dollu Kunitha is a major folk dance form of Karnataka performed predominantly by the Kuruba community. It involves vigorous dancing to the beats of dollu (a large drum hung from the neck). The performance is characterised by its high energy, disciplined formations, and thunderous drumming. Groups of 16 or more performers dance in perfect synchrony while playing their instruments. It is traditionally performed during festivals like Ugadi and at temple fairs. Dollu Kunitha was featured in Republic Day celebrations in New Delhi, bringing it national recognition.",
                "imageUrl"     to "https://img.youtube.com/vi/AzWwHNaSIqA/maxresdefault.jpg",
                "thumbnailUrl" to "https://img.youtube.com/vi/AzWwHNaSIqA/maxresdefault.jpg")
        )
        val artFormIds = listOf("yakshagana", "bidriware", "kinnala", "ilkal", "dollu")
        artFormIds.zip(artForms).forEach { (id, data) ->
            batch.set(db.collection("artforms").document(id), data)
        }

        // ── Artisans ───────────────────────────────────────────────────────
        val artisans = listOf(
            mapOf("name" to "Ramesh Gowda", "artFormId" to "yakshagana", "artFormName" to "Yakshagana",
                "district" to "Dharwad", "type" to "performance", "phone" to "+919876543210",
                "bio"      to "Ramesh Gowda has performed Yakshagana for over 25 years and is a recipient of the Karnataka Rajyotsava Award. He specialises in portraying mythological characters with authentic traditional costumes.",
                "photoUrl" to "", "geo" to GeoPoint(15.4589, 75.0078)),
            mapOf("name" to "Kaveri Devi", "artFormId" to "bidriware", "artFormName" to "Bidriware",
                "district" to "Bidar", "type" to "workshop", "phone" to "+919845123456",
                "bio"      to "Kaveri Devi is a third-generation Bidriware artisan from Bidar. She runs a workshop teaching the inlay technique and exports her work to galleries in Bengaluru and Mumbai.",
                "photoUrl" to "", "geo" to GeoPoint(17.9240, 77.5199)),
            mapOf("name" to "Suresh Chitari", "artFormId" to "kinnala", "artFormName" to "Kinnala Toys",
                "district" to "Koppal", "type" to "workshop", "phone" to "+919741258963",
                "bio"      to "Suresh Chitari is a master toy maker from Kinnala village. He conducts weekend workshops for children and has trained over 200 students in natural paint techniques.",
                "photoUrl" to "", "geo" to GeoPoint(15.3548, 76.1553)),
            mapOf("name" to "Annapurna Bai", "artFormId" to "ilkal", "artFormName" to "Ilkal Saree",
                "district" to "Bagalkot", "type" to "workshop", "phone" to "+919632147852",
                "bio"      to "Annapurna Bai is a master weaver with 30 years of experience in Ilkal handloom. She teaches the tope teni joining technique and kasuti embroidery at her home studio.",
                "photoUrl" to "", "geo" to GeoPoint(16.1765, 75.6930)),
            mapOf("name" to "Manjunath Tala", "artFormId" to "dollu", "artFormName" to "Dollu Kunitha",
                "district" to "Tumkur", "type" to "performance", "phone" to "+919517534826",
                "bio"      to "Manjunath Tala leads a 20-member Dollu Kunitha troupe and has performed at Republic Day celebrations in New Delhi. He trains youth groups in rural Tumkur.",
                "photoUrl" to "", "geo" to GeoPoint(13.3409, 77.1010)),
            mapOf("name" to "Girija Naik", "artFormId" to "yakshagana", "artFormName" to "Yakshagana",
                "district" to "Udupi", "type" to "performance", "phone" to "+919988776655",
                "bio"      to "Girija Naik is one of Karnataka's few female Yakshagana artists and runs a Guru-Shishya style school in Udupi. She has taken Yakshagana to stages in the USA and UK.",
                "photoUrl" to "", "geo" to GeoPoint(13.3409, 74.7421)),
            mapOf("name" to "Prakash Bidri", "artFormId" to "bidriware", "artFormName" to "Bidriware",
                "district" to "Bidar", "type" to "workshop", "phone" to "+919123456789",
                "bio"      to "Prakash Bidri is a National Award winning Bidriware craftsman. His workshop in Bidar offers intensive 3-day courses on the casting and silver inlay process.",
                "photoUrl" to "", "geo" to GeoPoint(17.9100, 77.5350))
        )
        val artisanIds = listOf(
            "artisan_001", "artisan_002", "artisan_003", "artisan_004",
            "artisan_005", "artisan_006", "artisan_007"
        )
        artisanIds.zip(artisans).forEach { (id, data) ->
            batch.set(db.collection("artisans").document(id), data)
        }

        // ── Events ─────────────────────────────────────────────────────────
        val now = System.currentTimeMillis()
        val day = 86400000L
        val events = listOf(
            mapOf("title" to "Yakshagana Utsav 2026", "artFormId" to "yakshagana", "artFormName" to "Yakshagana",
                "date" to Timestamp(now / 1000 + 5  * day / 1000, 0),
                "venue" to "Town Hall, Dharwad", "district" to "Dharwad", "imageUrl" to ""),
            mapOf("title" to "Dollu Kunitha Night", "artFormId" to "dollu", "artFormName" to "Dollu Kunitha",
                "date" to Timestamp(now / 1000 + 10 * day / 1000, 0),
                "venue" to "Mysore Palace Grounds", "district" to "Mysuru", "imageUrl" to ""),
            mapOf("title" to "Bidriware Exhibition 2026", "artFormId" to "bidriware", "artFormName" to "Bidriware",
                "date" to Timestamp(now / 1000 + 15 * day / 1000, 0),
                "venue" to "Karnataka Chitrakala Parishath, Bengaluru", "district" to "Bengaluru Urban", "imageUrl" to ""),
            mapOf("title" to "Ilkal Saree Handloom Fair", "artFormId" to "ilkal", "artFormName" to "Ilkal Saree",
                "date" to Timestamp(now / 1000 + 20 * day / 1000, 0),
                "venue" to "Bagalkot Cultural Centre", "district" to "Bagalkot", "imageUrl" to ""),
            mapOf("title" to "Kinnala Toy Workshop Camp", "artFormId" to "kinnala", "artFormName" to "Kinnala Toys",
                "date" to Timestamp(now / 1000 + 25 * day / 1000, 0),
                "venue" to "Koppal Community Hall", "district" to "Koppal", "imageUrl" to ""),
            mapOf("title" to "Yakshagana Nightly — Udupi", "artFormId" to "yakshagana", "artFormName" to "Yakshagana",
                "date" to Timestamp(now / 1000 + 30 * day / 1000, 0),
                "venue" to "Sri Krishna Temple Grounds, Udupi", "district" to "Udupi", "imageUrl" to "")
        )
        events.forEachIndexed { i, data ->
            batch.set(db.collection("events").document("event_${String.format("%03d", i + 1)}"), data)
        }

        // ── Single network call for all 18 documents ───────────────────────
        batch.commit().await()
    }
}