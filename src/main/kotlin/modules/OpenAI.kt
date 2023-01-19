package modules

import com.theokanning.openai.OpenAiService
import com.theokanning.openai.completion.CompletionRequest

object OpenAI {

    fun complete(query: String): String {
        val service = OpenAiService(System.getenv("OPENAI_TOKEN"))
        val completionRequest: CompletionRequest =
            CompletionRequest.builder()
                .model("text-davinci-003")
                .prompt(query)
                .temperature(0.9)
                .maxTokens(4000)
                .topP(1.0)
                .frequencyPenalty(0.0)
                .presencePenalty(0.0)
                .build()

        val response = service.createCompletion(completionRequest).choices.first()

        return response.text.trim()
    }
}
