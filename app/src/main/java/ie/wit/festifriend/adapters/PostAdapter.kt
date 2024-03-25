package ie.wit.festifriend.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.wit.festifriend.R
import ie.wit.festifriend.models.PostModel
import java.text.DateFormat
import java.util.Date

class PostAdapter(private var posts: List<PostModel>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount() = posts.size

    fun updatePosts(newPosts: List<PostModel>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val postImageView: ImageView = itemView.findViewById(R.id.postImageView)
        private val postTextView: TextView = itemView.findViewById(R.id.postTextView)
        private val postUserName: TextView = itemView.findViewById(R.id.postUserName)
        private val postTimestamp: TextView = itemView.findViewById(R.id.postTimestamp)

        fun bind(post: PostModel) {
            post.imageUrl?.let {
                Picasso.get().load(it).into(postImageView)
            }
            postTextView.text = post.text
            postUserName.text = post.userName ?: "Anonymous"
            postTimestamp.text = DateFormat.getDateTimeInstance().format(Date(post.timestamp))
        }
    }

}