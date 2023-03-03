import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            modifier = Modifier.fillMaxSize().padding(16.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(chatList.size) {
                val message = chatList[it]
                val horizontal = if (message.from != "我") Arrangement.Start else Arrangement.End
                Row(
                    horizontalArrangement = horizontal,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (message.from == "我") {
                        Row(modifier = Modifier.padding(start = 80.dp)) {
                            Text(message.message, modifier = Modifier.padding(top = 10.dp))
                            Spacer(modifier = Modifier.size(16.dp))
                            Avatar(message)
                        }
                    } else {
                        Row(modifier = Modifier.padding(end = 80.dp)) {
                            Avatar(message)
                            Spacer(modifier = Modifier.size(16.dp))
                            if (!message.message.startsWith("http")) {
                                Text(message.message, modifier = Modifier.padding(top = 10.dp))
                            } else {
                                ChatImage(message)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatImage(message: Message) {
    KamelImage(
        resource = lazyPainterResource(data = message.message),
        modifier = Modifier.size(150.dp, 150.dp),
        contentDescription = "Image",
        onLoading = {
            Box(
                modifier = Modifier.background(Color.LightGray).size(150.dp, 150.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp),
                    color = Color.White
                )
            }
        },
        onFailure = {
            Box(
                modifier = Modifier.background(Color.LightGray).size(150.dp, 150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "加载失败",
                    color = Color.Red
                )
            }
        }
    )
}

@Composable
fun Avatar(message: Message) {
    Box(
        modifier = Modifier.size(50.dp)
            .background(Color.Green, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(message.from)
    }
}