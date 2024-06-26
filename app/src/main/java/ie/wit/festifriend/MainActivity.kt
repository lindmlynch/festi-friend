package ie.wit.festifriend

import android.annotation.SuppressLint
import android.location.Location
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ie.wit.festifriend.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.Observer
import ie.wit.festifriend.ui.auth.LoggedInViewModel
import ie.wit.festifriend.ui.auth.Login
import ie.wit.festifriend.ui.map.MapsViewModel
import ie.wit.festifriend.utils.checkLocationPermissions
import ie.wit.festifriend.utils.isPermissionGranted
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var loggedInViewModel : LoggedInViewModel
    private val mapsViewModel : MapsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_schedule, R.id.navigation_community, R.id.mapsFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if(checkLocationPermissions(this)) {
            mapsViewModel.updateCurrentLocation()
        }
    }

    public override fun onStart() {
        super.onStart()
        loggedInViewModel = ViewModelProvider(this).get(LoggedInViewModel::class.java)

        loggedInViewModel.loggedOut.observe(this, Observer { loggedout ->
            if (loggedout) {
                startActivity(Intent(this, Login::class.java))
            }
        })

    }

    override fun onSupportNavigateUp(): Boolean {

        return findNavController(R.id.nav_host_fragment_activity_main).navigateUp()
                || super.onSupportNavigateUp()
    }

    fun signOut(item: MenuItem) {
        loggedInViewModel.logOut()
        val intent = Intent(this, Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isPermissionGranted(requestCode, grantResults))
            mapsViewModel.updateCurrentLocation()
        else {

            mapsViewModel.currentLocation.value = Location("Default").apply {
                latitude = 52.1409
                longitude = -10.3671
            }
        }
        Timber.i("LOC : %s", mapsViewModel.currentLocation.value)
    }
}