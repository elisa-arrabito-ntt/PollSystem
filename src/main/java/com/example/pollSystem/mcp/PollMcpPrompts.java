package com.example.pollSystem.mcp;

import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.stereotype.Component;

@Component
public class PollMcpPrompts {

    @McpPrompt(
            name = "analyze_poll_results",
            description = "Analyze a poll using direct MCP tools for poll details and live vote counts."
    )
    public String analyzePollResults(
            @McpArg(name = "pollId", description = "The unique database ID of the target poll to analyze", required = true)
            String pollId) {

        return """
            Analyze poll %s.

            Required workflow:
            1. Use the MCP tool `get_poll_details` with pollId `%s` to retrieve the poll title, status, and available options.
            2. Use the MCP tool `get_poll_votes` with pollId `%s` to retrieve the live vote counts.
            3. Base the answer only on the returned tool outputs.
            4. Do not infer or guess poll options from workspace files, source code, or mock data.
            5. Calculate the percentage for each option and identify the winning option.
            6. If the poll has no votes, state it explicitly.

            Output format:
            - Poll title
            - Poll status
            - Total votes
            - Results by option with vote count and percentage
            - Winning option
            - Explicit note when there are no votes
            """.formatted(pollId, pollId, pollId);
    }

    @McpPrompt(
            name = "analyze_poll_results_with_guide",
            description = "Analyze a poll using attached MCP guide and poll detail resources plus live vote data."
    )
    public String analyzePollResultsWithGuide(
            @McpArg(name = "pollId", description = "The unique database ID of the target poll to analyze", required = true)
            String pollId) {

        return """
            Analyze poll %s.

            Expected context:
            - The MCP resource `poll://analysis-guide` is attached to this conversation.
            - The MCP resource `poll://%s/details` is attached to this conversation.

            Required workflow:
            1. Read the attached analysis guide resource.
            2. Read the attached poll details resource to obtain the poll structure and available options.
            3. Use the MCP tool `get_poll_votes` with pollId `%s` to retrieve the live vote counts.
            4. Base the answer only on the attached MCP resources and the tool output.
            5. Do not infer or guess poll options from workspace files, source code, or mock data.
            6. Calculate the percentage for each option and identify the winning option.
            7. If the poll has no votes, state it explicitly.

            Output format:
            - Poll title
            - Poll status
            - Total votes
            - Results by option with vote count and percentage
            - Winning option
            - Explicit note when there are no votes
            """.formatted(pollId, pollId, pollId);
    }
}