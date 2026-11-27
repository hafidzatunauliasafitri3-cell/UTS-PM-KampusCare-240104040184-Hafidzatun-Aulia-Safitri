package id.antasari.kampuscare.ui.daftar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.kampuscare.data.local.CatatanEntity
import id.antasari.kampuscare.data.local.UrutanCatatan
import id.antasari.kampuscare.data.repository.CatatanRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DaftarViewModel(repository: CatatanRepository) : ViewModel() {
    val daftarCatatan: StateFlow<List<CatatanEntity>> = repository.daftarCatatan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val urutanAktif: StateFlow<UrutanCatatan> = repository.urutanAktif
}