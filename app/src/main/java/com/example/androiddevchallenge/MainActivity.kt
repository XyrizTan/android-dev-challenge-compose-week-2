/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {


        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Sai's Jetpack Compose Countdown Timer :D", modifier = Modifier.padding(24.dp))
            val countDownSecondsValue = remember { mutableStateOf(0) }
            val isTimerRunning = remember { mutableStateOf(false) }
            val textFieldEditable = derivedStateOf { !isTimerRunning.value }
            val textFieldValue = remember { mutableStateOf("00h00m00s") }
            val textFieldInput = remember { mutableStateOf("") }
            TextField(
                modifier = Modifier.padding(24.dp),
                enabled = textFieldEditable.value,
                value = textFieldValue.value,
                onValueChange = { text ->
                    // backspace registered
                    if (text.length < 9) {
                        if (textFieldInput.value.isNotEmpty()) {
                            textFieldInput.value =
                                textFieldInput.value.substring(0, textFieldInput.value.length - 1)
                        }
                        // shift digits to the right
                    } else {
                        textFieldInput.value = textFieldInput.value + text.last()
                        // shift digits to the left only until if the first digit is zero
                    }
                    val rawString = String.format(
                        "%06d", try {
                            textFieldInput.value.toInt()
                        } catch (e: NumberFormatException) {
                            0
                        }
                    )
                    val sb = StringBuilder(rawString)
                    sb.insert(2, 'h')
                    sb.insert(5, 'm')
                    sb.insert(8, 's')
                    val formattedString = sb.toString()
                    textFieldValue.value = formattedString

                    val nums = formattedString.split("[hms]".toRegex())
                    val hours = nums[0].toInt()
                    val minutes = nums[1].toInt()
                    val seconds = nums[2].toInt()
                    countDownSecondsValue.value = hours * 3600 + minutes * 60 + seconds
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Row {
                val startButtonToggleText = remember { mutableStateOf("Start") }
                var timer = remember { mutableStateOf<CountDownTimer?>(null) }
                Button(
                    modifier = Modifier.padding(16.dp),
                    onClick = {
                        if (!isTimerRunning.value) {
                            timer.value =
                                object :
                                    CountDownTimer(countDownSecondsValue.value * 1000L, 1000L) {
                                    override fun onTick(millisUntilFinished: Long) {
                                        countDownSecondsValue.value--
                                        textFieldValue.value =
                                            countDownSecondsValue.value.toCountDownFormat()
                                    }

                                    override fun onFinish() {
                                        isTimerRunning.value = false
                                    }
                                }.start()
                            isTimerRunning.value = true
                        } else {
                            timer.value?.cancel()
                            timer.value = null
                            isTimerRunning.value = false
                        }
                    }) {
                    Text(text = if (!isTimerRunning.value) "Start" else "Stop")
                }
                Button(modifier = Modifier.padding(16.dp), onClick = {
                    timer.value?.cancel()
                    timer.value = null
                    isTimerRunning.value = false

                    countDownSecondsValue.value = 0
                    textFieldValue.value = "00h00m00s"
                    textFieldInput.value = ""
                }) {
                    Text(text = "Reset")
                }
            }
        }
    }
}

fun Int.toCountDownFormat(): String {
    val hours = this / (60 * 60)
    val minutes = this / 60 % 60
    val seconds = this % 60

    return "${String.format("%02d", hours)}h${
        String.format(
            "%02d",
            minutes
        )
    }m${String.format("%02d", seconds)}s"
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
