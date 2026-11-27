package id.antasari.kampuscare.ui.daftar

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import id.antasari.kampuscare.data.local.AppDatabase
import id.antasari.kampuscare.data.local.PreferensiManager
import id.antasari.kampuscare.data.local.UrutanCatatan
import id.antasari.kampuscare.data.repository.CatatanRepository
import id.antasari.kampuscare.databinding.FragmentDaftarBinding
import id.antasari.kampuscare.ui.KampusCareViewModelFactory
import id.antasari.kampuscare.MainActivity
import id.antasari.kampuscare.ui.form.FormActivity
import kotlinx.coroutines.launch

class DaftarFragment : Fragment() {

    private var _binding: FragmentDaftarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DaftarViewModel by viewModels {
        val ctx = requireContext().applicationContext
        val db = AppDatabase.getInstance(ctx)
        KampusCareViewModelFactory(CatatanRepository(db.catatanDao(), PreferensiManager(ctx)))
    }

    private lateinit var adapter: CatatanAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDaftarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CatatanAdapter { catatan ->
            (activity as? MainActivity)?.bukaRincianCatatan(catatan.id)
        }
        binding.rvCatatan.adapter = adapter

        val bukaFormTambah = {
            startActivity(Intent(requireContext(), FormActivity::class.java))
        }
        binding.fabTambah.setOnClickListener { bukaFormTambah() }
        binding.btnBuatPertama.setOnClickListener { bukaFormTambah() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.urutanAktif.collect { urutan ->
                        binding.tvInfoUrutan.text = when (urutan) {
                            UrutanCatatan.TERBARU -> "Urutan tampilan: Terbaru"
                            UrutanCatatan.TERLAMA -> "Urutan tampilan: Terlama"
                        }
                    }
                }
                launch {
                    viewModel.daftarCatatan.collect { list ->
                        adapter.submitList(list)
                        val kosong = list.isEmpty()
                        binding.layoutEmptyState.visibility = if (kosong) View.VISIBLE else View.GONE
                        binding.rvCatatan.visibility = if (kosong) View.GONE else View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}