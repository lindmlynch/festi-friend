package ie.wit.festifriend.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ie.wit.festifriend.adapters.PerformanceAdapter
import ie.wit.festifriend.databinding.FragmentDayScheduleBinding

class ScheduleDayFragment : Fragment() {

    private var _binding: FragmentDayScheduleBinding? = null

    private val binding get() = _binding!!
    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var adapter: PerformanceAdapter

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
        setupPerformances()
    }

    private fun setupRecyclerView() {
        adapter = PerformanceAdapter()
        binding.rvPerformances.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ScheduleDayFragment.adapter
        }
    }

    private fun setupPerformances() {

        arguments?.getString("day")?.let { day ->
            scheduleViewModel.filterPerformancesByDay(day)
        }

        scheduleViewModel.performances.observe(viewLifecycleOwner) { performances ->
            adapter.setPerformances(performances)
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

