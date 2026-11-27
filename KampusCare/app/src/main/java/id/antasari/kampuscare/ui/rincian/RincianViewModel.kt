package id.antasari.kampuscare.ui.rincian

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.kampuscare.data.local.CatatanEntity
import id.antasari.kampuscare.data.repository.CatatanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RincianUiState {
    data object Memuat : RincianUiState()
    data class Ditemukan(val catatan: CatatanEntity) : RincianUiState()
    data object TidakDitemukan : RincianUiState()
    data object BerhasilDihapus : RincianUiState()
}

class RincianViewModel(private val repository: CatatanRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<RincianUiState>(RincianUiState.Memuat)
    val uiState: StateFlow<RincianUiState> = _uiState.asStateFlow()

    private var sudahMulaiObservasi = false

    fun muatCatatan(id: Long) {
        if (id <= 0L) {
            _uiState.value = RincianUiState.TidakDitemukan
            return
        }
        if (sudahMulaiObservasi) return
        sudahMulaiObservasi = true

        viewModelScope.launch {
            repository.observeCatatanById(id).collect { data ->
                if (_uiState.value is RincianUiState.BerhasilDihapus) return@collect
                _uiState.value = if (data != null) {
                    RincianUiState.Ditemukan(data)
                } else {
                    RincianUiState.TidakDitemukan
                }
            }
        }
    }

    fun hapusCatatan(catatan: CatatanEntity) {
        viewModelScope.launch {
            repository.hapusCatatan(catatan)
                .onSuccess { _uiState.value = RincianUiState.BerhasilDihapus }
        }
    }
}