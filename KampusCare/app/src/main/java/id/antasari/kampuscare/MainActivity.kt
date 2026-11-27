package id.antasari.kampuscare

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import id.antasari.kampuscare.databinding.ActivityMainBinding
import id.antasari.kampuscare.ui.beranda.BerandaFragment
import id.antasari.kampuscare.ui.daftar.DaftarFragment
import id.antasari.kampuscare.ui.pengaturan.PengaturanFragment
import id.antasari.kampuscare.ui.rincian.RincianFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.fragmentContainer.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right
            )
            binding.bottomNavigation.updatePadding(
                left = systemBars.left,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }

        if (savedInstanceState == null) {
            tampilkanTabFragment(BerandaFragment(), addToBackStack = false)
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_beranda -> {
                    tampilkanTabFragment(BerandaFragment(), addToBackStack = false)
                    true
                }
                R.id.menu_daftar -> {
                    tampilkanTabFragment(DaftarFragment(), addToBackStack = false)
                    true
                }
                R.id.menu_pengaturan -> {
                    tampilkanTabFragment(PengaturanFragment(), addToBackStack = false)
                    true
                }
                else -> false
            }
        }
    }

    fun bukaRincianCatatan(catatanId: Long) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RincianFragment.newInstance(catatanId))
            .addToBackStack("rincian_catatan")
            .commit()
    }

    fun pindahKeDaftarDariRincian() {
        supportFragmentManager.popBackStack()
        binding.bottomNavigation.selectedItemId = R.id.menu_daftar
    }

    private fun tampilkanTabFragment(fragment: Fragment, addToBackStack: Boolean) {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
        val tx = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            tx.addToBackStack(null)
        }
        tx.commit()
    }
}