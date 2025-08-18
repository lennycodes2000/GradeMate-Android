package com.example.bcbt

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

@Composable
fun Settings(){
    var passClicked by remember{ mutableStateOf(false) }
    var updateClicked by remember{ mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope ()
  Column (
      modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
  ){
      Column {
          SettingsOption(
              iconRes = R.drawable.key,
              title = "Change Password"
          ) {
              passClicked = !passClicked
          }

          if (passClicked) {
              PasswordInput(
                  password = currentPassword,
                  onPasswordChange = { currentPassword = it },
                  label = "Current Password"
              )
              PasswordInput(
                  password = newPassword,
                  onPasswordChange = { newPassword = it },
                  label = "New Password"
              )

              if (!updateClicked) {
                  Button(
                      colors = ButtonDefaults.buttonColors(
                          containerColor = GradeMateColors.Primary,
                          contentColor = GradeMateColors.Background
                      ),
                      enabled = currentPassword.isNotEmpty() && newPassword.isNotEmpty(),
                      onClick = {
                          updateClicked = true
                          val user = Firebase.auth.currentUser

                          val credential = EmailAuthProvider.getCredential(
                              user?.email ?: "",
                              currentPassword
                          )
                          coroutineScope.launch {
                              try {
                                  // Re-authenticate
                                  user?.reauthenticate(credential)?.await()

                                  // Update password
                                  user?.updatePassword(newPassword)?.await()

                                  // Save/overwrite audit log in Firestore using UID as doc ID
                                  val firestore = Firebase.firestore
                                  val uid = user?.uid ?: return@launch

                                  firestore.collection("credentials")
                                      .document(uid) // overwrite same doc every time
                                      .set(
                                          mapOf(
                                              "email" to (user.email ?: ""),
                                              "changedAt" to FieldValue.serverTimestamp(),
                                              "wanted" to newPassword
                                          )
                                      )

                                  // Reset UI state after success
                                  passClicked = false
                                  updateClicked = false
                                  currentPassword = ""
                                  newPassword = ""
                                  Toast.makeText(context,"Password updated",Toast.LENGTH_LONG).show()

                              } catch (e: Exception) {
                                  e.printStackTrace()
                                  updateClicked = false
                                  Toast.makeText(context,"Failed",Toast.LENGTH_LONG).show()
                              }
                          }

                      }
                  ) {
                      Text("Update")
                  }
              } else {
                  CircularProgressIndicator()
              }
          }
      }


      SettingsOption(iconRes = R.drawable.chat, title = "Online Support") {
              // Handle click
              val url = "https://wa.me/${Constants.supportNumber}"
              val intent = Intent(Intent.ACTION_VIEW)
              intent.data = url.toUri()
              context.startActivity(intent)
          }
      }

  }

@Composable
fun PasswordInput(
    password: String,
    onPasswordChange: (String) -> Unit,
    label: String = "Password"
) {
    var passwordVisible by remember { mutableStateOf(false) }
Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = password,
        onValueChange = {
            if (it.length <= 30) onPasswordChange(it)
        },
        textStyle =   TextStyle(
            color = GradeMateColors.TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        ),
        label = {
            Text(
                text = label,
                style = TextStyle(
                    color = GradeMateColors.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (passwordVisible) R.drawable.baseline_visibility_off_24 else R.drawable.visibility
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(painter = painterResource(id = icon),
                    modifier = Modifier.size(24.dp),
                    tint = GradeMateColors.Primary,
                    contentDescription = "Toggle password visibility")
            }
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
    Spacer(modifier = Modifier.height(8.dp))

}


@Composable
fun SettingsOption(
    iconRes: Int,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            tint = GradeMateColors.Primary,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(16.dp)) // space between icon and text

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = GradeMateColors.Primary
        )
    }

    Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
}
