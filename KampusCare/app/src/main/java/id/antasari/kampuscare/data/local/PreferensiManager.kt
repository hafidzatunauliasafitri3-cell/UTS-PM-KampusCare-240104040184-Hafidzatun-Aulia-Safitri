package id.antasari.kampuscare.data.local

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class UrutanCatatan {
    TERBARU,
    TERLAMA
}

class PreferensiManager(context: Context) {

    private val prefs = context.getSharedPreferences("kampuscare_prefs", Context.MODE_PRIVATE)

    private val _urutanFlow = MutableStateFlow(bacaUrutanTersimpan())
    val urutanFlow: StateFlow<UrutanCatatan> = _urutanFlow.asStateFlow()

    private fun bacaUrutanTersimpan(): UrutanCatatan {
        val nilai = prefs.getString(KEY_URUTAN, UrutanCatatan.TERBARU.name)
        return runCatching { UrutanCatatan.valueOf(nilai ?: UrutanCatatan.TERBARU.name) }
            .getOrDefault(UrutanCatatan.TERBARU)
    }

    fun simpanUrutan(urutan: UrutanCatatan) {
        prefs.edit().putString(KEY_URUTAN, urutan.name).apply()
        _urutanFlow.value = urutan
    }

    companion object {
        private const val KEY_URUTAN = "pref_urutan_catatan"
    }
}