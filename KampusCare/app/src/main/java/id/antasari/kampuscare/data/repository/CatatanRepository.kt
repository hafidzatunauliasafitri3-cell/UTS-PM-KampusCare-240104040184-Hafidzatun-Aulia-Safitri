package id.antasari.kampuscare.data.repository

import id.antasari.kampuscare.data.local.CatatanDao
import id.antasari.kampuscare.data.local.CatatanEntity
import id.antasari.kampuscare.data.local.PreferensiManager
import id.antasari.kampuscare.data.local.UrutanCatatan
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest

sealed class HasilValidasi {
    data object Valid : HasilValidasi()
    data class TidakValid(val errors: Map<String, String>) : HasilValidasi()
}

class CatatanRepository(
    private val dao: CatatanDao,
    private val preferensiManager: PreferensiManager
) {
    val urutanAktif: StateFlow<UrutanCatatan> = preferensiManager.urutanFlow
    val jumlahCatatan: Flow<Int> = dao.getJumlahCatatan()

    @OptIn(ExperimentalCoroutinesApi::class)
    val daftarCatatan: Flow<List<CatatanEntity>> = urutanAktif.flatMapLatest { urutan ->
        when (urutan) {
            UrutanCatatan.TERBARU -> dao.getAllTerbaru()
            UrutanCatatan.TERLAMA -> dao.getAllTerlama()
        }
    }

    fun ubahUrutan(urutan: UrutanCatatan) {
        preferensiManager.simpanUrutan(urutan)
    }

    fun observeCatatanById(id: Long): Flow<CatatanEntity?> = dao.observeById(id)

    suspend fun ambilCatatanById(id: Long): CatatanEntity? = dao.getById(id)

    fun validasiInput(
        judul: String,
        kategori: String,
        lokasi: String,
        deskripsi: String
    ): HasilValidasi {
        val errors = mutableMapOf<String, String>()

        val judulTrim = judul.trim()
        if (judulTrim.isEmpty()) {
            errors[FIELD_JUDUL] = "Judul wajib diisi"
        } else if (judulTrim.length !in 5..100) {
            errors[FIELD_JUDUL] = "Judul harus 5–100 karakter (saat ini ${judulTrim.length} karakter)"
        }

        if (kategori !in CatatanEntity.KATEGORI_VALID) {
            errors[FIELD_KATEGORI] = "Pilih salah satu kategori fasilitas yang tersedia"
        }

        val lokasiTrim = lokasi.trim()
        if (lokasiTrim.isEmpty()) {
            errors[FIELD_LOKASI] = "Lokasi wajib diisi"
        } else if (lokasiTrim.length !in 3..80) {
            errors[FIELD_LOKASI] = "Lokasi harus 3–80 karakter (saat ini ${lokasiTrim.length} karakter)"
        }

        val deskripsiTrim = deskripsi.trim()
        if (deskripsiTrim.isEmpty()) {
            errors[FIELD_DESKRIPSI] = "Deskripsi wajib diisi"
        } else if (deskripsiTrim.length !in 20..500) {
            errors[FIELD_DESKRIPSI] = "Deskripsi harus 20–500 karakter (saat ini ${deskripsiTrim.length} karakter)"
        }

        return if (errors.isEmpty()) HasilValidasi.Valid else HasilValidasi.TidakValid(errors)
    }

    suspend fun simpanCatatan(
        idLama: Long?,
        waktuDibuatLama: Long?,
        judul: String,
        kategori: String,
        lokasi: String,
        deskripsi: String
    ): Result<HasilValidasi> {
        val cekValidasi = validasiInput(judul, kategori, lokasi, deskripsi)
        if (cekValidasi is HasilValidasi.TidakValid) {
            return Result.success(cekValidasi)
        }

        return runCatching {
            val isModeUbah = idLama != null && idLama > 0L
            val entity = CatatanEntity(
                id = if (isModeUbah) idLama!! else 0L,
                judul = judul.trim(),
                kategori = kategori,
                lokasi = lokasi.trim(),
                deskripsi = deskripsi.trim(),
                waktuDibuat = if (isModeUbah && waktuDibuatLama != null) {
                    waktuDibuatLama
                } else {
                    System.currentTimeMillis()
                }
            )

            if (isModeUbah) {
                val barisTerubah = dao.update(entity)
                if (barisTerubah == 0) {
                    throw IllegalStateException("Catatan yang akan diubah tidak ditemukan di perangkat.")
                }
            } else {
                dao.insert(entity)
            }
            HasilValidasi.Valid
        }
    }

    suspend fun hapusCatatan(catatan: CatatanEntity): Result<Unit> = runCatching {
        dao.delete(catatan)
    }

    companion object {
        const val FIELD_JUDUL = "judul"
        const val FIELD_KATEGORI = "kategori"
        const val FIELD_LOKASI = "lokasi"
        const val FIELD_DESKRIPSI = "deskripsi"
    }
}