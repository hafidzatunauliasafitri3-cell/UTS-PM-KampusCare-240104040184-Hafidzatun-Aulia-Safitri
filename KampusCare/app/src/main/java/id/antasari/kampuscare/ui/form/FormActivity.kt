package id.antasari.kampuscare.ui.form

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import id.antasari.kampuscare.data.local.AppDatabase
import id.antasari.kampuscare.data.local.CatatanEntity
import id.antasari.kampuscare.data.local.PreferensiManager
import id.antasari.kampuscare.data.repository.CatatanRepository
import id.antasari.kampuscare.databinding.ActivityFormBinding
import id.antasari.kampuscare.ui.KampusCareViewModelFactory
import kotlinx.coroutines.launch

class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding

    private val viewModel: FormViewModel by viewModels {
        val db = AppDatabase.getInstance(applicationContext)
        val prefs = PreferensiManager(applicationContext)
        KampusCareViewModelFactory(CatatanRepository(db.catatanDao(), prefs))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Lifecycle: onCreate called")
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }

        val kategoriAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            CatatanEntity.KATEGORI_VALID
        )
        binding.actvKategori.setAdapter(kategoriAdapter)

        val catatanId = intent.getLongExtra(EXTRA_CATATAN_ID, -1L)
        if (catatanId > 0L) {
            binding.toolbarForm.title = "Ubah Catatan"
            viewModel.inisialisasiModeUbah(catatanId)
        } else {
            binding.toolbarForm.title = "Tambah Catatan"
        }

        binding.toolbarForm.setNavigationOnClickListener { finish() }

        binding.btnSimpanPerangkat.setOnClickListener {
            viewModel.simpan(
                judul = binding.etJudul.text?.toString().orEmpty(),
                kategori = binding.actvKategori.text?.toString().orEmpty(),
                lokasi = binding.etLokasi.text?.toString().orEmpty(),
                deskripsi = binding.etDeskripsi.text?.toString().orEmpty()
            )
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.btnSimpanPerangkat.isEnabled = !state.isSaving

                    state.dataAwalUntukUi?.let { catatan ->
                        binding.etJudul.setText(catatan.judul)
                        binding.actvKategori.setText(catatan.kategori, false)
                        binding.etLokasi.setText(catatan.lokasi)
                        binding.etDeskripsi.setText(catatan.deskripsi)
                        viewModel.tandaiDataAwalSudahDitampilkan()
                    }

                    binding.tilJudul.error = state.fieldErrors[CatatanRepository.FIELD_JUDUL]
                    binding.tilKategori.error = state.fieldErrors[CatatanRepository.FIELD_KATEGORI]
                    binding.tilLokasi.error = state.fieldErrors[CatatanRepository.FIELD_LOKASI]
                    binding.tilDeskripsi.error = state.fieldErrors[CatatanRepository.FIELD_DESKRIPSI]

                    if (state.catatanTidakDitemukan) {
                        Toast.makeText(this@FormActivity, "Catatan tidak ditemukan", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                    state.pesanGagalSistem?.let { pesan ->
                        Toast.makeText(this@FormActivity, pesan, Toast.LENGTH_LONG).show()
                    }

                    if (state.simpanBerhasil) {
                        Toast.makeText(this@FormActivity, "Catatan berhasil disimpan ke perangkat", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "Lifecycle: onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Lifecycle: onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "Lifecycle: onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "Lifecycle: onStop called")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "Lifecycle: onSaveInstanceState called")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "Lifecycle: onRestoreInstanceState called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "Lifecycle: onRestart called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Lifecycle: onDestroy called")
    }

    companion object {
        const val EXTRA_CATATAN_ID = "extra_catatan_id"
        private const val TAG = "FormActivity"
    }
}