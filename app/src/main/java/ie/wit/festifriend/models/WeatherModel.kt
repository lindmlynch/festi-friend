package ie.wit.festifriend.models

data class WeatherModel(
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)

data class Main(
    val temp: Double
)

data class Weather(
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double
)
