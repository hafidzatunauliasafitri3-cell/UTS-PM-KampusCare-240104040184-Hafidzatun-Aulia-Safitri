package id.antasari.kampuscare.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.antasari.kampuscare.data.repository.CatatanRepository
import id.antasari.kampuscare.ui.beranda.BerandaViewModel
import id.antasari.kampuscare.ui.daftar.DaftarViewModel
import id.antasari.kampuscare.ui.form.FormViewModel
import id.antasari.kampuscare.ui.pengaturan.PengaturanViewModel
import id.antasari.kampuscare.ui.rincian.RincianViewModel

class KampusCareViewModelFactory(
    private val repository: CatatanRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(BerandaViewModel::class.java) ->
                BerandaViewModel(repository) as T
            modelClass.isAssignableFrom(DaftarViewModel::class.java) ->
                DaftarViewModel(repository) as T
            modelClass.isAssignableFrom(RincianViewModel::class.java) ->
                RincianViewModel(repository) as T
            modelClass.isAssignableFrom(FormViewModel::class.java) ->
                FormViewModel(repository) as T
            modelClass.isAssignableFrom(PengaturanViewModel::class.java) ->
                PengaturanViewModel(repository) as T
            else -> throw IllegalArgumentException("ViewModel tidak dikenal: ${modelClass.name}")
        }
    }
}