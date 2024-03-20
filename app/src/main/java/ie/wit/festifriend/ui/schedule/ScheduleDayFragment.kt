package ie.wit.festifriend.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ie.wit.festifriend.adapters.PerformanceAdapter
import ie.wit.festifriend.databinding.FragmentDayScheduleBinding
import ie.wit.festifriend.models.PerformanceModel

class ScheduleDayFragment : Fragment() {

    private var _binding: FragmentDayScheduleBinding? = null

    private val binding get() = _binding!!
    private lateinit var scheduleViewModel: ScheduleViewModel


    private val adapter: PerformanceAdapter by lazy {
        PerformanceAdapter({ artist ->
            val action = ScheduleFragmentDirections.actionNavigationScheduleToArtistDetailFragment(artist)
            findNavController().navigate(action)
        }, { performance ->
            if (performance.favourite) {
                scheduleViewModel.removeFavourite(performance)
            } else {
                scheduleViewModel.addFavourite(performance)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDayScheduleBinding.inflate(inflater, container, false)
        scheduleViewModel = ViewModelProvider(requireActivity()).get(ScheduleViewModel::class.java)

        setupRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupPerformances()

        scheduleViewModel.refreshFavourites()
    }

    private fun setupRecyclerView() {
        binding.rvPerformances.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ScheduleDayFragment.adapter
        }
    }

    private fun setupPerformances() {
        val day = arguments?.getString("day")
        day?.let {
            scheduleViewModel.filterPerformancesByDay(day)
        }

        scheduleViewModel.performances.observe(viewLifecycleOwner) { allPerformances ->
            val dayPerformances = allPerformances.filter { it.day.equals(day, ignoreCase = true) }
            adapter.setPerformances(dayPerformances)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(day: String): ScheduleDayFragment {
            val fragment = ScheduleDayFragment().apply {
                arguments = Bundle().apply {
                    putString("day", day)
                }
            }
            return fragment
        }
    }
}
