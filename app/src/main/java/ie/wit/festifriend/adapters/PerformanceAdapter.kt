package ie.wit.festifriend.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.festifriend.R
import ie.wit.festifriend.databinding.ItemPerformanceBinding
import ie.wit.festifriend.models.ArtistModel
import ie.wit.festifriend.models.PerformanceModel

class PerformanceAdapter(
    private val onClick: (ArtistModel) -> Unit,
    private val onFavoriteClicked: (PerformanceModel) -> Unit
) : RecyclerView.Adapter<PerformanceAdapter.PerformanceViewHolder>() {

    private var performances: List<PerformanceModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerformanceViewHolder {
        val binding = ItemPerformanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PerformanceViewHolder(binding, onClick, onFavoriteClicked, this)
    }

    override fun onBindViewHolder(holder: PerformanceViewHolder, position: Int) {
        holder.bind(performances[position])
    }

    override fun getItemCount() = performances.size

    fun setPerformances(performances: List<PerformanceModel>) {
        this.performances = performances
        notifyDataSetChanged()
    }

    class PerformanceViewHolder(
        private val binding: ItemPerformanceBinding,
        private val onClick: (ArtistModel) -> Unit,
        private val onFavoriteClicked: (PerformanceModel) -> Unit,
        private val adapter: PerformanceAdapter
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(performance: PerformanceModel) {
            binding.apply {
                performanceName.text = performance.name
                performanceDescription.text = performance.description
                performanceTime.text = "${performance.startTime} - ${performance.endTime}"
                performanceLocation.text = performance.location
                favouriteIcon.setImageResource(
                    if (performance.favourite) R.drawable.ic_favourite_filled
                    else R.drawable.ic_favourite_border
                )
            }

            binding.favouriteIcon.setOnClickListener {
                onFavoriteClicked(performance)
                performance.favourite = !performance.favourite
                adapter.notifyItemChanged(adapterPosition)
            }
            itemView.setOnClickListener { onClick(performance.artist) }
        }
    }
}