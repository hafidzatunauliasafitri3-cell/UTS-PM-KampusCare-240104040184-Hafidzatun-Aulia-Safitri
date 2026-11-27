package id.antasari.kampuscare.ui.rincian

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.antasari.kampuscare.data.local.AppDatabase
import id.antasari.kampuscare.data.local.CatatanEntity
import id.antasari.kampuscare.data.local.PreferensiManager
import id.antasari.kampuscare.data.repository.CatatanRepository
import id.antasari.kampuscare.databinding.FragmentRincianBinding
import id.antasari.kampuscare.ui.KampusCareViewModelFactory
import id.antasari.kampuscare.MainActivity
import id.antasari.kampuscare.ui.form.FormActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

class RincianFragment : Fragment() {

    private var _binding: FragmentRincianBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RincianViewModel by viewModels {
        val ctx = requireContext().applicationContext
        val db = AppDatabase.getInstance(ctx)
        KampusCareViewModelFactory(CatatanRepository(db.catatanDao(), PreferensiManager(ctx)))
    }

    private var catatanAktif: CatatanEntity? = null
    private val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRincianBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val catatanId = arguments?.getLong(ARG_CATATAN_ID, -1L) ?: -1L

        binding.toolbarRincian.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnUbahCatatan.setOnClickListener {
            catatanAktif?.let { catatan ->
                val intent = Intent(requireContext(), FormActivity::class.java).apply {
                    putExtra(FormActivity.EXTRA_CATATAN_ID, catatan.id)
                }
                startActivity(intent)
            }
        }

        binding.btnHapusCatatan.setOnClickListener {
            catatanAktif?.let { catatan ->
                konfirmasiHapus(catatan)
            }
        }

        viewModel.muatCatatan(catatanId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is RincianUiState.Memuat -> Unit
                        is RincianUiState.Ditemukan -> {
                            catatanAktif = state.catatan
                            binding.tvRincianJudul.text = state.catatan.judul
                            binding.tvRincianMeta.text =
                                "Kategori: ${state.catatan.kategori}\n" +
                                        "Lokasi: ${state.catatan.lokasi}\n" +
                                        "Dibuat: ${formatter.format(Date(state.catatan.waktuDibuat))}"
                            binding.tvRincianDeskripsi.text = state.catatan.deskripsi
                        }
                        is RincianUiState.TidakDitemukan -> {
                            Toast.makeText(
                                requireContext(),
                                "Catatan tidak ditemukan",
                                Toast.LENGTH_SHORT
                            ).show()
                            (activity as? MainActivity)?.pindahKeDaftarDariRincian()
                        }
                        is RincianUiState.BerhasilDihapus -> {
                            Toast.makeText(
                                requireContext(),
                                "Catatan berhasil dihapus",
                                Toast.LENGTH_SHORT
                            ).show()
                            (activity as? MainActivity)?.pindahKeDaftarDariRincian()
                        }
                    }
                }
            }
        }
    }

    private fun konfirmasiHapus(catatan: CatatanEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Catatan")
            .setMessage("Yakin ingin menghapus catatan \"${catatan.judul}\" dari perangkat?")
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.hapusCatatan(catatan)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CATATAN_ID = "arg_catatan_id"

        fun newInstance(catatanId: Long): RincianFragment {
            return RincianFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CATATAN_ID, catatanId)
                }
            }
        }
    }
}