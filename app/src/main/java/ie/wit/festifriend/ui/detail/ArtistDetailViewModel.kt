package ie.wit.festifriend.ui.detail



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.festifriend.models.ArtistModel

class ArtistDetailViewModel : ViewModel() {

    private val _artist = MutableLiveData<ArtistModel>()
    val artist: LiveData<ArtistModel> = _artist

    fun setArtist(artist: ArtistModel) {
        _artist.value = artist
    }
}
