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
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.resourcesFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


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

    val openAI by lazy { OpenAI(OpenAIConfig(token = API_KEY, timeout = Timeout(socket = 10.seconds))) }

    fun requestGptText(prompt: String) =
        openAI.chatCompletions(
            ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo"),
                messages = listOf(
                    ChatMessage(
                        role = ChatRole.User,
                        content = prompt
                    )
                )
            )
        )

    fun requestGptImage(prompt: String) = flow {
        emit(
            openAI.imageURL(
                creation = ImageCreation(
                    prompt = prompt,
                    n = 1,
                    size = ImageSize.is512x512
                )
            )[0].url
        )
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
                    val text = (msg.message + (it.choices[0].delta?.content ?: ""))
                        .replace("回复中,请等待", "")
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

