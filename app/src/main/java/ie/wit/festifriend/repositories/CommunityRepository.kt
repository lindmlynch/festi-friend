package ie.wit.festifriend.repositories

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.database.*
import ie.wit.festifriend.models.PostModel
import java.util.*

class CommunityRepository {

    private val dbPosts = FirebaseDatabase.getInstance().getReference("posts")
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("postImages")

    fun fetchPosts(onResult: (List<PostModel>) -> Unit) {
        dbPosts.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val posts = mutableListOf<PostModel>()
                for (postSnapshot in dataSnapshot.children) {
                    val post = postSnapshot.getValue(PostModel::class.java)
                    post?.id = postSnapshot.key
                    post?.let { posts.add(it) }
                }
                onResult(posts)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun postReview(postModel: PostModel, onComplete: (Boolean) -> Unit) {
        val key = dbPosts.push().key
        key?.let {
            dbPosts.child(it).setValue(postModel).addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
        }
    }

    fun uploadImage(uri: Uri, onComplete: (String?) -> Unit) {
        val filename = UUID.randomUUID().toString()
        val ref = storageReference.child("images/$filename")
        ref.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                    onComplete(downloadUri.toString())
                }?.addOnFailureListener {
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    fun updatePost(postId: String, updatedPost: PostModel, onComplete: (Boolean) -> Unit) {
        dbPosts.child(postId).setValue(updatedPost).addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }

    fun deletePost(postId: String, onComplete: (Boolean) -> Unit) {
        dbPosts.child(postId).removeValue().addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }
}