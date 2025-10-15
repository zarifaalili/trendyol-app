package org.example.trendyolfinalproject.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.ChatGroup;
import org.example.trendyolfinalproject.dao.entity.ChatGroupKeys;
import org.example.trendyolfinalproject.dao.repository.ChatGroupKeysRepository;
import org.example.trendyolfinalproject.service.GroupMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Service
@Slf4j
public class GroupMessageServiceImpl implements GroupMessageService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private final ChatGroupKeysRepository chatGroupKeysRepository;

    public GroupMessageServiceImpl(ChatGroupKeysRepository chatGroupKeysRepository) {
        this.chatGroupKeysRepository = chatGroupKeysRepository;
    }

    @Transactional
    @Override
    public void generateAndSaveGroupKey(ChatGroup group) {
        try {
            log.info("Generating group key for group: ");
            SecureRandom random = new SecureRandom();
            byte[] keyBytes = new byte[16];
            random.nextBytes(keyBytes);
            String encodedKey = Base64.getEncoder().encodeToString(keyBytes);

            ChatGroupKeys groupKeys = new ChatGroupKeys();
            groupKeys.setGroup(group);
            groupKeys.setGroupKey(encodedKey);
            chatGroupKeysRepository.save(groupKeys);
            log.info("Generating group key for group: ");
        } catch (Exception e) {
            log.error("Failed to generate group key", e);
            throw new RuntimeException("Failed to generate group key", e);
        }
    }

    @Override
    public String encryptGroupMessage(String plainText, ChatGroup group) {
        try {
            log.info("Encrypting group message for group: ");
            ChatGroupKeys groupKeys = chatGroupKeysRepository.findByGroup(group)
                    .orElseThrow(() -> new RuntimeException("Group key not found"));

            byte[] decodedKey = Base64.getDecoder().decode(groupKeys.getGroupKey());
            SecretKeySpec secretKey = new SecretKeySpec(decodedKey, ALGORITHM);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encryptedMessage = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + encryptedMessage.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedMessage, 0, combined, iv.length, encryptedMessage.length);
            log.info("Encrypting group message for group: ");
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            log.error("Failed to encrypt message", e);
            throw new RuntimeException("Failed to encrypt message", e);
        }
    }

    @Override
    public String decryptGroupMessage(String encryptedBase64, ChatGroup group) {
        try {
            log.info("Decrypting group message for group: ");
            ChatGroupKeys groupKeys = chatGroupKeysRepository.findByGroup(group)
                    .orElseThrow(() -> new RuntimeException("Group key not found"));

            byte[] decodedKey = Base64.getDecoder().decode(groupKeys.getGroupKey());
            SecretKeySpec secretKey = new SecretKeySpec(decodedKey, ALGORITHM);

            byte[] combined = Base64.getDecoder().decode(encryptedBase64);
            byte[] iv = Arrays.copyOfRange(combined, 0, 16);
            byte[] encryptedMessage = Arrays.copyOfRange(combined, 16, combined.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            byte[] decryptedMessage = cipher.doFinal(encryptedMessage);
            log.info("Decrypting group message for group: ");
            return new String(decryptedMessage, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Failed to decrypt message", e);
            throw new RuntimeException("Failed to decrypt message", e);
        }
    }
}
