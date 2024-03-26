package ie.wit.festifriend.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ie.wit.festifriend.R
import ie.wit.festifriend.adapters.DayAdapter
import ie.wit.festifriend.databinding.FragmentScheduleBinding

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private lateinit var scheduleViewModel: ScheduleViewModel
    private var currentDay: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_schedule, menu)
                val item = menu.findItem(R.id.toggleFavourites)
                item.setActionView(R.layout.togglebutton_layout)

                val toggle: SwitchCompat? = item.actionView?.findViewById(R.id.toggleButton)
                toggle?.isChecked = scheduleViewModel.showFavourites.value ?: false

                toggle?.setOnCheckedChangeListener { _, isChecked ->
                    scheduleViewModel.setShowFavourites(isChecked)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.toggleFavourites -> true
                    else -> false
                }
            }

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)

        scheduleViewModel = ViewModelProvider(requireActivity())[ScheduleViewModel::class.java]
        setupTabs()
        setupMenu()

        scheduleViewModel.showFavourites.observe(viewLifecycleOwner) { showFavourites ->
            // Re-apply the filter whenever the user toggles the favourite performances view.
            currentDay?.let {
                scheduleViewModel.filterPerformancesByDay(it)
            }
        }

        return binding.root
    }

    private fun setupTabs() {
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = DayAdapter(this)

        val tabs: TabLayout = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Friday"
                1 -> "Saturday"
                2 -> "Sunday"
                else -> null
            }
        }.attach()

        setupTabListener(tabs)
    }

    private fun setupTabListener(tabs: TabLayout) {
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentDay = tab.text.toString()
                scheduleViewModel.filterPerformancesByDay(currentDay!!)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Optional: Implement if needed.
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Optional: Implement if needed.
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
