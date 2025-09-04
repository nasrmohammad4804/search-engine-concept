package com.nasr.searchservice.controller;

import com.nasr.searchservice.service.WebpageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.springframework.data.domain.PageRequest.of;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WebpageController {

    private final WebpageService webpageService;

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String query, @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) throws IOException {

        return ResponseEntity.ok(webpageService.search(query, of(page, size)));
    }

    @GetMapping("/embed-search")
    public ResponseEntity<?> embedSearch(@RequestParam String query, @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) throws IOException {

        return ResponseEntity.ok(webpageService.embedSearch(query, of(page, size)));
    }

}
