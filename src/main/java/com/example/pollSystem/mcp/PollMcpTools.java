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
            description = "Query the live application database to search for active polls using an optional text filter. Supports pagination."
    )
    public PollListPageResponseDto searchPolls(
            @McpToolParam(description = "Optional text search query to filter poll titles", required = false) String search,
            @McpToolParam(description = "The page number to retrieve, starting from 0", required = true) int page,
            @McpToolParam(description = "The number of items to return per page (e.g., 10)", required = true) int size) {
        return pollService.getPolls(search, page, size);
    }



    @McpTool(
            name = "get_poll_details",
            description = "Retrieve the full structure of a specific poll by its database ID, including title, metadata, and available options."
    )
    public PollDetailsResponseDto getPollDetails(
            @McpToolParam(description = "The unique database ID of the poll", required = true) Long pollId) {
        return pollService.getPollDetails(pollId);
    }



    public record OptionVoteResult(Long optionId, String optionMessage, long voteCount) {}

    @McpTool(
            name = "get_poll_votes",
            description = "Retrieve the live real-time vote results and counts for all options within a specific poll. Use this before performing statistical analysis."
    )
    public List<OptionVoteResult> getPollVotes(
            @McpToolParam(description = "The unique database ID of the poll", required = true) Long pollId) {
        PollDetailsResponseDto details = pollService.getPollDetails(pollId);
        return details.getOptions().stream()
                .map(opt -> new OptionVoteResult(
                        opt.getId(),
                        opt.getMessage(),
                        voteRepository.countByOptionId(opt.getId())))
                .toList();
    }
}