package com.example.compose_arch_sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.MutableSnapshot
import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


enum class StateKey {
    FIRST_SCREEN,
    SECOND_SCREEN
}

val <K, V> MutableMap<K, V>.lastKey: K
    get() = keys.last()

object StateMachine {
    //    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val appState: MutableMap<StateKey, StateHolder<out State, out Intent>> = LinkedHashMap()

    fun <S : State, I : Intent> put(key: StateKey, producer: @Composable () -> StateHandle<S, I>) =
        synchronized(this) {
            val stateHolder =
                (appState.getOrPut(key) { StateHolder<Nothing, Nothing>() }) as StateHolder<S, I>
            return@synchronized stateHolder.setStateProducer(producer)
        }

    fun remove(key: StateKey) = synchronized(this) {
        appState.remove(key = key)
    }

    fun peek() = synchronized(this) { appState[appState.lastKey] }

    fun <S : State, I : Intent> get(key: StateKey) = appState[key] as StateHolder<S, I>?
}

interface State
interface Intent


data class StateHistoryRecord<S : State, I : Intent>(val state: S, val intent: Intent)

public infix fun <S : State, I : Intent> S.to(that: I): StateHistoryRecord<S, I> =
    StateHistoryRecord(this, that)

data class StateHandle<S : State, I : Intent>(val state: S, val intentSink: (I) -> Unit)

public infix fun <S : State, I : Intent> S.to(that: (I) -> Unit): StateHandle<S, I> =
    StateHandle(this, that)

data class StateHolder<S : State, I : Intent>(
    var state: S? = null
) {
    private val _intentEmitter = Channel<I>()
    private val _stateOverrider = Channel<S>()
    private val _history = MutableStateFlow<List<StateHistoryRecord<S, I>>>(emptyList())
    private val snapshots = mutableListOf<MutableSnapshot>()

    val history: StateFlow<List<StateHistoryRecord<S, I>>> = _history
    val intentEmitter: SendChannel<I> = _intentEmitter
    val stateOverrider: SendChannel<S> = _stateOverrider
    val snapshotIndexTrigger: Channel<Int> = Channel<Int>()

    @OptIn(ExperimentalComposeApi::class)
    fun setStateProducer(producer: @Composable () -> StateHandle<S, I>)
            : @Composable () -> StateHandle<S, I> = @Composable {

        var overrideState: S? by remember { mutableStateOf(null) }
//        var snap: MutableSnapshot? by remember { mutableStateOf(null) }
        var lastIntent: I? by remember { mutableStateOf(null) }

        var (state, intentSink) = producer()

        val intentSinkDecorator = { intent: I ->
            lastIntent = intent
            intentSink(intent)
        }

        LaunchedEffect(key1 = Unit) {
            snapshotIndexTrigger.consumeEach { index ->
                val snap = snapshots.get(index)
                snap?.apply()
                snap?.dispose()
                delay(100)
            }
        }

        LaunchedEffect(key1 = Unit) {
            _stateOverrider.consumeEach { state ->
                overrideState = state
            }
        }

        LaunchedEffect(key1 = Unit) {
            _intentEmitter.consumeEach { intent ->
                intentSinkDecorator(intent)
            }
        }

        LaunchedEffect(state) {
            _history.update {
                it + (state to (lastIntent ?: return@LaunchedEffect))
            }
            snapshots += Snapshot.takeMutableSnapshot()
            this@StateHolder.state = state
            overrideState = null
        }

        (overrideState ?: state) to intentSinkDecorator
    }
}


data class FirstScreenState(
    val count: Int = 0,
    val text: String = "",
) : State

data class SecondScreenState(
    val buttonIndex: Int = 0
) : State


sealed class FirstScreenIntent : Intent {
    object Increment : FirstScreenIntent()
    object Decrement : FirstScreenIntent()
    data class SetText(val value: String) : FirstScreenIntent()
}

sealed class SecondScreenIntent : Intent {
    data class SelectButton(val buttonIndex: Int) : SecondScreenIntent()
}