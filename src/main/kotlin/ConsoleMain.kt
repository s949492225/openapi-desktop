import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

@OptIn(BetaOpenAI::class)
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

        val images = openAI.imageURL( // or openAI.imageJSON
            creation = ImageCreation(
                prompt = line,
                n = 2,
                size = ImageSize.is1024x1024
            )
        )

        println(images.map { it.url })
//
//        val completionRequest = CompletionRequest(
//            model = ModelId("text-davinci-003"),
//            prompt = input,
//            temperature = 0.0,
//            maxTokens = 1024,
//            topP = 1.0,
//            frequencyPenalty = 0.0,
//            presencePenalty = 0.0,
//            echo = true
//        )
//
//        val completions: Flow<TextCompletion> = openAI.completions(completionRequest)
//
//        val result = completions.map { it.choices[0].text }.reduce { a, b -> a + b }.replace(input, "")
//        println(result)
        println()
    }
}