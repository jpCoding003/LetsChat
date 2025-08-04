package com.tops.letschat


import com.tops.letschat.Constants.Companion.CONTENT_TYPE
import com.tops.letschat.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Data class for the notification payload
data class NotificationData(val title: String, val body: String)

// Data class for the entire push notification request
data class PushNotification(
    val data: NotificationData,
    val to: String // The recipient's FCM token
)

// Retrofit interface for the FCM API
interface NotificationAPI {
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}

// Constants for the API call
class Constants {
    companion object {
        const val BASE_URL = "https://fcm.googleapis.com"
        // Get your server key from Firebase Project Settings > Cloud Messaging
        const val SERVER_KEY = "YOUR_FCM_SERVER_KEY"
        const val CONTENT_TYPE = "application/json"
    }
}
