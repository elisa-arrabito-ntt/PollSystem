# Poll analysis guide

## Purpose
Analyze a poll using live application data only.

## Required inputs
- Poll ID

## Required sources
1. Read the MCP resource `poll://{pollId}/details`
2. Call the MCP tool `get_poll_votes`

## Procedure
1. Extract the poll title and the available options from `poll://{pollId}/details`.
2. Retrieve the live vote counts using `get_poll_votes`.
3. Compute the percentage for each option over the total number of votes.
4. Identify the option with the highest vote count.
5. If total votes are zero, explicitly state that the poll has no votes yet.

## Constraints
- Do not guess poll options.
- Do not use mock data.
- Do not use workspace files as a source of truth.
- Use only MCP resources and MCP tools provided by the server.

## Output requirements
Return:
- Poll title
- Total votes
- Votes per option
- Percentage per option
- Winning option, if any
- Explicit note when there are no votes