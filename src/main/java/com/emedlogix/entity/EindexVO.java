package com.emedlogix.entity;

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
    private Boolean ismainterm;
    private EindexVO child;
}
