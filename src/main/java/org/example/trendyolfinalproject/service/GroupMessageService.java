package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.dao.entity.ChatGroup;

public interface GroupMessageService {

    void generateAndSaveGroupKey(ChatGroup group);

    String encryptGroupMessage(String plainText, ChatGroup group);

    String decryptGroupMessage(String encryptedBase64, ChatGroup group);


}
