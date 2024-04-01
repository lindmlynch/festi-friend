package ie.wit.festifriend.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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
    private val onDelete: (PostModel) -> Unit,
    private val onLike: (String) -> Unit,

) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view, onEdit, onDelete, onLike, currentUserId)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount() = posts.size

    class PostViewHolder(
        itemView: View,
        private val onEdit: (PostModel) -> Unit,
        private val onDelete: (PostModel) -> Unit,
        private val onLike: (String) -> Unit,
        private val currentUserId: String
    ) : RecyclerView.ViewHolder(itemView) {
        private val postImageView: ImageView = itemView.findViewById(R.id.postImageView)
        private val postTextView: TextView = itemView.findViewById(R.id.postTextView)
        private val postUserName: TextView = itemView.findViewById(R.id.postUserName)
        private val postTimestamp: TextView = itemView.findViewById(R.id.postTimestamp)
        private val editPostButton: Button = itemView.findViewById(R.id.editPostButton)
        private val deletePostButton: Button = itemView.findViewById(R.id.deletePostButton)
        private val likePostButton: ImageButton = itemView.findViewById(R.id.likePostButton)
        private val likeCountTextView: TextView = itemView.findViewById(R.id.likeCountTextView)

        fun bind(post: PostModel) {
            Picasso.get().load(post.imageUrl).into(postImageView)
            postTextView.text = post.text
            postUserName.text = post.userName ?: "Anonymous"
            postTimestamp.text = DateFormat.getDateTimeInstance().format(Date(post.timestamp))

            editPostButton.visibility = if (post.userId == currentUserId) View.VISIBLE else View.GONE
            deletePostButton.visibility = if (post.userId == currentUserId) View.VISIBLE else View.GONE

            editPostButton.setOnClickListener { onEdit(post) }
            deletePostButton.setOnClickListener { onDelete(post) }
            likePostButton.setOnClickListener {
                post.id?.let(onLike)
            }

            val userLiked = post.userActions[currentUserId] ?: false
            if (userLiked) {
                likePostButton.setImageResource(R.drawable.ic_liked)
            } else {
                likePostButton.setImageResource(R.drawable.ic_unliked)
            }

            likeCountTextView.text = "${post.likes} Likes"

        }
    }

    fun updatePosts(newPosts: List<PostModel>) {
        posts = newPosts
        notifyDataSetChanged()
    }
}
