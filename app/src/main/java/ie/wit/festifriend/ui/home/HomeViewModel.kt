package ie.wit.festifriend.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ie.wit.festifriend.models.ForecastModel
import ie.wit.festifriend.models.WeatherModel
import ie.wit.festifriend.repositories.WeatherRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherResponse = MutableLiveData<WeatherModel>()

    private val _forecastResponse = MutableLiveData<ForecastModel>()
    val forecastResponse: LiveData<ForecastModel>
        get() = _forecastResponse

    private val dingleLat = 52.1409
    private val dingleLong = -10.2640

    private val _festivalUpdates = MutableLiveData<List<String>>()
    val festivalUpdates: LiveData<List<String>>
        get() = _festivalUpdates


    init {
        getWeatherData(dingleLat, dingleLong)
        getForecastData(dingleLat, dingleLong)
        fetchFestivalUpdates()
    }

    private fun getWeatherData(latitude: Double, longitude: Double) = viewModelScope.launch {
        try {
            val response = repository.getWeatherByCoordinates(latitude, longitude)
            if (response.isSuccessful) {
                _weatherResponse.postValue(response.body())
            } else {
                Timber.i("HomeViewModel", "getWeatherData Error: ${response.code()}")
            }
        } catch (e: Exception) {
            Timber.i("HomeViewModel", "getWeatherData Exception: ${e.message}")
        }
    }

    private fun getForecastData(latitude: Double, longitude: Double) = viewModelScope.launch {
        try {
            val response = repository.getForecastByCoordinates(latitude, longitude)
            if (response.isSuccessful) {
                _forecastResponse.postValue(response.body())
            } else {
                Timber.i("HomeViewModel", "getForecastData Error: ${response.code()}")
            }
        } catch (e: Exception) {
            Timber.i("HomeViewModel", "getForecastData Exception: ${e.message}")
        }
    }

    private fun fetchFestivalUpdates() {
        repository.getFestivalUpdates { updates ->
            _festivalUpdates.postValue(updates)
        }
    }
}
