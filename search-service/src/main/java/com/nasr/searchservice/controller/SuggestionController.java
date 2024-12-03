package com.nasr.searchservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/suggest")
public class SuggestionController {

    @GetMapping
    public ResponseEntity<?> suggest(@RequestParam("query")  String query) {
        return ResponseEntity.ok().build();
    }
}
