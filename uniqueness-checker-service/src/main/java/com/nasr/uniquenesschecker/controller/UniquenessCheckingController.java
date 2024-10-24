package com.nasr.uniquenesschecker.controller;

import com.nasr.uniquenesschecker.domain.ItemRequest;
import com.nasr.uniquenesschecker.service.UniquenessCheckerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/uniqueness")
public class UniquenessCheckingController {

    @Autowired
    private UniquenessCheckerService uniquenessCheckerService;

    @PostMapping("/check")
    public ResponseEntity<?> check(@RequestBody ItemRequest request){

        List<String> urlNotExists = uniquenessCheckerService.foundUrlNotExists(request.getUrls());
        return ResponseEntity.ok(urlNotExists);
    }

}
