package com.nasr.uniquenesschecker.service;

import java.util.List;

public interface UniquenessCheckerService {

    List<String> foundUrlNotExists(String[] urls);
}
