package com.example.compose_arch_sample.first_screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.compose_arch_sample.FirstScreenIntent
import com.example.compose_arch_sample.FirstScreenState
import com.example.compose_arch_sample.StateKey
import com.example.compose_arch_sample.StateMachine
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


@Composable
fun FirstScreen(navController: NavController, viewModel: FirstScreenViewModel = viewModel()) {
    val (state, intentSink) = viewModel.state()
    val scope = rememberCoroutineScope()
    val stateHistory by StateMachine.get<FirstScreenState, FirstScreenIntent>(StateKey.FIRST_SCREEN)?.history!!.collectAsState()

    LaunchedEffect(key1 = Unit, block = {
        StateMachine.peek()?.history?.collect {
            Log.d("DASd", it.toString())
        }
    })

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
//            navController.navigate("secondscreen")
            scope.launch {
                StateMachine.get<FirstScreenState, FirstScreenIntent>(StateKey.FIRST_SCREEN)?.snapshotIndexTrigger?.send(2)
            }
        }) {
            Text("Go to next screen")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Count: ${state.count}", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = {
            intentSink(FirstScreenIntent.Increment)
        }) {
            Text(text = "INCREMENT")
        }
        Button(onClick = {
            intentSink(FirstScreenIntent.Decrement)
        }) {
            Text(text = "DECREMENT")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Text: ${state.text}", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = state.text, onValueChange = {
            intentSink(FirstScreenIntent.SetText(it))
        })


        LazyColumn(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .height(150.dp)
                .fillMaxWidth(),
            content = {
                items(stateHistory) {
                    Text(text = "${it.intent.javaClass.simpleName} -> ${it.state}")
                }
            })
    }
}