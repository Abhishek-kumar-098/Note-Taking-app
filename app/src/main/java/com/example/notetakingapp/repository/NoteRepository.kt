package com.example.notetakingapp.repository

import android.util.Log
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
    val notesLiveData : LiveData<NetworkResult<List<NoteResponse>>>
        get() = _notesLiveData

    private val _statusLiveData = MutableLiveData<NetworkResult<Pair<Boolean, String>>>()
    val statusLiveData : LiveData<NetworkResult<Pair<Boolean, String>>>
        get() = _statusLiveData


    suspend fun getNotes(){
        _notesLiveData.postValue(NetworkResult.Loading())
        val response = notesAPI.getNotes()
        if(response.isSuccessful && response.body() != null){
            _notesLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
//        else if (response.errorBody() != null){
//            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
//            _notesLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
//        }
        else if (response.errorBody() != null){
            val errorText = response.errorBody()!!.string()

            try {
                val errorObj = JSONObject(errorText)
                _notesLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
            } catch (e: Exception) {
                _notesLiveData.postValue(NetworkResult.Error("Server error: $errorText"))
            }
        }
        else{
            _notesLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }
    suspend fun createNote(noteRequest: NoteRequest){
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = notesAPI.createNote(noteRequest)
        handleResponse(response, "Note Created")

    }


//    suspend fun createNote(noteRequest: NoteRequest){
//        _statusLiveData.postValue(NetworkResult.Loading())
//
//        val response = notesAPI.createNote(noteRequest)
//
//        Log.d("API_DEBUG", "STATUS CODE: ${response.code()}")
//        Log.d("API_DEBUG", "IS SUCCESSFUL: ${response.isSuccessful}")
//
//        response.body()?.let {
//            Log.d("API_DEBUG", "SUCCESS BODY: $it")
//        }
//
//        response.errorBody()?.let {
//            val errorText = it.string()
//            Log.d("API_DEBUG", "ERROR BODY: $errorText")
//        }
//
//        handleResponse(response, "Note Created")
//    }

//    suspend fun deleteNote(noteId: String){
//        _statusLiveData.postValue(NetworkResult.Loading())
//        val response = notesAPI.deleteNote(noteId)
//        handleResponse(response, "Note Deleted")
//    }

    suspend fun deleteNote(noteId: String){
        _statusLiveData.postValue(NetworkResult.Loading())

        Log.d("API_DEBUG", "DELETE REQUEST ID: $noteId")

        val response = notesAPI.deleteNote(noteId)

        Log.d("API_DEBUG", "STATUS CODE: ${response.code()}")
        Log.d("API_DEBUG", "IS SUCCESSFUL: ${response.isSuccessful}")

        response.body()?.let {
            Log.d("API_DEBUG", "SUCCESS BODY: $it")
        }

        response.errorBody()?.let {
            val errorText = it.string()
            Log.d("API_DEBUG", "ERROR BODY: $errorText")
        }

        handleResponse(response, "Note Deleted")
    }


    suspend fun updateNote(noteId: String, noteRequest: NoteRequest){
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = notesAPI.updateNote(noteId, noteRequest)
        handleResponse(response, "Note Updated")
    }
    private fun handleResponse(response: Response<NoteResponse>, message: String) {
        if (response.isSuccessful && response.body() != null) {
            _statusLiveData.postValue(NetworkResult.Success(Pair(true, message)))
        } else {
            _statusLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }


}
