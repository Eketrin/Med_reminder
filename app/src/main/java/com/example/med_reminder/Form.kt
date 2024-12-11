package com.example.med_reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.round

//  тут описание плашки для создания нового напоминания

@Preview
@Composable // основной
fun Form(viewModel: RemindersViewModel = viewModel()) {
    val context = LocalContext.current // Получаем текущий контекст приложения


    // Основной контейнер для вертикального размещения элементов формы
    Column(
        modifier = Modifier
            .padding(10.dp) // Задаем отступы вокруг колонки
            .background(
                colorResource(id = R.color.lite_back),
                RoundedCornerShape(20.dp)
            ) // Задаем фон и скругленные углы
            .padding(top = 10.dp), // Дополнительные отступы сверху
        horizontalAlignment = Alignment.CenterHorizontally // Выравнивание содержимого по центру по горизонтали
    ) {
        // Заголовок формы
        Text(
            text = stringResource(id = R.string.form_title), // Получаем строку из ресурсов
            style = TextStyle(
                color = colorResource(id = R.color.gr_dark),
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.montserrat))
            )
        )

        // Поле для ввода текста напоминания
        ReminderTextField(viewModel)
        // Поля для выбора даты и времени
        DateTimeInputFields(viewModel)
        // Кнопка для создания напоминания
        CreateButton {
            viewModel.addReminder(context) // передали функцию для создания напоминания
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable // введите лекарство
fun ReminderTextField(viewModel: RemindersViewModel) {
    TextField(
        value = viewModel.text,
        onValueChange = { viewModel.text = it }, // Обновление значения при изменении
        label = { Text(text = stringResource(id = R.string.form_text_hint))
//            if (viewModel.text.isEmpty()) {
//                 // Показываем подсказку, если поле пустое
//            }
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent, // Прозрачный фон
            unfocusedTextColor = colorResource(id = R.color.gr_dark),
            focusedTextColor = colorResource(id = R.color.gr_dark),
            cursorColor = colorResource(id = R.color.gr_dark), // Цвет курсора
            focusedPlaceholderColor = colorResource(id = R.color.gr_dark),
            unfocusedPlaceholderColor = colorResource(id = R.color.gr_dark),
            focusedLabelColor = colorResource(id = R.color.gr_dark), // Цвет метки при фокусе
            unfocusedLabelColor = colorResource(id = R.color.gr_dark), // Цвет метки при отсутствии фокуса
            focusedIndicatorColor = Color.Transparent, // Прозрачная линия индикатора при фокусе
            unfocusedIndicatorColor = Color.Transparent, // Прозрачная линия индикатора при отсутствии фокуса
            disabledIndicatorColor = Color.Transparent // Прозрачная линия индикатора, если поле отключено
        ),

        singleLine = true, // Однострочное поле ввода
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), // Настройки клавиатуры
        modifier = Modifier.fillMaxWidth() // Заполнение ширины
    )
}

