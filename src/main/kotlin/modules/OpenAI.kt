package modules

import com.theokanning.openai.OpenAiService
import com.theokanning.openai.completion.CompletionRequest
import utilities.Logger

object OpenAI {

    fun complete(query: String, stops: List<String>): String {
        try {
            val service = OpenAiService(System.getenv("OPENAI_TOKEN"))
            val completionRequest: CompletionRequest =
                CompletionRequest.builder()
                    .model("text-davinci-003")
                    .prompt(query)
                    .temperature(0.5)
                    .maxTokens(4000)
                    .topP(1.0)
                    .frequencyPenalty(0.0)
                    .presencePenalty(0.6)
                    .stop(stops.distinct())
                    .build()

            val response = service.createCompletion(completionRequest)

            return response.choices.first().text.trim()
        } catch (t: Throwable) {
            Logger.error(t.message ?: "Exception")
            Logger.error(t.localizedMessage)
            Logger.error(t.stackTraceToString())
            throw t
        }
    }
}
