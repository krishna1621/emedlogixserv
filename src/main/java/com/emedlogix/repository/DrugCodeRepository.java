package com.emedlogix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emedlogix.entity.DrugCode;

@Repository
public interface DrugCodeRepository extends JpaRepository<DrugCode, Integer> {
	
}
