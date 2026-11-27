package id.antasari.kampuscare.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catatan_fasilitas")
data class CatatanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val judul: String,
    val kategori: String,
    val lokasi: String,
    val deskripsi: String,
    val waktuDibuat: Long
) {
    companion object {
        val KATEGORI_VALID = listOf(
            "Penerangan",
            "Kebersihan",
            "Perabot",
            "Jaringan",
            "Lainnya"
        )
    }
}