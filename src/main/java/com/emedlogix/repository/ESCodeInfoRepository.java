package com.emedlogix.repository;


import com.emedlogix.entity.CodeInfo;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ESCodeInfoRepository extends ElasticsearchRepository<CodeInfo, String> {

    CodeInfo getByCode(String code);


    List<CodeInfo> findByCodeStartingWith(String code);
    List<CodeInfo> findByDescriptionContains(String description);



}




