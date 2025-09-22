package com.example.hiring_app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BajajTest implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        SpringApplication.run(BajajTest.class, args);
    }

    @Override
    public void run(String... args) {
        try {

            //generate Webhook

            String genUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            Map<String, String> request = new HashMap<>();
            request.put("name", "Dhairya Gupta");
            request.put("regNo", "0002AL221019");
            request.put("email", "dhairyagupta1147@gmail.com");

            ResponseEntity<Map> genResponse =
                    restTemplate.postForEntity(genUrl, request, Map.class);

            if (!genResponse.getStatusCode().is2xxSuccessful()) {
                System.err.println("Webhook generation failed: " + genResponse.getStatusCode());
                return;
            }

            Map<String, Object> body = genResponse.getBody();
            if (body == null) {
                System.err.println("Empty response from webhook generation.");
                return;
            }

            String webhookUrl = (String) body.get("webhook");
            String accessToken = (String) body.get("accessToken");

            System.out.println("Got webhook: " + webhookUrl);
            System.out.println("Got accessToken: " + accessToken);


            //prepare Final SQL Query

            String finalQuery =
                    "SELECT P.AMOUNT AS SALARY, " +
                            "CONCAT(E.FIRST_NAME, ' ', E.LAST_NAME) AS NAME, " +
                            "TIMESTAMPDIFF(YEAR, E.DOB, CURDATE()) AS AGE, " +
                            "D.DEPARTMENT_NAME " +
                            "FROM PAYMENTS P " +
                            "JOIN EMPLOYEE E ON P.EMP_ID = E.EMP_ID " +
                            "JOIN DEPARTMENT D ON E.DEPARTMENT = D.DEPARTMENT_ID " +
                            "WHERE DAY(P.PAYMENT_TIME) <> 1 " +
                            "ORDER BY P.AMOUNT DESC " +
                            "LIMIT 1";


            //submit Final Query

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);

            Map<String, String> submitBody = new HashMap<>();
            submitBody.put("finalQuery", finalQuery);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(submitBody, headers);

            ResponseEntity<String> submitResp =
                    restTemplate.exchange(webhookUrl, HttpMethod.POST, entity, String.class);

            System.out.println("Submission HTTP status: " + submitResp.getStatusCode());
            System.out.println("Submission response: " + submitResp.getBody());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //stop app after submission
            System.exit(0);
        }
    }
}
