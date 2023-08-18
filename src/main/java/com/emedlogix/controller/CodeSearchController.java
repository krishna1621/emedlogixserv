package com.emedlogix.controller;


import java.io.IOException;
import java.util.List;

import com.emedlogix.entity.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/codes")
public interface CodeSearchController {

    @GetMapping("/{code}/search")
    CodeInfo getCodeInfo(@PathVariable String code) throws IOException;
    @GetMapping("/{code}/matches")
    List<CodeInfo> getCodeInfoMatches(@PathVariable String code);
    @GetMapping("/{description}/description")
    List<CodeInfo> getCodeInfoDescription(@PathVariable String description);
    @GetMapping("/{code}/details")
    CodeDetails getCodeInfoDetails(@PathVariable String code, @RequestParam String version);

    @GetMapping("/{code}/index")
    List<EindexVO> getEIndex(@PathVariable String code);
    @GetMapping("/{code}/neoplasm")
    List<MedicalCodeVO> getNeoPlasm(@PathVariable String code);
    @GetMapping("/{code}/drug")
	List<MedicalCodeVO> getDrug(@PathVariable String code);

    @GetMapping("/alldetails/index")
    List<Eindex> getIndexDetails();
    @GetMapping("/alldetails/neoplasm")
    List<MedicalCodeVO> getNeoplasmDetails();
    @GetMapping("/alldetails/drug")
    List<MedicalCodeVO> getDrugDetails();

    @GetMapping("/mainterm")
    void getLevelTerms();

    @GetMapping("/title/{title}")
    EindexLevels getSearchTerm(@PathVariable String title);

}
