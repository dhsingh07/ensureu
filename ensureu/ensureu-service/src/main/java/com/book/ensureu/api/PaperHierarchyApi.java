package com.book.ensureu.api;

import com.book.ensureu.model.PaperHierarchy;
import com.book.ensureu.repository.PaperHierarchyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hierarchy")
public class PaperHierarchyApi {

    @Autowired
    private PaperHierarchyRepository paperHierarchyRepository;

    @CrossOrigin
    @GetMapping("/paper")
    ResponseEntity<List<PaperHierarchy>> getPaperHierarchy(){
        return ResponseEntity.ok(paperHierarchyRepository.findAll());
    }
}
