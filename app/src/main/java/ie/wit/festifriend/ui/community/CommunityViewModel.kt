package ie.wit.festifriend.ui.community

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ie.wit.festifriend.models.PostModel
import ie.wit.festifriend.repositories.CommunityRepository
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(private val repository: CommunityRepository) : ViewModel() {

    private val _posts = MutableLiveData<List<PostModel>>()
    val posts: LiveData<List<PostModel>> = _posts

    private val _uploadSuccess = MutableLiveData<Boolean>()
    val uploadSuccess: LiveData<Boolean> = _uploadSuccess

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> = _deleteSuccess


    private val _imageUploadUrl = MutableLiveData<String?>()
    val imageUploadUrl: LiveData<String?> = _imageUploadUrl

    fun fetchPosts() {
        repository.fetchPosts { postsList ->
            _posts.postValue(postsList)
        }
    }

    fun postReview(postModel: PostModel) {
        viewModelScope.launch {
            repository.postReview(postModel) { success ->
                _uploadSuccess.postValue(success)
            }
        }
    }

    fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            repository.uploadImage(uri) { imageUrl ->
                _imageUploadUrl.postValue(imageUrl)
            }
        }
    }

    fun updatePost(postId: String?, updatedPost: PostModel) {
        postId?.takeIf { it.isNotBlank() }?.let {
            viewModelScope.launch {
                repository.updatePost(it, updatedPost) { success ->
                    _updateSuccess.postValue(success)
                    if (success) {
                        fetchPosts()
                    }
                }
            }
        } ?: _updateSuccess.postValue(false)
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            repository.deletePost(postId) { success ->
                _deleteSuccess.postValue(success)
                if (success) {
                    fetchPosts()
                }
            }
        }
    }

    fun likePost(postId: String, userId: String) {
        viewModelScope.launch {
            repository.likePost(postId, userId) { success ->
                if (success) {
                    fetchPosts()
                }
            }
        }
    }
}