@Composable // колонка для кнопок
fun DateTimeInputFields(viewModel: RemindersViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth() // Заполнение ширины
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp), // Задаем отступы
        verticalArrangement = Arrangement.spacedBy(8.dp), // Расстояние между элементами
        horizontalAlignment = Alignment.CenterHorizontally // Выравнивание по центру
    ) {
        DateInputField(viewModel) // Поле для ввода даты
        TimeInputField(viewModel) // Поле для ввода времени
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable // выберите дату
fun DateInputField(viewModel: RemindersViewModel) {
    val context = LocalContext.current // Получаем текущий контекст приложения
    val calendar = Calendar.getInstance() // Создаем объект календаря
    val year = calendar.get(Calendar.YEAR) // Получаем текущий год
    val month = calendar.get(Calendar.MONTH) // Получаем текущий месяц
    val day = calendar.get(Calendar.DAY_OF_MONTH) // Получаем текущий день

    // Создаем диалог выбора даты
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            // Обновляем дату в ViewModel при выборе
            viewModel.date =
                "${Utils.addZero(selectedDay)}.${Utils.addZero(selectedMonth + 1)}.$selectedYear"
        },
        year, month, day // Устанавливаем начальные значения
    )

    Box { // Контейнер для наложения элементов
        TextField(
            value = viewModel.date, // Текущее значение поля даты
            onValueChange = { viewModel.date = it }, // Обновление значения при изменении
            modifier = Modifier
                .fillMaxWidth() // Заполнение ширины
                .clickable { datePickerDialog.show() }, // Открытие диалога при нажатии
            colors = TextFieldDefaults.textFieldColors(
                focusedTextColor = Color.Transparent,
                unfocusedTextColor = Color.Transparent,
                containerColor = colorResource(id = R.color.lite_orange),  // Цвет фона текстового поля
                //unfocusedIndicatorColor = Color.Transparent,
                //focusedIndicatorColor = Color.Transparent
            ),
            enabled = false,  // Отключаем редактирование поля
            label = { Text(text = stringResource(id = R.string.form_date_hint)) }
            //shape = RoundedCornerShape(15.dp)

        )
        // Отображение текста даты или подсказки
        Text(
            text = "",
//            if (viewModel.date.isNotEmpty()) viewModel.date
//            else stringResource(id = R.string.form_date_hint),
            color = colorResource(id = R.color.green), // Цвет текста
            modifier = Modifier
                .align(Alignment.CenterStart) // Выравнивание текста по началу
                .padding(start = 10.dp) // Отступ слева
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable // выберите время
fun TimeInputField(viewModel: RemindersViewModel) {
    val context = LocalContext.current // Получаем текущий контекст приложения
    val calendar = Calendar.getInstance() // Создаем объект календаря
    val hour = calendar.get(Calendar.HOUR_OF_DAY) // Получаем текущий час
    val minute = calendar.get(Calendar.MINUTE) // Получаем текущую минуту

    // Создаем диалог выбора времени
    val timePickerDialog = TimePickerDialog(
        context,
        { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
            // Обновляем время в ViewModel при выборе
            viewModel.time = "${Utils.addZero(selectedHour)}:${Utils.addZero(selectedMinute)}"
        },
        hour, minute, true // Устанавливаем начальные значения
    )

    Box { // Контейнер для наложения элементов
        TextField(
            value = viewModel.time, // Текущее значение поля времени
            onValueChange = { viewModel.time = it }, // Обновление значения при изменении
            modifier = Modifier
                .fillMaxWidth() // Заполнение ширины
                .clickable { timePickerDialog.show() }, // Открытие диалога при нажатии
            colors = TextFieldDefaults.textFieldColors(
                containerColor = colorResource(id = R.color.lite_orange) // Цвет фона текстового поля
            ),
            enabled = false, // Отключаем редактирование поля
            label = { Text(text = stringResource(id = R.string.form_time_hint)) }
        )
        // Отображение текста времени или подсказки
        Text(
            text = "",
            //text = if(viewModel.time.isNotEmpty()) viewModel.time
            // else stringResource(id = R.string.form_time_hint),
            color = colorResource(id = R.color.green), // Цвет текста
            modifier = Modifier
                .align(Alignment.CenterStart) // Выравнивание текста по началу
                .padding(start = 10.dp) // Отступ слева

        )
    }
}

@Composable
fun CreateButton(onClick: () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current // Получаем контроллер клавиатуры

    Button(
        onClick = {
            onClick() // Вызываем переданную функцию при нажатии
            keyboardController?.hide() // Скрываем клавиатуру
        },
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp) // Задаем отступы
            .fillMaxWidth() // Заполнение ширины
            .background(
                colorResource(id = R.color.grad_1),
//                brush = Brush.horizontalGradient( // Задаем градиентный фон
//                    colors = listOf(
//                        colorResource(id = R.color.grad_1), // Первый цвет градиента
//                        colorResource(id = R.color.grad_2) // Второй цвет градиента
//                    )),
                shape = RoundedCornerShape(15.dp) // Скругленные углы кнопки
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent // Прозрачный цвет фона кнопки
        )
    )
    {
        // Текст на кнопке
        Text(
            stringResource(id = R.string.form_create), // Получаем строку из ресурсов
            style = TextStyle(
                color = Color.White, // Цвет текста
                fontWeight = FontWeight.Bold // Жирный шрифт
            )
        )
    }
}





















