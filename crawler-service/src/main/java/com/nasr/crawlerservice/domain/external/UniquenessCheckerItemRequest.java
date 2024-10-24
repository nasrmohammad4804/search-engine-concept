package com.nasr.crawlerservice.domain.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UniquenessCheckerItemRequest {

    private Set<String> urls;
}
