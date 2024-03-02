package ie.wit.festifriend.repositories

import ie.wit.festifriend.api.ApiService
import ie.wit.festifriend.utils.Constants
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val apiService: ApiService) {

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