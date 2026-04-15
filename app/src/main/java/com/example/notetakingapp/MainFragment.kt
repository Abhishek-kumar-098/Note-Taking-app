package com.example.notetakingapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.room.InvalidationTracker
import com.example.notetakingapp.api.NotesAPI
import com.example.notetakingapp.databinding.FragmentMainBinding
import com.example.notetakingapp.models.NoteResponse
import com.example.notetakingapp.utils.Constants.TAG
import com.example.notetakingapp.utils.NetworkResult
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val noteViewModel by viewModels<NoteViewModel>()

    private lateinit var adapter: NoteAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentMainBinding.inflate(inflater, container, false)
        adapter = NoteAdapter { onNoteCkicked(it) }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
        noteViewModel.getNotes()
        binding.noteList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.noteList.adapter = adapter
        binding.addNote.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_noteFragment)
        }

    }

    private fun bindObservers() {
        noteViewModel.notesLiveData.observe(viewLifecycleOwner,{
            binding.progressBar.isVisible= false
           when (it){

               is NetworkResult.Success<*> -> {
                   adapter.submitList(it.data)
               }
               is NetworkResult.Error<*> -> {
                   Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

               }
               is NetworkResult.Loading<*> -> {
                   binding.progressBar.isVisible = true
               }
           }
        })
        }

    private fun onNoteCkicked(noteResponse: NoteResponse) {
        val bundle = Bundle()
        bundle.putString("note", Gson().toJson(noteResponse))
        findNavController().navigate(R.id.action_mainFragment_to_noteFragment, bundle)

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
