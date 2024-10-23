package com.nasr.uniquenesschecker.controller;

import com.nasr.uniquenesschecker.service.UniquenessCheckerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/uniqueness")
public class UniquenessCheckingController {

    @Autowired
    private UniquenessCheckerService uniquenessCheckerService;

    @GetMapping("/check")
    public ResponseEntity<?> check(@RequestParam String[] urls){

        List<String> urlNotExists = uniquenessCheckerService.foundUrlNotExists(urls);
        return ResponseEntity.ok(urlNotExists);
    }

}
