package ie.wit.festifriend.repositories

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import ie.wit.festifriend.api.ApiService
import ie.wit.festifriend.utils.Constants
import javax.inject.Inject
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import timber.log.Timber

class WeatherRepository @Inject constructor(private val apiService: ApiService) {

    private val dbReference = FirebaseDatabase.getInstance().getReference("updates")
    fun getFestivalUpdates(callback: (List<String>) -> Unit) {
        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updates = snapshot.children.mapNotNull { it.getValue<String>() }
                callback(updates)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.w(error.toException(), "Failed to read updates.")
            }
        })
    }

    suspend fun getWeatherByCoordinates(latitude: Double, longitude: Double) = apiService.getWeather(
        latitude = latitude,
        longitude = longitude,
        apiKey = Constants.API_KEY
    )

    suspend fun getForecastByCoordinates(latitude: Double, longitude: Double) = apiService.getForecast(
        latitude = latitude,
        longitude = longitude,
        apiKey = Constants.API_KEY
    )
}