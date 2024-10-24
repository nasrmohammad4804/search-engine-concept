package com.nasr.uniquenesschecker.service;

import java.util.List;
import java.util.Set;

public interface UniquenessCheckerService {

    List<String> foundUrlNotExists(Set<String> urls);
}
