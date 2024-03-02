package ie.wit.festifriend.models

data class ForecastModel(
    val list: List<ForecastDetail>
)

data class ForecastDetail(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>
)