import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bcbt.Auth
import com.example.bcbt.Constants
import com.example.bcbt.GradeMateColors
import com.example.bcbt.R
import com.example.bcbt.Routes
import com.example.bcbt.loadGrades
import com.example.bcbt.loadStudent
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale
import java.util.Locale.getDefault

@Composable
fun Login(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val usernameFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var isClicked by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        usernameFocusRequester.requestFocus()
    }
    BackHandler {
        navController.navigate(Routes.splash) {
            popUpTo(0) { inclusive = true } // Clear entire backstack
            launchSingleTop = true          // Prevent duplicate splash
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GradeMateColors.backGradient)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "BCBT Logo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to ${Constants.title}",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF0D47A1)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Username field
            OutlinedTextField(
                value = username.uppercase(getDefault()),
                onValueChange = { if (it.length <= 16) username = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(usernameFocusRequester),
                placeholder = { Text("e.g NS0034/0134/2025", style = TextStyle(color = GradeMateColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)) },
                label = { Text("Username", style = TextStyle(color = GradeMateColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)) },
                textStyle = TextStyle(color = GradeMateColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() }
                ),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1976D2),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFF1976D2)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { if (it.length <= 20) password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester),
                label = { Text("Password", style = TextStyle(color = GradeMateColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)) },
                textStyle = TextStyle(color = GradeMateColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) R.drawable.baseline_visibility_off_24 else R.drawable.visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(id = icon),
                            tint = GradeMateColors.Primary,
                            modifier = Modifier.size(24.dp),
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1976D2),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFF1976D2)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
            if(!isClicked){
                Button(
                    onClick = {
                        if(username.isNotEmpty() && password.isNotEmpty()){
                            isClicked = true
                            signInWithEmail(username.lowercase(Locale.getDefault()).replace("/","")+"@gmail.com", password) { success, error ->
                                if (success) {
                                    // Navigate to dashboard or home screen
                                    isClicked = false
                                    Auth.setId(username)
                                   // Toast.makeText(context, Auth.id.value,Toast.LENGTH_SHORT).show()
                                    navController.navigate(Routes.gradeHome)
                                    loadStudent()
                                } else {
                                    isClicked = false
                                    Toast.makeText(context,error,Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2),
                        contentColor = Color.White,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.DarkGray
                    )
                ) {
                    Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }else{
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp),
                    color = GradeMateColors.Primary,
                    strokeWidth = 3.dp
                )            }


            Spacer(modifier = Modifier.height(12.dp))

            // Optional: Forgot password or register
            Text(
                text = "Forgot password?",
                modifier = Modifier.clickable { /* TODO: handle recovery */ },
                color = Color(0xFF1976D2),
                fontSize = 14.sp
            )


        }
    }
}
//sign in function
fun signInWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
    val auth = FirebaseAuth.getInstance()

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, null)
            } else {
                onResult(false, task.exception?.message)
            }
        }
}