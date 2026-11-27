package id.antasari.kampuscare.ui.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.kampuscare.data.local.CatatanEntity
import id.antasari.kampuscare.data.repository.CatatanRepository
import id.antasari.kampuscare.data.repository.HasilValidasi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormUiState(
    val isSaving: Boolean = false,
    val dataAwalUntukUi: CatatanEntity? = null,
    val catatanTidakDitemukan: Boolean = false,
    val fieldErrors: Map<String, String> = emptyMap(),
    val pesanGagalSistem: String? = null,
    val simpanBerhasil: Boolean = false
)

class FormViewModel(private val repository: CatatanRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    private var idCatatanEdit: Long? = null
    private var waktuDibuatAsli: Long? = null
    private var dataAwalSudahDimuat = false

    fun inisialisasiModeUbah(id: Long) {
        if (id <= 0L || dataAwalSudahDimuat) return
        dataAwalSudahDimuat = true

        viewModelScope.launch {
            val catatan = repository.ambilCatatanById(id)
            if (catatan != null) {
                idCatatanEdit = catatan.id
                waktuDibuatAsli = catatan.waktuDibuat
                _uiState.update { it.copy(dataAwalUntukUi = catatan) }
            } else {
                _uiState.update { it.copy(catatanTidakDitemukan = true) }
            }
        }
    }

    fun tandaiDataAwalSudahDitampilkan() {
        _uiState.update { it.copy(dataAwalUntukUi = null) }
    }

    fun simpan(judul: String, kategori: String, lokasi: String, deskripsi: String) {
        if (_uiState.value.isSaving) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSaving = true, fieldErrors = emptyMap(), pesanGagalSistem = null)
            }

            val hasil = repository.simpanCatatan(
                idLama = idCatatanEdit,
                waktuDibuatLama = waktuDibuatAsli,
                judul = judul,
                kategori = kategori,
                lokasi = lokasi,
                deskripsi = deskripsi
            )

            hasil.fold(
                onSuccess = { validasi ->
                    when (validasi) {
                        is HasilValidasi.Valid -> {
                            _uiState.update { it.copy(isSaving = false, simpanBerhasil = true) }
                        }
                        is HasilValidasi.TidakValid -> {
                            _uiState.update {
                                it.copy(isSaving = false, fieldErrors = validasi.errors)
                            }
                        }
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            pesanGagalSistem = throwable.message ?: "Gagal menyimpan catatan ke perangkat."
                        )
                    }
                }
            )
        }
    }
}