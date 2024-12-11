package com.example.med_reminder

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

//   тут описывается как будет выглядеть каждый элемент в списке элементов

@Composable
fun ReminderItem(reminder: Reminder, viewModel: RemindersViewModel) {
    val context = LocalContext.current
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(10.dp),
//        shape = RoundedCornerShape(15.dp),
//        elevation = 5.dp
//    ) {}
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            .background(colorResource(id = R.color.gr_dark), RoundedCornerShape(25.dp))
            .border(0.5.dp, colorResource(id = R.color.green), RoundedCornerShape(25.dp))
            .padding(start = 10.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
            .clickable {
                Toast
                    .makeText(context, reminder.text, Toast.LENGTH_LONG)
                    .show()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = reminder.text,
            style = TextStyle(color = Color.White),
            modifier = Modifier.fillMaxWidth(0.50f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = reminder.date,
                style = TextStyle(color = colorResource(id = R.color.green)),
                modifier = Modifier.padding(start = 7.dp)
            )
            Text(
                text = reminder.time,
                style = TextStyle(color = colorResource(id = R.color.green)),
                modifier = Modifier.padding(start = 7.dp)
            )


//            Checkbox(
//                checked = isChecked,
//                onCheckedChange = { isChecked = it }, // Обработка изменения состояния чекбокса
//                modifier = Modifier.padding(start = 7.dp)
//            )

            Image(
                painter = painterResource(id = R.drawable.delete),
                contentDescription = "Delete",
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 7.dp)
                    .clip(CircleShape)
                    .clickable {
                        viewModel.removeReminder(reminder, context)
                    }
            )
        }

    }
}