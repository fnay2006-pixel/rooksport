package com.rook.sport.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rook.sport.data.model.StreamServer
import com.rook.sport.data.firebase.FirebaseRepository
import com.rook.sport.data.repo.ContentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContentViewModel : ViewModel() {

    private val _state = MutableStateFlow(ContentRepository.Content(emptyList(), emptyList(), emptyMap()))
    val state: StateFlow<ContentRepository.Content> = _state.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _loading.value = true
            // يقرأ من Firebase (التي يعدّلها الأدمن)، ومع fallback تلقائي
            _state.value = FirebaseRepository.load()
            _loading.value = false
        }
    }

    fun serversFor(ownerId: String): List<StreamServer> =
        _state.value.serversByOwner[ownerId] ?: emptyList()

    fun matchById(id: String) = _state.value.matches.firstOrNull { it.id == id }
    fun channelById(id: String) = _state.value.channels.firstOrNull { it.id == id }
}
