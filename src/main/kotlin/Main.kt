import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


data class Message(val from: String, val message: String)

const val API_KEY = "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"
fun main() = application {
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var chatList by remember { mutableStateOf(listOf<Message>()) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val openAI by lazy { OpenAI(API_KEY) }

    fun requestGpt(prompt: String): Flow<TextCompletion> {
        val completionRequest = CompletionRequest(
            model = ModelId("text-davinci-003"),
            prompt = prompt,
            temperature = 0.0,
            maxTokens = 1024,
            topP = 1.0,
            frequencyPenalty = 0.0,
            presencePenalty = 0.0,
            echo = true
        )
        return openAI.completions(completionRequest)
    }

    fun onSend() {
        if (loading) {
            return
        }
        if (input.trim().isBlank()) {
            return
        }
        chatList += Message("我", input)
        chatList += Message("gpt", "回复中,请等待")
        val prompt = input
        input = ""
        loading = true
        coroutineScope.launch {
            listState.animateScrollToItem(chatList.size - 1)
        }
        requestGpt(prompt)
            .flowOn(Dispatchers.IO)
            .onEach {
                val msg = chatList.last()
                val text = (msg.message + it.choices[0].text).replace("回复中,请等待", "")
                chatList = chatList.modifyLast(text)
                println(text)
            }
            .catch {
                chatList = chatList.modifyLast("网络错误")
                loading = false
            }
            .onCompletion {
                loading = false
            }
            .launchIn(coroutineScope)
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "ChatGpt Desktop",
        state = rememberWindowState(width = 800.dp, height = 600.dp)
    ) {
        MaterialTheme {
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
                ChatList(listState, chatList)
                EditBox(input, loading, onChange = { input = it }) { onSend() }
            }
        }
    }

}

