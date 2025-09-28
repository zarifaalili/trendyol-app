package org.example.trendyolfinalproject.dao.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;

@Document(indexName = "group_messages")
@Setting(settingPath = "/elasticsearch/setting.json")
@Getter
@Setter
public class GroupMessageIndex {

    @Id
    private String id;

    private Long senderId;

    private Long groupId;

    @Field(type = FieldType.Text, analyzer = "chat_edge_ngram_analyzer", searchAnalyzer = "chat_lowercase_analyzer")
    private String message;


    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private Instant timestamp;
}
