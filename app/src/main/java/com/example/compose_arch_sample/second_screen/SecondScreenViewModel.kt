package com.example.compose_arch_sample.second_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.compose_arch_sample.SecondScreenIntent
import com.example.compose_arch_sample.SecondScreenState
import com.example.compose_arch_sample.StateHandle
import com.example.compose_arch_sample.StateKey
import com.example.compose_arch_sample.StateMachine

class SecondScreenViewModel : ViewModel() {

    val state = StateMachine.put(StateKey.SECOND_SCREEN) {
            var buttonIndex by remember { mutableIntStateOf(0) }

            StateHandle(
                state = SecondScreenState(
                    buttonIndex = buttonIndex
                )
            ) { intent ->
                buttonIndex = (intent as SecondScreenIntent.SelectButton).buttonIndex
            }
        }

    override fun onCleared() {
        StateMachine.remove(StateKey.SECOND_SCREEN)
    }
}
