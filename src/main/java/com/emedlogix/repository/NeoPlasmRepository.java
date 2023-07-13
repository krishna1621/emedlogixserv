package com.emedlogix.repository;

import java.util.Map;
import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.emedlogix.entity.Neoplasm;

@Repository
public interface NeoPlasmRepository extends JpaRepository<Neoplasm, Integer> {

	@Query("SELECT n.id as id ,n.title as title,n.see as see,n.seealso as seealso,n.ismainterm as ismainterm,c.code as code FROM neoplasm n join neoplasm_code c on n.id=c.neoplasm_id WHERE c.code = :code")
	List<Map<String,Object>> findNeoplasmByCode(String code);
}
