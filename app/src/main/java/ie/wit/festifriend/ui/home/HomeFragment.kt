package ie.wit.festifriend.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import ie.wit.festifriend.databinding.FragmentHomeBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.forecastResponse.observe(viewLifecycleOwner, Observer { forecast ->
            val dailyForecast = forecast.list
                .asSequence()
                .map { forecastDetail ->
                    val date = Date(forecastDetail.dt * 1000)
                    val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                    dateFormat.timeZone = TimeZone.getTimeZone("Europe/Dublin")
                    val dayOfWeek = dateFormat.format(date)
                    dayOfWeek to forecastDetail
                }
                .distinctBy { it.first }
                .take(3)
                .toList()

            dailyForecast.forEachIndexed { index, (dayOfWeek, forecastDetail) ->
                val iconCode = forecastDetail.weather.firstOrNull()?.icon ?: "01d"
                val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
                val temp = String.format("%.0f°C", forecastDetail.main.temp)
                val description = forecastDetail.weather.firstOrNull()?.description ?: "No description"
                val forecastText = "$dayOfWeek: $temp, $description"

                when (index) {
                    0 -> {
                        binding.tvForecast1.text = forecastText
                        Picasso.get().load(iconUrl).into(binding.ivForecastIcon1)
                    }
                    1 -> {
                        binding.tvForecast2.text = forecastText
                        Picasso.get().load(iconUrl).into(binding.ivForecastIcon2)
                    }
                    2 -> {
                        binding.tvForecast3.text = forecastText
                        Picasso.get().load(iconUrl).into(binding.ivForecastIcon3)
                    }
                }
            }
        })
        viewModel.festivalUpdates.observe(viewLifecycleOwner, Observer { updates ->
            Timber.i("Updates observed: $updates")
            if (updates.isNotEmpty()) {
                val updatesText = updates.joinToString(separator = "\n\n")
                binding.festivalUpdates.text = updatesText
            } else {
                binding.festivalUpdates.text = "No updates available at this time."
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

