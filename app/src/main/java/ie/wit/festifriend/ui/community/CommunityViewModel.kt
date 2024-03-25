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
}