package ie.wit.festifriend.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.wit.festifriend.R
import ie.wit.festifriend.models.PostModel
import java.text.DateFormat
import java.util.Date

class PostAdapter(
    private var posts: List<PostModel>,
    private val currentUserId: String,
    private val onEdit: (PostModel) -> Unit,
    private val onDelete: (PostModel) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view, onEdit, onDelete, currentUserId)
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

    class PostViewHolder(
        itemView: View,
        private val onEdit: (PostModel) -> Unit,
        private val onDelete: (PostModel) -> Unit,
        private val currentUserId: String
    ) : RecyclerView.ViewHolder(itemView) {
        private val postImageView: ImageView = itemView.findViewById(R.id.postImageView)
        private val postTextView: TextView = itemView.findViewById(R.id.postTextView)
        private val postUserName: TextView = itemView.findViewById(R.id.postUserName)
        private val postTimestamp: TextView = itemView.findViewById(R.id.postTimestamp)
        private val editPostButton: Button = itemView.findViewById(R.id.editPostButton)
        private val deletePostButton: Button = itemView.findViewById(R.id.deletePostButton)

        fun bind(post: PostModel) {
            post.imageUrl?.let {
                Picasso.get().load(it).into(postImageView)
            }
            postTextView.text = post.text
            postUserName.text = post.userName ?: "Anonymous"
            postTimestamp.text = DateFormat.getDateTimeInstance().format(Date(post.timestamp))

            editPostButton.visibility = if (post.userId == currentUserId) View.VISIBLE else View.GONE
            deletePostButton.visibility = if (post.userId == currentUserId) View.VISIBLE else View.GONE

            editPostButton.setOnClickListener { onEdit(post) }
            deletePostButton.setOnClickListener { onDelete(post) }
        }
    }
}
