package com.tifd.projectcomposed053.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tifd.projectcomposed053.data.model.local.Tugas
import com.tifd.projectcomposed053.data.model.local.TugasRepository
import kotlinx.coroutines.launch

class MainViewModel(private val tugasRepository: TugasRepository) : ViewModel() {

    // LiveData for the list of tasks
    val tugasList: LiveData<List<Tugas>> = tugasRepository.getAllTugas()

    // Loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Error message state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Function to add a new task
    fun addTugas(tugas: Tugas) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                tugasRepository.insert(tugas)
            } catch (e: Exception) {
                _error.postValue("Failed to add task: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}