import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.resourcesFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


data class Message(val from: String, val message: String)

val desktopConfig = KamelConfig {
    takeFrom(KamelConfig.Default)
    // Available only on Desktop.
    resourcesFetcher()
}

@OptIn(BetaOpenAI::class)
fun main() = application {
    var selectModel by remember { mutableStateOf("文本") }
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var chatList by remember { mutableStateOf(listOf<Message>()) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val openAI by lazy { OpenAI(API_KEY) }

    fun requestGptText(prompt: String): Flow<TextCompletion> {
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

    fun requestGptImage(prompt: String) = flow {
        val url = openAI.imageURL(
            creation = ImageCreation(
                prompt = prompt,
                n = 1,
                size = ImageSize.is512x512
            )
        )[0].url
//        emit("https://img0.baidu.com/it/u=2900833435,993445529&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500")
        emit(url)
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
            delay(32)
            listState.animateScrollToItem(chatList.size - 1)
        }
        if (selectModel == "文本") {
            requestGptText(prompt)
                .flowOn(Dispatchers.IO)
                .onEach {
                    val msg = chatList.last()
                    val text = (msg.message + it.choices[0].text).replace("回复中,请等待", "")
                    chatList = chatList.modifyLast(text)
                    println(text)
                }
                .catch {
                    chatList = chatList.modifyLast("网络错误")
                }
                .onCompletion {
                    loading = false
                    delay(32)
                    listState.animateScrollToItem(chatList.size - 1)
                }
                .launchIn(coroutineScope)
        } else {
            requestGptImage(prompt)
                .flowOn(Dispatchers.IO)
                .onEach {
                    chatList = chatList.modifyLast(it)
                    println(it)
                }
                .catch {
                    chatList = chatList.modifyLast("网络错误")
                }
                .onCompletion {
                    loading = false
                    delay(32)
                    listState.animateScrollToItem(chatList.size - 1)
                }
                .launchIn(coroutineScope)
        }
    }

    CompositionLocalProvider(LocalKamelConfig provides desktopConfig) {
        Window(
            onCloseRequest = ::exitApplication,
            title = "ChatGpt Desktop",
            state = rememberWindowState(width = 800.dp, height = 600.dp)
        ) {
            MaterialTheme {
                Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
                    ChatList(listState, chatList)
                    EditBox(
                        selectModel,
                        input,
                        loading,
                        onModelChange = { selectModel = it },
                        onTextChange = { input = it }) { onSend() }
                }
            }
        }
    }
}

