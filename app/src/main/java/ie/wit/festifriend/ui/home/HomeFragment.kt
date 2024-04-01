package ie.wit.festifriend.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import dagger.hilt.android.AndroidEntryPoint
import ie.wit.festifriend.R
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
        setupMenu()
        return binding.root
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_home, menu)
                val profileMenuItem = menu.findItem(R.id.profileImageView)
                profileMenuItem.setActionView(R.layout.image_layout)

                val profileImageView: ImageView? = profileMenuItem.actionView as? ImageView

                val account = GoogleSignIn.getLastSignedInAccount(requireContext())
                val photoUrl = account?.photoUrl

                if (photoUrl != null) {
                    Picasso.get()
                        .load(photoUrl)
                        .resize(80, 80)
                        .transform(customTransformation())
                        .centerCrop()
                        .into(profileImageView)
                } else {
                    profileImageView?.setImageResource(R.drawable.image_placeholder_ic)
                }
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    private fun customTransformation(): Transformation =
        RoundedTransformationBuilder()
            .cornerRadiusDp(40F)
            .oval(true)
            .build()


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
                val forecastText = "$dayOfWeek: $temp"

                when (index) {
                    0 -> {
                    binding.tvForecastDayTemp1.text = forecastText
                    binding.tvForecastDesc1.text = description
                    Picasso.get().load(iconUrl).into(binding.ivForecastIcon1)
                }
                    1 -> {
                        binding.tvForecastDayTemp2.text = forecastText
                        binding.tvForecastDesc2.text = description
                        Picasso.get().load(iconUrl).into(binding.ivForecastIcon2)
                    }
                    2 -> {
                        binding.tvForecastDayTemp3.text = forecastText
                        binding.tvForecastDesc3.text = description
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

