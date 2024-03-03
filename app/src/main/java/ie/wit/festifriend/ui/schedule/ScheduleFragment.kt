package ie.wit.festifriend.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ie.wit.festifriend.adapters.DayAdapter
import ie.wit.festifriend.databinding.FragmentScheduleBinding

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private lateinit var scheduleViewModel: ScheduleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)

        scheduleViewModel = ViewModelProvider(requireActivity()).get(ScheduleViewModel::class.java)

        setupTabs()

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
                val day = tab.text.toString()
                scheduleViewModel.filterPerformancesByDay(day)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
