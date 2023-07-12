package com.emedlogix.repository;


import com.emedlogix.entity.CodeInfo;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ESCodeInfoRepository  {

    CodeInfo getByCode(String code);

    List<CodeInfo> findByCode(String s);
    List<CodeInfo> findByCodeStartingWith(String code);
}




