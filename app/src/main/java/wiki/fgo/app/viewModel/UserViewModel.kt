package wiki.fgo.app.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    private val userName = MutableLiveData("岸波白野")
    private val userId = MutableLiveData("")
    fun getUserName(): LiveData<String> {
        return userName
    }

    fun getUserId(): LiveData<String> {
        return userId
    }

    fun userName(name: String) {
        userName.value = name
    }

    fun userId(id: String) {
        userId.value = id
    }
}

