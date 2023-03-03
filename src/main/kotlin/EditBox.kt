import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EditBox(
    selectModel: String,
    input: String,
    loading: Boolean,
    onModelChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp).wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var expanded by remember { mutableStateOf(false) }
        val items = listOf("文本", "图片")
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEachIndexed { _, s ->
                DropdownMenuItem(onClick = {
                    onModelChange(s)
                    expanded = false
                }) {
                    Text(text = s, color = Color.Black)
                }
            }
        }
        Button(
            modifier = Modifier.height(45.dp).width(80.dp).padding(end = 16.dp),
            onClick = { expanded = true }
        )
        {
            Text(selectModel)
        }
        TextField(
            input,
            modifier = Modifier.weight(1.0f).wrapContentHeight().padding(end = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Gray,
                disabledTextColor = Color.Transparent,
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = { Text("请输入") },
            shape = RoundedCornerShape(12),
            onValueChange = {
                onTextChange(it)
            },
        )
        Button(
            modifier = Modifier.height(45.dp).width(80.dp),
            onClick = {
                onSend()
            }) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(30.dp), color = Color.White)
            } else {
                Text("输入")
            }
        }
    }
}