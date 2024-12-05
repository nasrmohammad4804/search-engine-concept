package com.nasr.searchservice.controller;

import com.nasr.searchservice.service.WebpageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/suggest")
public class SuggestionController {

    private final WebpageService webpageService;

    @GetMapping
    public ResponseEntity<?> suggest(@RequestParam("query") String query) throws IOException {
        List<String> suggestions = webpageService.suggest(query);
        return ResponseEntity.ok(suggestions);
    }
}
