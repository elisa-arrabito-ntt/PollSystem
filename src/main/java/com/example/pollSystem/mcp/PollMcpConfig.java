package com.example.pollSystem.mcp;

import com.example.pollSystem.dto.response.PollDetailsResponseDto;
import com.example.pollSystem.dto.response.PollResponseDto;
import com.example.pollSystem.service.PollService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.modelcontextprotocol.server.McpStatelessServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class PollMcpConfig {

    private final PollService pollService;
    private final ObjectMapper objectMapper;

    public PollMcpConfig(PollService pollService) {
        this.pollService = pollService;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    // ==========================================
    // 1. RESOURCES (Risorse in sola lettura)
    // ==========================================
    @Bean
    public List<McpStatelessServerFeatures.SyncResourceTemplateSpecification> pollResources() {
        var pollTemplate = new McpSchema.ResourceTemplate(
                "poll://{id}", "Poll basic details", "Basic info about a poll", "application/json", null);

        var pollTemplateSpec = new McpStatelessServerFeatures.SyncResourceTemplateSpecification(pollTemplate, (context, request) -> {
            try {
                String uri = request.uri();
                Long id = Long.parseLong(uri.replace("poll://", ""));
                PollResponseDto poll = pollService.getPollById(id);
                String json = objectMapper.writeValueAsString(poll);
                return new McpSchema.ReadResourceResult(
                        List.of(new McpSchema.TextResourceContents(uri, "application/json", json)));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load poll", e);
            }
        });

        var pollDetailsTemplate = new McpSchema.ResourceTemplate(
                "poll://{id}/details", "Poll full details", "Full details including options for a poll", "application/json", null);

        var pollDetailsTemplateSpec = new McpStatelessServerFeatures.SyncResourceTemplateSpecification(pollDetailsTemplate, (context, request) -> {
            try {
                String uri = request.uri();
                Long id = Long.parseLong(uri.replace("poll://", "").replace("/details", ""));
                PollDetailsResponseDto poll = pollService.getPollDetails(id);
                String json = objectMapper.writeValueAsString(poll);
                return new McpSchema.ReadResourceResult(
                        List.of(new McpSchema.TextResourceContents(uri, "application/json", json)));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load poll details", e);
            }
        });

        return List.of(pollTemplateSpec, pollDetailsTemplateSpec);
    }

    // ==========================================
    // 2. PROMPTS (Modelli di prompt per l'IA)
    // ==========================================
    @Bean
    public List<McpStatelessServerFeatures.SyncPromptSpecification> pollPrompts() {
        var prompt = new McpSchema.Prompt(
                "analyze_poll_results",
                "Genera un'analisi dettagliata e le percentuali per un sondaggio specifico.",
                List.of(new McpSchema.PromptArgument("pollId", "L'ID del sondaggio da analizzare", true))
        );

        var promptSpecification = new McpStatelessServerFeatures.SyncPromptSpecification(prompt, (context, request) -> {
            Map<String, Object> arguments = request.arguments();
            String pollIdArg = arguments != null && arguments.containsKey("pollId") ? arguments.get("pollId").toString() : "0";
            Long pollId = Long.parseLong(pollIdArg);

            String promptText = String.format(
                "Per favore analizza i risultati del sondaggio con ID %d. " +
                "Includi nel contesto la risorsa 'poll://%d/details' per leggere i dati. " +
                "Calcola le percentuali per ogni opzione, evidenzia l'opzione vincente e fornisci un breve riassunto dell'esito del sondaggio.",
                pollId, pollId
            );

            var userMessage = new McpSchema.PromptMessage(
                    McpSchema.Role.USER,
                    new McpSchema.TextContent(promptText)
            );

            return new McpSchema.GetPromptResult("Analisi sondaggio", List.of(userMessage));
        });

        return List.of(promptSpecification);
    }

    // ==========================================
    // 3. TOOLS (Strumenti eseguibili)
    // ==========================================
    @Bean
    public ToolCallbackProvider pollToolsProvider(PollMcpTools pollMcpTools) {
        return MethodToolCallbackProvider.builder().toolObjects(pollMcpTools).build();
    }
}
