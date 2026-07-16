package com.example.pollSystem.mcp;

import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.stereotype.Component;

@Component
public class PollMcpPrompts {

    @McpPrompt(
            name = "analyze_poll_results",
            description = "Analizza i risultati di un sondaggio specifico."
    )
    public String analyzePollResults(
            @McpArg(name = "pollId", description = "L'ID del sondaggio da analizzare", required = true)
            String pollId) {

        return """
           Analizza il sondaggio con ID %s.
           Usa la risorsa 'poll://%s/details' per leggere i dettagli del sondaggio e il tool 'get_poll_votes' per ottenere il conteggio reale dei voti.
           Calcola la percentuale per ogni opzione, individua l'opzione vincente e fornisci un breve riepilogo finale in italiano.
           Se non ci sono voti, dichiaralo esplicitamente.
           """.formatted(pollId, pollId);
    }
}