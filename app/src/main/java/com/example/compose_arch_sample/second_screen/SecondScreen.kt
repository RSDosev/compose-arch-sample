package com.example.compose_arch_sample.second_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.compose_arch_sample.FirstScreenState
import com.example.compose_arch_sample.SecondScreenIntent
import com.example.compose_arch_sample.SecondScreenState
import com.example.compose_arch_sample.StateMachine
import com.example.compose_arch_sample.first_screen.FirstScreenViewModel


@Composable
fun SecondScreen(navController: NavController, viewModel: SecondScreenViewModel = viewModel()) {
    val (state, intentSink) = viewModel.state.invoke()

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RadioButton(selected = state.buttonIndex == 0, onClick = {
            intentSink(SecondScreenIntent.SelectButton(0))
        })
        RadioButton(selected = state.buttonIndex == 1, onClick = {
            intentSink(SecondScreenIntent.SelectButton(1))
        })
        RadioButton(selected = state.buttonIndex == 2, onClick = {
            intentSink(SecondScreenIntent.SelectButton(2))
        })
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            navController.popBackStack()
        }) {
            Text(text = "Goback")
        }
    }
}