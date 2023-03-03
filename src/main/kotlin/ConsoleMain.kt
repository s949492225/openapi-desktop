import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val openAI = OpenAI(OpenAIConfig("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", logLevel = LogLevel.None))
    var input = ""
    while (input != "exit") {
        print("请输入问题：")
        val line = readlnOrNull()
        if (line.isNullOrBlank()) {
            continue
        }
        input = line
        val completionRequest = CompletionRequest(
            model = ModelId("text-davinci-003"),
            prompt = input,
            temperature = 0.0,
            maxTokens = 1024,
            topP = 1.0,
            frequencyPenalty = 0.0,
            presencePenalty = 0.0,
            echo = true
        )

        val completions: Flow<TextCompletion> = openAI.completions(completionRequest)

        val result = completions.map { it.choices[0].text }.reduce { a, b -> a + b }.replace(input, "")
        println(result)
        println()
    }
}