package com.example.compose_arch_sample.first_screen

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.compose_arch_sample.FirstScreenIntent
import com.example.compose_arch_sample.FirstScreenState
import com.example.compose_arch_sample.StateHandle
import com.example.compose_arch_sample.StateHolder
import com.example.compose_arch_sample.StateKey
import com.example.compose_arch_sample.StateMachine
import kotlinx.coroutines.delay

class FirstScreenViewModel : ViewModel() {
    var count by mutableIntStateOf(0)
    var text by mutableStateOf("")

    val state = StateMachine.put(StateKey.FIRST_SCREEN) {
        var triggerCounter by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(key1 = triggerCounter, block = {
            if (!triggerCounter) return@LaunchedEffect
            val currentCount = count
            while (true) {
                if (count == currentCount + 10) {
                    triggerCounter = false
                    break
                }
                count += 1
                delay(1000)
            }
        })


        StateHandle(
            state = FirstScreenState(
                count = count,
                text = text
            )
        ) { intent ->
            when (intent) {
                FirstScreenIntent.Decrement -> triggerCounter = true
                FirstScreenIntent.Increment -> count++
                is FirstScreenIntent.SetText -> text = intent.value
            }
        }
    }

    override fun onCleared() {
        StateMachine.remove(StateKey.FIRST_SCREEN)
    }
}
