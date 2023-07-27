package com.emedlogix.repository;

import java.util.List;
import java.util.Map;

import com.emedlogix.entity.EindexVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.emedlogix.entity.Eindex;

@Repository
public interface EindexRepository extends JpaRepository<Eindex, Integer> {


	@Query("SELECT e from eindex e where e.code = :code")
	List<Eindex> findMainTermBySearch(String code);

	@Query(value = "SELECT t.parent_id as id,e.title as title,t.level as level,e.code as code,e.see as see,e.seealso as seealso,e.nemod as nemod,e.ismainterm as ismainterm from eindex e "
			+ "join term_hierarchy t on t.parent_id=e.id where t.child_id = :id order by t.level asc", nativeQuery = true)
	List<Map<String,Object>> getParentChildList(Integer id);

	@Query(value ="SELECT * from eindex e",nativeQuery = true)
	List<Map<String, Object>> findAllIndexData();



}
