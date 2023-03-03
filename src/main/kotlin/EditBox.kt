import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EditBox(input: String, loading: Boolean, onChange: (String) -> Unit, onSend: () -> Unit) {
    Row(modifier = Modifier.padding(16.dp).height(55.dp), verticalAlignment = Alignment.CenterVertically) {
        TextField(
            input,
            modifier = Modifier.weight(1.0f).fillMaxHeight().padding(end = 16.dp),
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
                onChange(it)
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