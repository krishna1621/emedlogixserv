package com.emedlogix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emedlogix.entity.NeoplasmHierarchy;

@Repository
public interface NeoplasmHierarchyRepository extends JpaRepository<NeoplasmHierarchy, Integer> {

}
