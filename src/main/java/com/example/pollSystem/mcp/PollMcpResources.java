package com.example.pollSystem.mcp;

import com.example.pollSystem.service.PollService;
import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class PollMcpResources {

    private final PollService pollService;
    private final ObjectMapper objectMapper;

    public PollMcpResources(PollService pollService, ObjectMapper objectMapper) {
        this.pollService = pollService;
        this.objectMapper = objectMapper;
    }

    @McpResource(
            uri = "poll://{id}",
            name = "poll-basic-details",
            title = "Poll basic details",
            description = "Basic info about a poll",
            mimeType = "application/json"
    )
    public String pollById(@McpArg(name = "id") String id) throws Exception {
        Long pollId = Long.parseLong(id);
        return objectMapper.writeValueAsString(pollService.getPollById(pollId));
    }

    @McpResource(
            uri = "poll://{id}/details",
            name = "poll-full-details",
            title = "Poll full details",
            description = "Full details including options for a poll",
            mimeType = "application/json"
    )
    public String pollDetails(@McpArg(name = "id") String id) throws Exception {
        Long pollId = Long.parseLong(id);
        return objectMapper.writeValueAsString(pollService.getPollDetails(pollId));
    }
}