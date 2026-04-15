package com.example.notetakingapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.notetakingapp.api.NotesAPI
import com.example.notetakingapp.models.NoteRequest
import com.example.notetakingapp.models.NoteResponse
import com.example.notetakingapp.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class NoteRepository @Inject constructor(private val notesAPI: NotesAPI) {

    private val _notesLiveData = MutableLiveData<NetworkResult<List<NoteResponse>>>()
    val notesLiveData: LiveData<NetworkResult<List<NoteResponse>>>
        get() = _notesLiveData

    private val _statusLiveData = MutableLiveData<NetworkResult<String>>()
    val statusLiveData: LiveData<NetworkResult<String>>
        get() = _statusLiveData

    suspend fun getNotes() {
        _notesLiveData.postValue(NetworkResult.Loading())
        try {
            val response = notesAPI.getNotes()
            if (response.isSuccessful && response.body() != null) {
                _notesLiveData.postValue(NetworkResult.Success(response.body()!!))
            } else {
                handleError(_notesLiveData, response)
            }
        } catch (e: Exception) {
            _notesLiveData.postValue(NetworkResult.Error(e.message ?: "Something went wrong"))
        }
    }

    suspend fun createNote(noteRequest: NoteRequest) {
        _statusLiveData.postValue(NetworkResult.Loading())
        try {
            val response = notesAPI.createNote(noteRequest)
            handleResponse(response, "Note Created")
        } catch (e: Exception) {
            _statusLiveData.postValue(NetworkResult.Error(e.message ?: "Something went wrong"))
        }
    }

    suspend fun deleteNote(noteId: String) {
        _statusLiveData.postValue(NetworkResult.Loading())
        try {
            val response = notesAPI.deleteNote(noteId)
            handleResponse(response, "Note Deleted")
        } catch (e: Exception) {
            _statusLiveData.postValue(NetworkResult.Error(e.message ?: "Something went wrong"))
        }
    }

    suspend fun updateNote(noteId: String, noteRequest: NoteRequest) {
        _statusLiveData.postValue(NetworkResult.Loading())
        try {
            val response = notesAPI.updateNote(noteId, noteRequest)
            handleResponse(response, "Note Updated")
        } catch (e: Exception) {
            _statusLiveData.postValue(NetworkResult.Error(e.message ?: "Something went wrong"))
        }
    }

    private fun handleResponse(response: Response<NoteResponse>, message: String) {
        if (response.isSuccessful && response.body() != null) {
            _statusLiveData.postValue(NetworkResult.Success(message))
        } else {
            val errorMsg = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorMsg ?: "").getString("message")
            } catch (e: Exception) {
                "Something went wrong"
            }
            _statusLiveData.postValue(NetworkResult.Error(errorMessage))
        }
    }

    private fun <T> handleError(mutableLiveData: MutableLiveData<NetworkResult<T>>, response: Response<*>) {
        val errorMsg = response.errorBody()?.string()
        val message = try {
            JSONObject(errorMsg ?: "").getString("message")
        } catch (e: Exception) {
            "Something went wrong"
        }
        mutableLiveData.postValue(NetworkResult.Error(message))
    }
}
