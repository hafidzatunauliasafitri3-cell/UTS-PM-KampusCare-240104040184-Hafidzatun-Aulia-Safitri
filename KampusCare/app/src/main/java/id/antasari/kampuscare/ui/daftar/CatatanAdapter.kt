package id.antasari.kampuscare.ui.daftar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.antasari.kampuscare.data.local.CatatanEntity
import id.antasari.kampuscare.databinding.ItemCatatanBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CatatanAdapter(
    private val onItemClick: (CatatanEntity) -> Unit
) : ListAdapter<CatatanEntity, CatatanAdapter.CatatanViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatatanViewHolder {
        val binding = ItemCatatanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CatatanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatatanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CatatanViewHolder(
        private val binding: ItemCatatanBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"))

        fun bind(item: CatatanEntity) {
            binding.tvItemJudul.text = item.judul
            binding.chipItemKategori.text = item.kategori
            binding.tvItemLokasi.text = item.lokasi
            binding.tvItemWaktu.text = formatter.format(Date(item.waktuDibuat))
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CatatanEntity>() {
            override fun areItemsTheSame(oldItem: CatatanEntity, newItem: CatatanEntity): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CatatanEntity, newItem: CatatanEntity): Boolean =
                oldItem == newItem
        }
    }
}