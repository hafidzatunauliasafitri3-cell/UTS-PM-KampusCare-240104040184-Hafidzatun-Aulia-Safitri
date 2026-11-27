package id.antasari.kampuscare.ui.beranda

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
import id.antasari.kampuscare.data.repository.CatatanRepository
import id.antasari.kampuscare.databinding.FragmentBerandaBinding
import id.antasari.kampuscare.ui.KampusCareViewModelFactory
import id.antasari.kampuscare.ui.form.FormActivity
import kotlinx.coroutines.launch

class BerandaFragment : Fragment() {

    private var _binding: FragmentBerandaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BerandaViewModel by viewModels {
        val ctx = requireContext().applicationContext
        val db = AppDatabase.getInstance(ctx)
        KampusCareViewModelFactory(CatatanRepository(db.catatanDao(), PreferensiManager(ctx)))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBerandaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnTambahBeranda.setOnClickListener {
            startActivity(Intent(requireContext(), FormActivity::class.java))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.totalCatatan.collect { jumlah ->
                    binding.tvJumlahCatatan.text = jumlah.toString()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}