package ie.wit.festifriend.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ie.wit.festifriend.ui.schedule.ScheduleDayFragment

class DayAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val days = arrayOf("Friday", "Saturday", "Sunday")

    override fun getItemCount(): Int = days.size

    override fun createFragment(position: Int): Fragment {

        val day = days[position]
        val fragment = ScheduleDayFragment()
        fragment.arguments = Bundle().apply {
            putString("day", day)
        }
        return fragment
    }
}