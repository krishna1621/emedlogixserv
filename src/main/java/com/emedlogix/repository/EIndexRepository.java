package com.emedlogix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.emedlogix.entity.EIndex;

@Repository
public interface EIndexRepository extends JpaRepository<EIndex, String> {

	@Query("SELECT e FROM eindex e WHERE e.code = :code and e.title like %:title%")
	List<EIndex> findMainTermBySearch(String code,String title);
}
