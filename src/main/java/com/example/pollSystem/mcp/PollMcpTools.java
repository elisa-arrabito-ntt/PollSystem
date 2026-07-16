package com.example.pollSystem.mcp;

import com.example.pollSystem.dto.response.PollDetailsResponseDto;
import com.example.pollSystem.dto.response.PollListPageResponseDto;
import com.example.pollSystem.repository.VoteRepository;
import com.example.pollSystem.service.PollService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PollMcpTools {

    private final PollService pollService;
    private final VoteRepository voteRepository;

    public PollMcpTools(PollService pollService, VoteRepository voteRepository) {
        this.pollService = pollService;
        this.voteRepository = voteRepository;
    }

    @McpTool(
            name = "search_polls",
            description = "Cerca sondaggi in base a un parametro di ricerca testuale opzionale, supportando la paginazione con page da 0 e size."
    )
    public PollListPageResponseDto searchPolls(
            @McpToolParam(description = "Testo di ricerca opzionale") String search,
            @McpToolParam(description = "Numero di pagina, a partire da 0", required = true) int page,
            @McpToolParam(description = "Dimensione della pagina", required = true) int size) {
        return pollService.getPolls(search, page, size);
    }

    public record OptionVoteResult(Long optionId, String optionMessage, long voteCount) {}

    @McpTool(
            name = "get_poll_votes",
            description = "Restituisce il conteggio reale dei voti per tutte le opzioni di un sondaggio. Usalo prima di fare analisi statistiche su un sondaggio."
    )
    public List<OptionVoteResult> getPollVotes(
            @McpToolParam(description = "ID del sondaggio", required = true) Long pollId) {
        PollDetailsResponseDto details = pollService.getPollDetails(pollId);
        return details.getOptions().stream()
                .map(opt -> new OptionVoteResult(
                        opt.getId(),
                        opt.getMessage(),
                        voteRepository.countByOptionId(opt.getId())))
                .toList();
    }
}