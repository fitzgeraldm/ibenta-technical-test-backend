package au.com.ibenta.test.service;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient
@DisplayName("Test Health Check Endpoints")
public class HealthCheckControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    @DisplayName("Authentication Service Health")
    public void testAuthenticationServiceHealthCheck() {

        when(restTemplate.getForObject(anyString(), ArgumentMatchers.eq(String.class))).thenReturn("OK");

        webTestClient.get()
                .uri("/health/authentication-service/status")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);

        verify(restTemplate, times(1))
                .getForObject(anyString(), eq(String.class));
    }


}