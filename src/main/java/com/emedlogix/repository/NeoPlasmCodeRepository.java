package com.emedlogix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emedlogix.entity.NeoPlasmCode;

@Repository
public interface NeoPlasmCodeRepository extends JpaRepository<NeoPlasmCode, Integer> {
	
}
