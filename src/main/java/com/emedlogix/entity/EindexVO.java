package com.emedlogix.entity;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EindexVO {

    private Integer id;    
    private String title;
    private String code;
    private String see;
    private String seealso;
    private String seecat;
    private Boolean ismainterm;
    private List<Map<String,Object>> termHierarchyList;
}
