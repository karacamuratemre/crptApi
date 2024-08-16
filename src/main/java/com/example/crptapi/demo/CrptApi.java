package com.example.crptapi.demo;
import com.example.crptapi.entity.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class CrptApi {
    private final TimeUnit timeUnit;
    private final int requestLimit;
    private final Lock lock = new ReentrantLock();
    private final ConcurrentMap<Instant, Integer> requestMap = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiUrl = "https://ismp.crpt.ru/api/v3/lk/documents/create";
    private final String apiSignature;

    public CrptApi(TimeUnit timeUnit, int requestLimit, String apiSignature) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.apiSignature = apiSignature;
    }

    public void createDocument(Document document) {
        lock.lock();
        try {
            Instant now = Instant.now();
            Instant windowStart = now.minus(1, timeUnit.toChronoUnit());
            requestMap.keySet().removeIf(instant -> instant.isBefore(windowStart));
            int currentCount = requestMap.values().stream().mapToInt(Integer::intValue).sum();

            if (currentCount >= requestLimit) {
                long waitTime = timeUnit.toMillis(1) - ChronoUnit.MILLIS.between(windowStart, now);
                if (waitTime > 0) {
                    Thread.sleep(waitTime);
                }
            }

            requestMap.merge(now, 1, Integer::sum);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiSignature); // Ensure the token is correct
            headers.set("Content-Type", "application/json"); // Set the correct content type
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(document), headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
            System.out.println("Response: " + response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }






}
