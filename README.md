# Weather MCP Server

This project is a Java implementation of a Model Context Protocol (MCP) server that provides weather information tools. It's built using Spring Boot and Spring AI, following the implementation guide from [MCP Server Java Quickstart](https://modelcontextprotocol.io/quickstart/server#java).

In addition to the original weather endpoints, the server now exposes a tool for reading the official [Blossom](https://github.com/fiatjaf/blossom) (Nostr blob storage) documentation directly from its GitHub repository. You can request the README or any BUD specification (e.g. `BUD-01`, `buds/02.md`) and receive the raw markdown content in the model context.

