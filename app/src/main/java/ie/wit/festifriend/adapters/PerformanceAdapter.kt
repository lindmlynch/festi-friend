package ie.wit.festifriend.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.festifriend.databinding.ItemPerformanceBinding
import ie.wit.festifriend.models.PerformanceModel

class PerformanceAdapter : RecyclerView.Adapter<PerformanceAdapter.PerformanceViewHolder>() {

    private var performances: List<PerformanceModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerformanceViewHolder {
        val binding = ItemPerformanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PerformanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PerformanceViewHolder, position: Int) {
        holder.bind(performances[position])
    }

    override fun getItemCount() = performances.size

    fun setPerformances(performances: List<PerformanceModel>) {
        this.performances = performances
        notifyDataSetChanged()
    }

    class PerformanceViewHolder(private val binding: ItemPerformanceBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(performance: PerformanceModel) {
            binding.apply {
                performanceName.text = performance.name
                performanceDescription.text = performance.description
                performanceTime.text = "${performance.startTime} - ${performance.endTime}"
                performanceLocation.text = performance.location
            }
        }
    }
}