package com.example.med_reminder

import android.app.AlertDialog
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

// тут достаём все наши напоминания и выводим их в колонку
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun List(viewModel: RemindersViewModel = viewModel()) {
    val context = LocalContext.current
    var shouldReload by remember { mutableStateOf(true) }

    // Если нужно перезагрузить, вызываем getReminders
    LaunchedEffect(shouldReload) {
        if (shouldReload) {
            viewModel.getReminders(context)
            shouldReload = false // Сброс состояния после загрузки
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(viewModel.reminders, key = { it.id }) { reminder -> // Используем уникальный key
            ReminderItem(reminder, viewModel) {
                // При удалении напоминания, устанавливаем shouldReload в true
                shouldReload = true
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReminderItem(reminder: Reminder, viewModel: RemindersViewModel, onDelete: () -> Unit) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val currentDate = LocalDate.now().format(formatter)

    var backgroundColor = Color.White
    var textPillColor = Color.White
    if (reminder.date == currentDate) {
        backgroundColor = colorResource(id = R.color.gr_dark) // сегодня
        textPillColor = colorResource(id = R.color.lite_orange)
    } else {
        backgroundColor = colorResource(id = R.color.dark_green_list) // не сегодня
        textPillColor = colorResource(id = R.color.lite_green_list)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            .background(backgroundColor, RoundedCornerShape(25.dp))
            .padding(start = 10.dp, end = 5.dp, top = 5.dp, bottom = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            // Выводим сообщение с вопросом перед удалением напоминания
                            val alertDialog = AlertDialog
                                .Builder(context)
                                .setTitle("Удаление напоминания")
                                .setMessage("Вы точно хотите удалить ${reminder.text}?")
                                .setPositiveButton("Да") { _, _ ->
                                    // Удаление напоминания при подтверждении
                                    viewModel.removeReminder(reminder, context)
                                    // Вызываем onDelete для перезагрузки списка
                                    onDelete()
                                }
                                .setNegativeButton("Нет", null)
                                .create()
                            alertDialog.show()
                        }
                    )
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.50f)
                    .padding(start = 10.dp)
            ) {
                Text(
                    text = reminder.text,
                    style = TextStyle(color = Color.White, fontSize = 18.sp),
                    modifier = Modifier.fillMaxWidth(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${stringResource(id = R.string.list_dose)}: ${reminder.dose} ${reminder.piece}",
                    style = TextStyle(color = textPillColor, fontSize = 16.sp),
                    modifier = Modifier.padding(end = 7.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = reminder.date,
                    style = TextStyle(color = textPillColor, fontSize = 16.sp),
                    modifier = Modifier.padding(start = 7.dp)
                )
                Text(
                    text = reminder.time,
                    style = TextStyle(color = textPillColor, fontSize = 16.sp),
                    modifier = Modifier.padding(start = 7.dp)
                )
                Checkbox(
                    checked = reminder.taked,
                    onCheckedChange = { isChecked ->
                        viewModel.updateReminderTaked(reminder.copy(taked = isChecked), context)
                    },
                    colors = CheckboxDefaults.colors(checkedColor = colorResource(id = R.color.green))
                )
            }
        }
    }
}
