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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
@Preview
@Composable
fun Form(viewModel: RemindersViewModel = viewModel()) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(10.dp)
            .background(colorResource(id = R.color.lite_orange), RoundedCornerShape(15.dp))
            .border(0.5.dp, colorResource(id = R.color.lite_orange), RoundedCornerShape(15.dp))
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.form_title),
            style = TextStyle(
                color = colorResource(id = R.color.white),
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace
            )
        )

        ReminderTextField(viewModel)
        DateTimeInputFields(viewModel)
        CreateButton {
            viewModel.addReminder(context)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderTextField(viewModel: RemindersViewModel) {
    TextField(
        value = viewModel.text,
        onValueChange = { viewModel.text = it },
        label = { Text(text = stringResource(id = R.string.form_text_hint)) },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
//            textColor = colorResource(id = R.color.green),
            cursorColor = colorResource(id = R.color.green),
//            placeholderColor = colorResource(id = R.color.green),
            focusedLabelColor = colorResource(id = R.color.green),
            unfocusedLabelColor = colorResource(id = R.color.green),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = Modifier.fillMaxWidth()
    )

}

@Composable
fun DateTimeInputFields(viewModel: RemindersViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DateInputField(viewModel)
        TimeInputField(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInputField(viewModel: RemindersViewModel) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val datePickerDialog = DatePickerDialog(
        context,
        {
                _: DatePicker,
                selectedYear: Int,
                selectedMonth: Int,
                selectedDay: Int -> viewModel.date = "${Utils.addZero(selectedDay)}.${Utils.addZero(selectedMonth + 1)}.$selectedYear"},
        year, month, day
    )

    Box {
        TextField(
            value = viewModel.date,
            onValueChange = { viewModel.date = it },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = colorResource(id = R.color.orange)
            ),
            enabled = false
        )
        Text(
            text = if(viewModel.date.isNotEmpty()) viewModel.date else stringResource(id = R.string.form_date_hint),
            color = colorResource(id = R.color.green),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeInputField(viewModel: RemindersViewModel) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val timePickerDialog = TimePickerDialog(
        context,
        {
                _: TimePicker,
                selectedHour: Int,
                selectedMinute: Int -> viewModel.time = "${Utils.addZero(selectedHour)}:${Utils.addZero(selectedMinute)}"},
        hour, minute, true
    )

    Box {
        TextField(
            value = viewModel.time,
            onValueChange = { viewModel.time = it },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { timePickerDialog.show() },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = colorResource(id = R.color.orange)
            ),
            enabled = false
        )
        Text(
            text = if(viewModel.time.isNotEmpty()) viewModel.time else stringResource(id = R.string.form_time_hint),
            color = colorResource(id = R.color.green),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp)
        )
    }
}


@Composable
fun CreateButton(onClick: () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Button(
        onClick = {
            onClick()
            keyboardController?.hide()
        },
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        colorResource(id = R.color.grad_1),
                        colorResource(id = R.color.grad_2)
                    )
                ),
                shape = RoundedCornerShape(15.dp)
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            )
        )
    {
        Text(
            stringResource(id = R.string.form_create),
            style = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}





















