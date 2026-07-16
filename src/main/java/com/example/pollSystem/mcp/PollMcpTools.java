package com.example.pollSystem.mcp;

import com.example.pollSystem.dto.response.PollDetailsResponseDto;
import com.example.pollSystem.dto.response.PollListPageResponseDto;
import com.example.pollSystem.repository.VoteRepository;
import com.example.pollSystem.service.PollService;
import org.springframework.ai.tool.annotation.Tool;
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

    @Tool(name = "search_polls", description = "Cerca sondaggi in base a un parametro di ricerca testuale (opzionale), supportando la paginazione con pagina (page, da 0) e dimensione (size).")
    public PollListPageResponseDto searchPolls(String search, int page, int size) {
        return pollService.getPolls(search, page, size);
    }

    @Tool(name = "get_poll_details", description = "Recupera tutti i dettagli di un sondaggio specifico, incluse le opzioni e le percentuali, a partire dal suo ID.")
    public PollDetailsResponseDto getPollDetails(Long pollId) {
        return pollService.getPollDetails(pollId);
    }

    public record OptionVoteResult(Long optionId, String optionMessage, long voteCount) {}

    @Tool(name = "get_poll_votes", description = "Restituisce il conteggio reale dei voti per tutte le opzioni di un sondaggio. Usalo sempre prima di fare analisi statistiche su un sondaggio.")
    public List<OptionVoteResult> getPollVotes(Long pollId) {
        PollDetailsResponseDto details = pollService.getPollDetails(pollId);
        return details.getOptions().stream()
                .map(opt -> new OptionVoteResult(opt.getId(), opt.getMessage(), voteRepository.countByOptionId(opt.getId())))
                .toList();
    }
}
