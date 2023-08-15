package com.emedlogix.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class MedicalCodeVO {
    private Integer id;
    private String title;
    private String see;
    private String seealso;
    private Boolean ismainterm;
    private String nemod;
    private List<String> code;
    private MedicalCodeVO child;
}
