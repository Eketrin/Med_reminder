package com.example.med_reminder

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.med_reminder.ui.theme.Med_reminderTheme
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean -> if(!isGranted) {
        Toast.makeText(this, R.string.permission_warning, Toast.LENGTH_LONG).show()
    }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        window.statusBarColor = Color.BLACK

        // Получаем доступ к AlarmManager, который позволяет управлять будильниками и запланированными задачами.
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Проверяем, если версия Android >= Tiramisu (API 33) и уведомления не включены для приложения.
        // Если это так, запрашиваем разрешение на отправку уведомлений.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && !NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        // Проверяем, если версия Android >= S (API 31) и приложение не имеет разрешения на планирование точных будильников.
        // Если это так, запускаем активность, которая позволяет пользователю предоставить это разрешение.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && !alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
        }


        setContent {

            val systemUiController: SystemUiController = rememberSystemUiController()
            // Скрываем системные панели (панель состояния и панель навигации)
            systemUiController.isSystemBarsVisible = false
            systemUiController.isNavigationBarVisible = false

            // Инициализируем RemindersViewModel с помощью делегата viewModel()
            val viewModel: RemindersViewModel = viewModel()

            // Инициализируем DatabaseHelper и alarmManager для viewModel
            viewModel.dbHelper = DatabaseHelper(LocalContext.current)
            viewModel.alarmManager = alarmManager

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf( // градиент на заднем плане
                                colorResource(id = R.color.lite_orange),
                                colorResource(id = R.color.orange)
                            )
                        )
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // выводим плашку создания нпоминания и список напоминаний
                AppTitle()
                Form(viewModel)
                List(viewModel)
            }
        }
    }
}
@Preview
@Composable
fun AppTitle() {
    Text(
        text = stringResource(id = R.string.app_title),
        style = TextStyle(
            color = colorResource(id = R.color.gr_dark),
            fontSize = 26.sp,
            fontFamily = FontFamily(Font(R.font.montserrat))
        )
    )
}












