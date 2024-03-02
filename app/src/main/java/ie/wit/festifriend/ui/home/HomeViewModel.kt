package ie.wit.festifriend.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ie.wit.festifriend.models.ForecastModel
import ie.wit.festifriend.models.WeatherModel
import ie.wit.festifriend.repositories.WeatherRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherResponse = MutableLiveData<WeatherModel>()

    private val _forecastResponse = MutableLiveData<ForecastModel>()
    val forecastResponse: LiveData<ForecastModel>
        get() = _forecastResponse

    private val dingleLat = 52.1409
    private val dingleLong = -10.2640

    init {
        getWeatherData(dingleLat, dingleLong)
        getForecastData(dingleLat, dingleLong)
    }

    private fun getWeatherData(latitude: Double, longitude: Double) = viewModelScope.launch {
        try {
            val response = repository.getWeatherByCoordinates(latitude, longitude)
            if (response.isSuccessful) {
                _weatherResponse.postValue(response.body())
            } else {
                Log.d("HomeViewModel", "getWeatherData Error: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "getWeatherData Exception: ${e.message}")
        }
    }

    private fun getForecastData(latitude: Double, longitude: Double) = viewModelScope.launch {
        try {
            val response = repository.getForecastByCoordinates(latitude, longitude)
            if (response.isSuccessful) {
                _forecastResponse.postValue(response.body())
            } else {
                Log.d("HomeViewModel", "getForecastData Error: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "getForecastData Exception: ${e.message}")
        }
    }
}
