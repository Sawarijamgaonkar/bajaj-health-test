
package com.test.bajajhealthtest;
// package com.test.baja_health_test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

import lombok.Data;

@Component
public class AppRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Application has started. Beginning the process...");
        RestTemplate restTemplate = new RestTemplate();
        String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        String name = "Sawari Jamgaonkar";
        String regNo = "22BAI10393"; 
        String email = "sawari.jamgaonkar18@gmail.com";

        Map<String, String> initialRequest = Map.of("name", name, "regNo", regNo, "email", email);

        System.out.println("Sending initial data: " + initialRequest);

        WebhookResponse response = restTemplate.postForObject(generateWebhookUrl, initialRequest, WebhookResponse.class);

        if (response == null || response.getWebhookURL() == null) {
            System.err.println("❌ Failed to get a valid response from the webhook generation API.");
            return;
        }

        System.out.println("✅ Successfully received the webhook URL: " + response.getWebhookURL());
        System.out.println(" Received the  Access Token.");
        System.out.println("Preparing the SQL query solution.");
        String finalSqlQuery = "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, d.DEPARTMENT_NAME FROM PAYMENTS p JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID WHERE EXTRACT(DAY FROM p.PAYMENT_TIME) != 1 ORDER BY p.AMOUNT DESC LIMIT 1;";
        
        System.out.println("Final SQL Query prepared: " + finalSqlQuery);
        String submissionUrl = response.getWebhookURL();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(response.getAccessToken());

        Map<String, String> finalRequest = Collections.singletonMap("finalQuery", finalSqlQuery);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(finalRequest, headers);
        
        System.out.println("Submitting final query to: " + submissionUrl);

        String submissionResponse = restTemplate.postForObject(submissionUrl, entity, String.class);

        System.out.println("📬 Submission response: " + submissionResponse);
        System.out.println("🎉 Process Complete!");
    }
}

@Data
class WebhookResponse {
    private String webhookURL;
    private String accessToken;
}