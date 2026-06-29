package com.rook.sport.admin.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    val matches = MutableStateFlow<List<AdminMatch>>(emptyList())
    val channels = MutableStateFlow<List<AdminChannel>>(emptyList())
    val loading = MutableStateFlow(false)
    val message = MutableStateFlow<String?>(null)

    fun loadAll() {
        viewModelScope.launch {
            loading.value = true
            try {
                matches.value = AdminRepository.getMatches()
                channels.value = AdminRepository.getChannels()
            } catch (e: Exception) {
                message.value = "خطأ في الاتصال: ${e.localizedMessage}"
            }
            loading.value = false
        }
    }

    fun saveMatch(m: AdminMatch, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                AdminRepository.saveMatch(m)
                message.value = "✅ تم حفظ المباراة"
                loadAll(); onDone()
            } catch (e: Exception) { message.value = "خطأ: ${e.localizedMessage}" }
        }
    }

    fun deleteMatch(id: String) {
        viewModelScope.launch {
            try { AdminRepository.deleteMatch(id); message.value = "🗑️ تم الحذف"; loadAll() }
            catch (e: Exception) { message.value = "خطأ: ${e.localizedMessage}" }
        }
    }

    fun saveChannel(c: AdminChannel, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                AdminRepository.saveChannel(c)
                message.value = "✅ تم حفظ القناة"
                loadAll(); onDone()
            } catch (e: Exception) { message.value = "خطأ: ${e.localizedMessage}" }
        }
    }

    fun deleteChannel(id: String) {
        viewModelScope.launch {
            try { AdminRepository.deleteChannel(id); message.value = "🗑️ تم الحذف"; loadAll() }
            catch (e: Exception) { message.value = "خطأ: ${e.localizedMessage}" }
        }
    }

    fun clearMessage() { message.value = null }
}
