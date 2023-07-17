package com.emedlogix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emedlogix.entity.TermHierarchy;

@Repository
public interface TermHierarchyRepository extends JpaRepository<TermHierarchy, Integer> {

}
