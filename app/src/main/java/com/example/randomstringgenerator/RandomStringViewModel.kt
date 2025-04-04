package com.example.randomstringgenerator

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.randomstringgenerator.model.RandomStringData
import com.example.randomstringgenerator.repository.RandomStringRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RandomStringViewModel(application: Application) :
    AndroidViewModel(application = application) {
    private val repository = RandomStringRepository(application.contentResolver)

    private val _randomStringList = MutableStateFlow<List<RandomStringData>>(emptyList())
    val randomStringList = _randomStringList.asStateFlow()

    private val _inputValue = MutableStateFlow<String>("")
    val inputValue = _inputValue.asStateFlow()

    private val _screenLoader = MutableStateFlow<Boolean>(false)
    val screenLoader = _screenLoader.asStateFlow()

    private val _errorMessage = MutableStateFlow<String>("")
    val errorMessage = _errorMessage.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchRandomString(noOfCharacters: Int, displayToast: (Int) -> Unit) =
        viewModelScope.launch {
            _screenLoader.value = true
            withContext(Dispatchers.IO) {
                val result = repository.getRandomString(noOfCharacters, displayToast)
                result?.let {
                    _randomStringList.value = listOf(it) + _randomStringList.value
                }
            }
            _inputValue.value = ""
            _screenLoader.value = false
        }

    fun clearAll() {
        _randomStringList.value = emptyList()

    }

    fun deleteString(item: RandomStringData) {
        _randomStringList.value = _randomStringList.value.filterNot { it == item }
    }

    fun updateInputField(value: String, updateError: (Int) -> Unit) {
        try{
            if(value == ""){
                _inputValue.value = value
                return
            }
            if(value.isDigitsOnly()){
                _inputValue.value = value.trim()
                _errorMessage.value = ""
            }else{
                updateError(R.string.invalid_input)
            }

        }catch (error: NumberFormatException){
            _inputValue.value = ""
            updateError(R.string.out_of_bounds)

        }catch (error: Exception){
            _inputValue.value = ""
            updateError(R.string.something_went_wrong)
        }
    }

    fun updateErrorMessage(message: String){
        _errorMessage.value = message
    }
}