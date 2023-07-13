package com.emedlogix.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.emedlogix.entity.Drug;

@Repository
public interface DrugRepository extends JpaRepository<Drug, Integer> {

	@Query("SELECT n.id as id ,n.title as title,n.see as see,n.seealso as seealso,n.ismainterm as ismainterm,c.code as code FROM drug n join drug_code c on n.id=c.drug_id WHERE c.code = :code")
	List<Map<String,Object>> findDrugByCode(String code);
}
