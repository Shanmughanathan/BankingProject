package com.example.BankingProject.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.BankingProject.constants.BankingConstants.MAX_LOGIN_ATTEMPT;

@Service
public class LogInAttemptService {

    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();

    public void logInSucceeded(String key) {
        attemptsCache.remove(key);
    }

    public void logInFailed(String key) {
        int attempts = attemptsCache.getOrDefault(key, 0);
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public int getNumberOfAttempts(String key){
        return attemptsCache.get(key);
    }

    public boolean isBlocked(String key) {
        return attemptsCache.getOrDefault(key, 0) >= MAX_LOGIN_ATTEMPT;
    }

}
