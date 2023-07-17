package com.emedlogix.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicalCodeVO {
    Integer id;
    String title;
    String see;
    String seealso;
    Boolean ismainterm;
    List<String> code;
}
