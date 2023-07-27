package com.emedlogix.controller;


import java.io.IOException;
import java.util.List;

import com.emedlogix.entity.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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
    CodeDetails getCodeInfoDetails(@PathVariable String code, @RequestParam("version") String version);

    @GetMapping("/{code}/index")
    List<EindexVO> getEIndex(@PathVariable String code);

    @GetMapping("/alldetails/index")
    List<EindexVO> getIndexDetails();

    @GetMapping("/{code}/neoplasm")
    List<MedicalCodeVO> getNeoPlasm(@PathVariable String code);

    @GetMapping("/alldetails/neoplasm")
    List<MedicalCodeVO> getNeoplasmDetails();
    @GetMapping("/filterby/neoplasm")
    List<MedicalCodeVO> filterNeoplasmDetails(@RequestParam("filterBy") String filterBy);

    @GetMapping("/{code}/drug")
	List<MedicalCodeVO> getDrug(@PathVariable String code);


}
