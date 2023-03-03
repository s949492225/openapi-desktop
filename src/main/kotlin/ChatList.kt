import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource

@Composable
fun ColumnScope.ChatList(
    listState: LazyListState,
    chatList: List<Message>
) {
    Box(modifier = Modifier.weight(1.0f)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(chatList.size) {
                val message = chatList[it]
                val horizontal = if (message.from != "我") Arrangement.Start else Arrangement.End
                Row(horizontalArrangement = horizontal, modifier = Modifier.fillMaxWidth()) {
                    if (message.from == "我") {
                        Box(
                            modifier = Modifier.size(50.dp)
                                .background(Color.Green, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(message.from)
                        }
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(message.message, modifier = Modifier.weight(1f).padding(top = 10.dp, end = 50.dp))
                    } else {
                        if (!message.message.startsWith("http")) {
                            Text(
                                message.message,
                                modifier = Modifier.weight(1f).padding(top = 10.dp, start = 50.dp),
                                textAlign = TextAlign.End
                            )
                        } else {
                            Row(
                                modifier = Modifier.weight(1f).padding(top = 10.dp, start = 50.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                KamelImage(
                                    resource = lazyPainterResource(data = message.message),
                                    modifier = Modifier.size(150.dp, 150.dp),
                                    contentDescription = "Image",
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(16.dp))
                        Box(
                            modifier = Modifier.size(50.dp)
                                .background(Color.Green, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(message.from)
                        }
                    }
                }
            }
        }
    }
}