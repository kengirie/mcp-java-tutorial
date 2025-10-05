package com.example.weather.service;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class BlossomDocumentationService {

  private static final String BASE_URL = "https://raw.githubusercontent.com/fiatjaf/blossom/master/";
  private static final Pattern BUD_PATTERN = Pattern.compile("^bud[-_\\s]*(\\d{1,2})$");

  private final RestClient restClient;

  public BlossomDocumentationService() {
    this.restClient = RestClient.builder()
        .baseUrl(BASE_URL)
        .defaultHeader("User-Agent", "MCPBlossomDocReader/1.0 (+https://github.com/fiatjaf/blossom)")
        .defaultHeader("Accept", "text/plain")
        .build();
  }

  @Tool(description = "Read a Blossom (Nostr) documentation file from the official repository")
  public String readBlossomDocument(
      @ToolParam(description = "Document identifier (e.g. README, BUD-01, buds/02.md)") String documentId) {

    String path = resolvePath(documentId);

    try {
      return restClient.get().uri(path).retrieve().body(String.class);
    } catch (RestClientException ex) {
      return String.format("Failed to fetch Blossom document '%s': %s", documentId, ex.getMessage());
    }
  }

  private String resolvePath(String documentId) {
    if (documentId == null) {
      throw new IllegalArgumentException("Document identifier must not be null");
    }

    String trimmed = documentId.trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException("Document identifier must not be empty");
    }

    if (trimmed.contains("..")) {
      throw new IllegalArgumentException("Document identifier must not contain '..'");
    }

    String lower = trimmed.toLowerCase(Locale.ROOT);

    if ("readme".equals(lower) || "readme.md".equals(lower)) {
      return "README.md";
    }

    Matcher matcher = BUD_PATTERN.matcher(lower);
    if (matcher.matches()) {
      int number = Integer.parseInt(matcher.group(1));
      return String.format("buds/%02d.md", number);
    }

    if (lower.startsWith("buds/")) {
      String suffix = trimmed.substring("buds/".length()).toLowerCase(Locale.ROOT);
      if (!suffix.endsWith(".md")) {
        suffix = suffix + ".md";
      }

      return "buds/" + suffix;
    }

    return trimmed;
  }
}
