package com.tifd.projectcomposed053.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore

// Tambahkan data class JadwalKuliah di sini
data class JadwalKuliah(
    val mataKuliah: String = "",
    val jamMulai: String = "",
    val jamSelesai: String = "",
    val ruang: String = "",
    val hari: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatkulScreen(navController: NavHostController = rememberNavController()) {
    val firestore = FirebaseFirestore.getInstance()
    val jadwalList = remember { mutableStateListOf<JadwalKuliah>() }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch data dari Firestore
    LaunchedEffect(Unit) {
        firestore.collection("Jadwal Kuliah")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val jadwal = JadwalKuliah(
                        mataKuliah = document.getString("Mata_Kuliah") ?: "",
                        jamMulai = document.getString("Jam_Mulai") ?: "",
                        jamSelesai = document.getString("Jam_Selesai") ?: "",
                        ruang = document.getString("Ruang") ?: "",
                        hari = document.getString("Hari") ?: ""
                    )
                    jadwalList.add(jadwal)
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jadwal Kuliah", style = MaterialTheme.typography.headlineMedium) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center // Posisi indikator di tengah
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(jadwalList) { jadwal ->
                        ScheduleItem(jadwal)
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleItem(jadwal: JadwalKuliah) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD) // Warna background kartu yang lebih estetik
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = jadwal.mataKuliah, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${jadwal.jamMulai} - ${jadwal.jamSelesai}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Ruang: ${jadwal.ruang}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = jadwal.hari.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EA))
        }
    }
}