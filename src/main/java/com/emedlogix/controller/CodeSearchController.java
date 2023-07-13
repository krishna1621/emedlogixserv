package com.emedlogix.controller;


import com.emedlogix.entity.CodeDetails;
import com.emedlogix.entity.CodeInfo;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping(value = "/codes")
public interface CodeSearchController {

    @GetMapping("/{code}/search")
    CodeInfo getCodeInfo(@PathVariable String code) throws IOException;
    @GetMapping("/{code}/matches")
    List<CodeInfo> getCodeInfoMatches(@PathVariable String code);
    @GetMapping("/{code}/details")                                                                                                                                                                                                                                    
    CodeDetails getCodeInfoDetails(@PathVariable String code ,@RequestParam String version );
    @GetMapping("/{description}/description")
    List<CodeInfo> getCodeInfoDescription(@PathVariable String description);

    @GetMapping("/{searchTerm}/allsearch")
    List<CodeInfo> getAllInfo(@PathVariable String searchTerm);

}
