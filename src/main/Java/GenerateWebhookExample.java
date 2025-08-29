import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class GenerateWebhookExample {

    // request body class
    static class GenerateWebhookRequest {
        private String name;
        private String regNo;
        private String email;

        public GenerateWebhookRequest(String name, String regNo, String email) {
            this.name = name;
            this.regNo = regNo;
            this.email = email;
        }

        public String getName() { return name; }
        public String getRegNo() { return regNo; }
        public String getEmail() { return email; }
    }

    // response body class
    static class GenerateWebhookResponse {
        private String webhook;
        private String accessToken;

        public String getWebhook() { return webhook; }
        public void setWebhook(String webhook) { this.webhook = webhook; }

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    }

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        // 1️⃣ First API call: Generate webhook + token
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        GenerateWebhookRequest req =
                new GenerateWebhookRequest("John Doe", "REG12347", "john@example.com"); 
        // 👆 Replace with YOUR real name, regNo, email

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GenerateWebhookRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<GenerateWebhookResponse> response =
                restTemplate.postForEntity(url, entity, GenerateWebhookResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Error: " + response.getStatusCode());
            return;
        }

        String webhookUrl = response.getBody().getWebhook();
        String accessToken = response.getBody().getAccessToken();

        System.out.println("Webhook URL = " + webhookUrl);
        System.out.println("Access Token = " + accessToken);

        // 2️⃣ Second API call: Submit SQL query
        // Since your regNo is EVEN → Question 2
        String sqlQuery = "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, "
        + "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT "
        + "FROM EMPLOYEE e1 "
        + "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID "
        + "LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT "
        + "AND e2.DOB > e1.DOB "
        + "GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME "
        + "ORDER BY e1.EMP_ID DESC;"; // <-- replace with your real SQL answer

        HttpHeaders submitHeaders = new HttpHeaders();
        submitHeaders.setContentType(MediaType.APPLICATION_JSON);
        submitHeaders.set("Authorization", accessToken); // IMPORTANT

        String submitBody = "{ \"finalQuery\": \"" + sqlQuery + "\" }";

        HttpEntity<String> submitEntity = new HttpEntity<>(submitBody, submitHeaders);

        ResponseEntity<String> submitResponse =
                restTemplate.postForEntity(webhookUrl, submitEntity, String.class);

        System.out.println("Submission status = " + submitResponse.getStatusCode());
        System.out.println("Submission response = " + submitResponse.getBody());
    }
}
