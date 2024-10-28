package com.tifd.projectcomposed053.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore

// Data class JadwalKuliah
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
        containerColor = Color(0xFF212121) // Menyatukan tampilan dengan background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Jadwal Kuliah",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFBB86FC))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
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
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF333333),
            contentColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = jadwal.mataKuliah,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE0E0E0)
            )
            Text(
                text = "${jadwal.jamMulai} - ${jadwal.jamSelesai}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFBDBDBD)
            )
            Text(
                text = "Ruang: ${jadwal.ruang}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFBDBDBD)
            )
            Text(
                text = jadwal.hari.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFBB86FC)
            )
        }
    }
}