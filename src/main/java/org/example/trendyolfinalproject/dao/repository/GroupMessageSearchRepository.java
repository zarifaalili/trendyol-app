package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.GroupMessageIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMessageSearchRepository extends ElasticsearchRepository<GroupMessageIndex, String> {
    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "query_string": {
                      "default_field": "message",
                      "query": "*?0*"
                    }
                  },
                  {
                    "term": { "groupId": ?1 }
                  }
                ]
              }
            }
            """)
    Page<GroupMessageIndex> searchMessagesByKeywordAndGroup(String keyword, Long groupId, Pageable pageable);

}
