package com.tifd.projectcomposed053

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.tifd.projectcomposed053.data.model.local.TugasRepository
import com.tifd.projectcomposed053.navigation.NavigationItem
import com.tifd.projectcomposed053.navigation.Screen
import com.tifd.projectcomposed053.screen.MatkulScreen
import com.tifd.projectcomposed053.screen.ProfileScreen
import com.tifd.projectcomposed053.screen.TugasScreen
import com.tifd.projectcomposed053.ui.theme.PraktikumPAPBTheme

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase only once
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }

        auth = FirebaseAuth.getInstance()

        // Inisialisasi TugasRepository dengan context aplikasi
        val tugasRepository = TugasRepository(application)

        setContent {
            PraktikumPAPBTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainActivityContent(tugasRepository = tugasRepository) // Teruskan tugasRepository ke MainActivityContent
                }
            }
        }
    }

    fun performLogin(email: String, password: String, onLoginSuccess: () -> Unit) {
        val trimmedEmail = email.trim()

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(trimmedEmail, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    onLoginSuccess() // Panggil fungsi ini untuk beralih ke screen utama setelah login
                } else {
                    val errorMessage = task.exception?.message ?: "Authentication failed"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    Log.e("MainActivity", "Login failed: $errorMessage", task.exception)
                }
            }
    }
}

@Composable
fun MainActivityContent(tugasRepository: TugasRepository) {
    val navController = rememberNavController() // Create NavController for navigating screens
    var isLoggedIn by remember { mutableStateOf(false) } // Menyimpan status login

    if (isLoggedIn) {
        // Jika sudah login, tampilkan navigasi utama
        Scaffold(
            bottomBar = { BottomBar(navController = navController) },  // Bottom Navigation Bar
            modifier = Modifier
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Matkul.route,  // Start from the Matkul screen
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Matkul.route) {
                    MatkulScreen()
                }
                composable(Screen.Tugas.route) {
                    TugasScreen(tugasRepository = tugasRepository) // Teruskan tugasRepository ke TugasScreen
                }
                composable(Screen.Profil.route) {
                    ProfileScreen(username = "kulrzzz") // Ganti dengan username GitHub Anda
                }
            }
        }
    } else {
        // Jika belum login, tampilkan login screen
        LoginScreen { email, password ->
            val activity = (navController.context as MainActivity)
            activity.performLogin(email, password) {
                isLoggedIn = true // Ubah status login jika login berhasil
            }
        }
    }
}

@Composable
private fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        val navigationItems = listOf(
            NavigationItem(
                title = "Matkul",
                icon = Icons.Default.Search,
                screen = Screen.Matkul
            ),
            NavigationItem(
                title = "Tugas",
                icon = Icons.Default.List,
                screen = Screen.Tugas
            ),
            NavigationItem(
                title = "Profil",
                icon = Icons.Default.Person,
                screen = Screen.Profil
            )
        )

        val currentRoute = navController.currentBackStackEntry?.destination?.route
        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(item.icon, contentDescription = item.title)
                },
                label = {
                    Text(item.title)
                },
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                alwaysShowLabel = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLogin: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isButtonEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Email field dengan icon orang
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                isButtonEnabled = email.isNotBlank() && password.isNotBlank()
            },
            label = { Text("Email") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = "Person Icon")
            },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF6200EA),
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field dengan icon gembok
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                isButtonEnabled = email.isNotBlank() && password.isNotBlank()
            },
            label = { Text("Password") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock Icon")
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF6200EA),
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onLogin(email, password) },
            enabled = isButtonEnabled,
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .padding(8.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isButtonEnabled) Color(0xFF6200EA) else Color.Gray
            )
        ) {
            Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}