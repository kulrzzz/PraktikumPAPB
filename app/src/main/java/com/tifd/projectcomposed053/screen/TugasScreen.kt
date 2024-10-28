package com.tifd.projectcomposed053.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tifd.projectcomposed053.data.model.local.Tugas
import com.tifd.projectcomposed053.data.model.local.TugasRepository
import com.tifd.projectcomposed053.viewmodel.MainViewModel
import com.tifd.projectcomposed053.viewmodel.MainViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TugasScreen(tugasRepository: TugasRepository) {
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(tugasRepository))
    val tugasList by mainViewModel.tugasList.observeAsState(emptyList())
    val isLoading by mainViewModel.isLoading.observeAsState(false)
    val errorMessage by mainViewModel.error.observeAsState()

    var matkul by remember { mutableStateOf("") }
    var detailTugas by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = Color(0xFF212121),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Tugas",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        },
        containerColor = Color(0xFF212121)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = matkul,
                onValueChange = { matkul = it },
                label = { Text("Matkul", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    focusedBorderColor = Color(0xFFBB86FC),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFFBB86FC),
                    unfocusedLabelColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = detailTugas,
                onValueChange = { detailTugas = it },
                label = { Text("Detail Tugas", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    focusedBorderColor = Color(0xFFBB86FC),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFFBB86FC),
                    unfocusedLabelColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (matkul.isNotBlank() && detailTugas.isNotBlank()) {
                        mainViewModel.addTugas(Tugas(matkul = matkul, detailTugas = detailTugas, selesai = false))
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Task added successfully")
                        }
                        matkul = ""
                        detailTugas = ""
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please fill in all fields")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EA),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Add Task", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFFBB86FC))
            } else if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage ?: "Unknown error",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tugasList) { tugas ->
                        TugasItem(tugas)
                    }
                }
            }
        }
    }
}

@Composable
fun TugasItem(tugas: Tugas) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF333333),
            contentColor = Color.White
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = tugas.matkul,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE0E0E0)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tugas.detailTugas,
                fontSize = 16.sp,
                color = Color(0xFFBDBDBD)
            )
        }
    }
}