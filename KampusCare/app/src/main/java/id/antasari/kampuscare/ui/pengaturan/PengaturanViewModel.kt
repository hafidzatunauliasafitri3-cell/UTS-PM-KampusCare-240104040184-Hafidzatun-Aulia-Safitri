package id.antasari.kampuscare.ui.pengaturan

import androidx.lifecycle.ViewModel
import id.antasari.kampuscare.data.local.UrutanCatatan
import id.antasari.kampuscare.data.repository.CatatanRepository
import kotlinx.coroutines.flow.StateFlow

class PengaturanViewModel(private val repository: CatatanRepository) : ViewModel() {
    val urutanAktif: StateFlow<UrutanCatatan> = repository.urutanAktif

    fun setUrutan(urutan: UrutanCatatan) {
        repository.ubahUrutan(urutan)
    }
}