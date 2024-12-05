package com.nasr.searchservice.controller;

import com.nasr.searchservice.service.WebpageService;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.domain.PageRequest.of;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final WebpageService webpageService;

    @GetMapping
    public ResponseEntity<?> search(@RequestParam @NotEmpty String query, @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(webpageService.search(query, of(page, size)));
    }
}
