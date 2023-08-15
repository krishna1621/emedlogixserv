package com.emedlogix.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "drug_code")
public class DrugCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer drug_id;
    String code;
}
