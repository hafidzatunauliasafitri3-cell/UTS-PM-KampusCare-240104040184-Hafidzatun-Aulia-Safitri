package id.antasari.kampuscare.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CatatanDao {

    @Query("SELECT * FROM catatan_fasilitas ORDER BY waktuDibuat DESC, id DESC")
    fun getAllTerbaru(): Flow<List<CatatanEntity>>

    @Query("SELECT * FROM catatan_fasilitas ORDER BY waktuDibuat ASC, id ASC")
    fun getAllTerlama(): Flow<List<CatatanEntity>>

    @Query("SELECT COUNT(*) FROM catatan_fasilitas")
    fun getJumlahCatatan(): Flow<Int>

    @Query("SELECT * FROM catatan_fasilitas WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<CatatanEntity?>

    @Query("SELECT * FROM catatan_fasilitas WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CatatanEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(catatan: CatatanEntity): Long

    @Update
    suspend fun update(catatan: CatatanEntity): Int

    @Delete
    suspend fun delete(catatan: CatatanEntity): Int
}