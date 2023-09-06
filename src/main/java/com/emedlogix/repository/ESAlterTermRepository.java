package com.emedlogix.repository;

import com.emedlogix.entity.AlterTerm;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ESAlterTermRepository extends ElasticsearchRepository<AlterTerm, String> {
    List<AlterTerm> findByAlterDescription(String alterDescription);

    Optional<AlterTerm> findByCode(String code);
}