package com.emedlogix.controller;

import com.emedlogix.entity.AlterTerm;
import com.emedlogix.repository.ESAlterTermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alter-terms")
public class AlterTermController {

    @Autowired
    private ESAlterTermRepository esAlterTermRepository;

    @GetMapping("/search")
    public List<AlterTerm> searchByAlterDescription(@RequestParam String alterDescription) {
        // Use the Elasticsearch repository to search for AlterTerm objects by alterDescription
        return esAlterTermRepository.findByAlterDescription(alterDescription);
    }
}