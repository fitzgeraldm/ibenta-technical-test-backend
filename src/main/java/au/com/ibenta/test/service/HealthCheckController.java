package au.com.ibenta.test.service;


import io.swagger.annotations.Api;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Api(tags = "health")
@RestController
@RequestMapping("/health")
@Profile("health")
public class HealthCheckController {

    private final RestTemplate restTemplate;


    public HealthCheckController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping(value = "/authentication-service/status")
    public String getActuatorHealth() {

        String redirectUrl = "http://authentication-service.staging.ibenta.com/actuator/health";
        return restTemplate.getForObject(redirectUrl, String.class);
    }
}
