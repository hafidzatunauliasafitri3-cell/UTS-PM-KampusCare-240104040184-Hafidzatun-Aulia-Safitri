package id.antasari.kampuscare.ui.beranda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.kampuscare.data.repository.CatatanRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class BerandaViewModel(repository: CatatanRepository) : ViewModel() {
    val totalCatatan: StateFlow<Int> = repository.jumlahCatatan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}