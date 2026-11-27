package id.antasari.kampuscare.ui.pengaturan

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
import id.antasari.kampuscare.databinding.FragmentPengaturanBinding
import id.antasari.kampuscare.ui.KampusCareViewModelFactory
import kotlinx.coroutines.launch

class PengaturanFragment : Fragment() {

    private var _binding: FragmentPengaturanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PengaturanViewModel by viewModels {
        val ctx = requireContext().applicationContext
        val db = AppDatabase.getInstance(ctx)
        KampusCareViewModelFactory(CatatanRepository(db.catatanDao(), PreferensiManager(ctx)))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPengaturanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rgUrutanCatatan.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbTerbaru.id -> viewModel.setUrutan(UrutanCatatan.TERBARU)
                binding.rbTerlama.id -> viewModel.setUrutan(UrutanCatatan.TERLAMA)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.urutanAktif.collect { urutan ->
                    when (urutan) {
                        UrutanCatatan.TERBARU -> binding.rbTerbaru.isChecked = true
                        UrutanCatatan.TERLAMA -> binding.rbTerlama.isChecked = true
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