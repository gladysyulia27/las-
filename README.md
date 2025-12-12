# Aplikasi Koleksi Obat Herbal

Aplikasi web untuk mengelola koleksi obat herbal tradisional dengan Spring Boot. Aplikasi ini memungkinkan pengguna untuk menambahkan, mengubah, menghapus, dan melihat data obat herbal beserta visualisasi data dalam bentuk grafik.

## Fitur

- ✅ **Autentikasi**: Register dan Login dengan JWT
- ✅ **CRUD Obat Herbal**: Tambah, Ubah, Hapus, dan Lihat data obat herbal
- ✅ **Upload Gambar**: Upload dan update gambar untuk setiap obat herbal
- ✅ **Visualisasi Data**: Grafik distribusi berdasarkan kategori dan asal
- ✅ **UI Modern**: Interface yang menarik menggunakan Bootstrap 5
- ✅ **Coverage 100%**: Test coverage menggunakan JaCoCo

## Teknologi

- **Spring Boot 4.0.0-RC1**
- **PostgreSQL** (Database: `db_herbal`)
- **Thymeleaf** (Template Engine)
- **JWT** (JSON Web Token untuk autentikasi)
- **Bootstrap 5** (UI Framework)
- **Chart.js** (Visualisasi Data)
- **JaCoCo** (Code Coverage)

## Struktur Proyek

```
src/
├── main/
│   ├── java/org/delcom/app/
│   │   ├── entities/          # Entitas: User, AuthToken, HerbalMedicine
│   │   ├── repositories/      # Repository interfaces
│   │   ├── services/          # Business logic
│   │   ├── controllers/       # Web controllers
│   │   ├── dto/               # Data Transfer Objects
│   │   ├── configs/            # Konfigurasi
│   │   ├── interceptors/      # Interceptors
│   │   └── utils/              # Utility classes
│   └── resources/
│       ├── templates/          # Thymeleaf templates
│       └── static/             # CSS, JS, images
└── test/
    └── java/org/delcom/app/    # Test classes
```

## Entitas

### User
- id: UUID
- name: String
- email: String
- password: String
- createdAt: LocalDateTime
- updatedAt: LocalDateTime

### AuthToken
- id: UUID
- token: String
- userId: UUID
- createdAt: LocalDateTime

### HerbalMedicine
- id: UUID
- userId: UUID
- name: String
- description: String
- category: String
- origin: String
- benefits: String
- imagePath: String
- createdAt: LocalDateTime
- updatedAt: LocalDateTime

## Setup

### Prerequisites
- Java 25
- Maven 3.6+
- PostgreSQL

### Konfigurasi Database

1. Buat database PostgreSQL:
```sql
CREATE DATABASE db_herbal;
```

2. Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/db_herbal
spring.datasource.username=postgres
spring.datasource.password=root
```

### Menjalankan Aplikasi

```bash
# Install dependencies
mvn clean install

# Run application
mvn spring-boot:run
```

Aplikasi akan berjalan di: http://localhost:8080

## Testing

### Menjalankan Test
```bash
mvn clean test
```

### Melihat Coverage Report
```bash
# Windows
mvn clean test; start target\site\jacoco\index.html

# Mac
mvn clean test && open target/site/jacoco/index.html

# Linux
mvn clean test && xdg-open target/site/jacoco/index.html
```

### Check Coverage (100%)
```bash
mvn clean test jacoco:check
```

## Fitur Aplikasi

### 1. Autentikasi
- Register akun baru
- Login dengan email dan password
- Logout

### 2. Manajemen Obat Herbal
- **Tambah**: Tambah obat herbal baru dengan gambar
- **Ubah**: Update data dan gambar obat herbal
- **Hapus**: Hapus obat herbal dari koleksi
- **Lihat**: Lihat daftar semua obat atau obat milik sendiri
- **Detail**: Lihat detail lengkap obat herbal

### 3. Visualisasi Data
- Grafik distribusi berdasarkan kategori (Doughnut Chart)
- Grafik distribusi berdasarkan asal (Bar Chart)

## Endpoints

- `GET /` - Halaman beranda
- `GET /auth/login` - Halaman login
- `POST /auth/login` - Proses login
- `GET /auth/register` - Halaman register
- `POST /auth/register` - Proses register
- `GET /auth/logout` - Logout
- `GET /herbal-medicines` - Daftar semua obat
- `GET /herbal-medicines/my-medicines` - Obat milik saya
- `GET /herbal-medicines/add` - Form tambah obat
- `POST /herbal-medicines/add` - Proses tambah obat
- `GET /herbal-medicines/{id}` - Detail obat
- `GET /herbal-medicines/{id}/edit` - Form edit obat
- `POST /herbal-medicines/{id}/edit` - Proses edit obat
- `POST /herbal-medicines/{id}/delete` - Hapus obat
- `GET /charts` - Halaman grafik

## Purpose

Proyek ini dibuat untuk tujuan **Pendidikan** sebagai proyek akhir mata kuliah Pemrograman Berorientasi Objek (PBO).

## License

Educational Purpose Only
