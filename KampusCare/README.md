# KampusCare

Aplikasi Android untuk mencatat dan mengelola temuan fasilitas kampus (lampu padam, kursi rusak, ruangan kurang bersih, jaringan bermasalah, dan sebagainya) langsung di perangkat pengguna. Data tersimpan secara lokal dan tetap tersedia setelah aplikasi dihentikan dan dibuka ulang.

Proyek ini dikerjakan untuk Ujian Tengah Semester (UTS) mata kuliah Pemrograman Mobile.

## Identitas

| | |
|---|---|
| Nama | Noor Azkia Salsabila |
| NIM | 240104040183 |
| Kelas | TI 24 B |
| Program Studi | Teknologi Informasi |
| Fakultas | Dakwah dan Ilmu Komunikasi |
| Universitas | Universitas Islam Negeri Antasari Banjarmasin |
| Mata Kuliah | Pemrograman Mobile |
| Dosen Pengampu | Muhayat, M.IT |

## Fitur

- **Beranda**: nama aplikasi, penjelasan singkat, jumlah catatan yang dibaca dari database, dan tombol Tambah Catatan.
- **Daftar Catatan**: RecyclerView berisi judul, kategori, lokasi, dan waktu pembuatan; setiap item membuka rincian. Tersedia kondisi kosong dengan petunjuk.
- **Rincian Catatan**: seluruh informasi catatan, keterangan "Tersimpan di perangkat", serta tombol Ubah dan Hapus (dengan dialog konfirmasi).
- **Formulir Tambah/Ubah**: input judul, kategori, lokasi, dan deskripsi dengan validasi dan pesan kesalahan per field. Input tidak dikosongkan saat validasi gagal.
- **Pengaturan**: pilihan urutan daftar (terbaru/terlama) yang disimpan di SharedPreferences, serta identitas pengembang.

### Aturan validasi

| Field | Ketentuan |
|---|---|
| Judul | Wajib, 5–100 karakter setelah spasi tepi dihapus |
| Kategori | Wajib dipilih: Penerangan, Kebersihan, Perabot, Jaringan, Lainnya |
| Lokasi | Wajib, 3–80 karakter |
| Deskripsi | Wajib, 20–500 karakter |

## Versi Alat

| Komponen | Versi |
|---|---|
| Bahasa / UI | Kotlin, XML Views, ViewBinding, Material Components (Material 3 DayNight) |
| Android Gradle Plugin | 9.5.2 |
| Gradle Wrapper | 9.9.1 |
| KSP | 2.1.20-1.0.32 |
| Room | 2.7.0 (`exportSchema = true`) |
| Material / AppCompat | 1.14.0 / 1.8.0 |
| Fragment-ktx / Lifecycle / Coroutines | 1.8.5 / 2.8.6 / 1.9.0 |
| minSdk / targetSdk / compileSdk | 26 / 37 / 37 |
| Java compatibility | VERSION_11 |
| Perangkat uji | Android Emulator Pixel 8, Android 14 (API 34) |

## Cara Menjalankan

1. Pastikan Android Studio dan Android SDK (API 37) sudah terpasang.
2. Clone repository ini:
   ```bash
   git clone <URL-REPOSITORY>
   cd KampusCare
   ```
3. Buka folder proyek di Android Studio, lalu tunggu sinkronisasi Gradle selesai.
4. Jalankan pada emulator atau perangkat fisik (Android 8.0 / API 26 ke atas) melalui tombol **Run**, atau dari terminal:
   ```bash
   ./gradlew assembleDebug        # Windows: gradlew.bat assembleDebug
   ```
   Hasil build: `app/build/outputs/apk/debug/app-debug.apk` (± 7,5 MB).

## Arsitektur

Aplikasi memakai arsitektur berlapis dengan pemisahan tanggung jawab:

```
Activity / Fragment  →  ViewModel  →  Repository  →  DAO / Room  →  SQLite
                                          └────────→  SharedPreferences (urutan daftar)
```

| Lapisan | Tanggung jawab |
|---|---|
| Activity / Fragment | Menampilkan data, menangani klik, menampilkan pesan |
| ViewModel | Menyimpan state tampilan (Memuat, Kosong, Gagal, Berhasil); memanggil Repository di `viewModelScope` |
| Repository | Validasi, akses DAO, dan urutan daftar dari SharedPreferences |
| DAO / Room | Operasi CRUD dan query pada SQLite |
| SharedPreferences | Hanya menyimpan pilihan urutan daftar |

### Struktur proyek

```
id.antasari.kampuscare
├── MainActivity.kt                 (induk Fragment + BottomNavigation)
├── data
│   ├── local
│   │   ├── CatatanEntity.kt        (Entity: tabel catatan_fasilitas)
│   │   ├── CatatanDao.kt           (DAO: query & CRUD)
│   │   ├── AppDatabase.kt          (RoomDatabase, singleton)
│   │   └── PreferensiManager.kt    (SharedPreferences: urutan daftar)
│   └── repository
│       └── CatatanRepository.kt    (validasi + akses DAO + urutan)
└── ui
    ├── KampusCareViewModelFactory.kt
    ├── beranda/    BerandaFragment, BerandaViewModel
    ├── daftar/     DaftarFragment, DaftarViewModel, CatatanAdapter
    ├── rincian/    RincianFragment, RincianViewModel
    ├── form/       FormActivity, FormViewModel
    └── pengaturan/ PengaturanFragment, PengaturanViewModel
```

### Penyimpanan data

- **Room** (`kampuscare_database.db`), tabel `catatan_fasilitas`: `id` (primary key otomatis), `judul`, `kategori`, `lokasi`, `deskripsi`, `waktuDibuat` (epoch ms). Skema diekspor ke `app/schemas/`.
- **SharedPreferences** (`kampuscare_prefs.xml`), kunci `pref_urutan_catatan` bernilai `TERBARU` atau `TERLAMA`.

## Hasil Pengujian

Pengujian dilakukan secara manual pada emulator Pixel 8 (API 34). **15 dari 15 skenario lulus** dan tidak ditemukan masalah yang memerlukan perbaikan.

| ID | Skenario | Status |
|---|---|---|
| U01 | Membuka aplikasi tanpa catatan | Lulus |
| U02 | Menyimpan formulir kosong atau tidak valid | Lulus |
| U03 | Menambah catatan valid | Lulus |
| U04 | Menambah lima catatan | Lulus |
| U05 | Memilih item daftar | Lulus |
| U06 | Mengubah catatan | Lulus |
| U07 | Membatalkan penghapusan | Lulus |
| U08 | Mengonfirmasi penghapusan | Lulus |
| U09 | Mengganti urutan daftar | Lulus |
| U10 | Force stop setelah operasi selesai, lalu buka ulang | Lulus |
| U11 | Rotasi saat mengisi atau menyunting | Lulus |
| U12 | Navigasi dan tombol Back | Lulus |
| U13 | Layar sempit, landscape, dan keyboard | Lulus |
| U14 | Mode pesawat dan ketukan simpan berulang | Lulus |
| U15 | Membuka ID yang tidak ditemukan (skenario terkontrol) | Lulus |

Bukti tangkapan layar dan penjelasan lengkap tersedia pada laporan UTS.

## Keterbatasan

- Data hanya tersimpan pada satu perangkat tanpa backup maupun sinkronisasi.
- Belum tersedia pencarian, penyaringan kategori, atau lampiran foto.
- Urutan daftar hanya memiliki dua pilihan (terbaru dan terlama).
- Pengujian dilakukan secara manual dan belum dilengkapi pengujian otomatis.
