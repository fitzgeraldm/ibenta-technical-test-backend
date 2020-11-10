package au.com.ibenta.test.service;

import au.com.ibenta.template.BaseTestClass;
import au.com.ibenta.test.exception.ResourceNotFoundException;
import au.com.ibenta.test.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient
@DisplayName("Test User Endpoints")
public class UserControllerTest extends BaseTestClass {

    private static final long id = 1;
    private static User testUser;
    private static Mono<User> userMono;
    private static Flux<User> userFlux;

    @MockBean
    private UserService userService;

    @Autowired
    private WebTestClient webTestClient;

    @Before
    public void setUp() {

        testUser = User.builder()
                .id(id)
                .email("abc@gmail.com")
                .firstName("Alex")
                .lastName("Gonzaga")
                .password("password123")
                .build();

        userMono = Mono.just(testUser);
        userFlux = Mono.just(Collections.singletonList(testUser))
                .flux().flatMap(Flux::fromIterable);
    }

    @Test
    @DisplayName("Test Create User")
    public void testCreateUser() {

        when(userService.create(testUser)).thenReturn(userMono);

        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(testUser), User.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(User.class);

        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    @DisplayName("Test Get User")
    public void testGetUser() throws ResourceNotFoundException {

        when(userService.get(id)).thenReturn(userMono);

        webTestClient.get()
                .uri("/users/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(User::getFirstName, equalTo(testUser.getFirstName()));

        verify(userService, times(1)).get(anyLong());
    }

    @Test
    @DisplayName("Test Delete Existing User")
    public void testDeleteExistingUser() {

        when(userService.delete(id)).thenReturn(true);

        webTestClient.delete()
                .uri("/users/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().isEmpty();

        verify(userService, times(1)).delete(anyLong());
    }

    @Test
    @DisplayName("Test Delete Non-Existing User")
    public void testDeleteNonExistingUser() {

        when(userService.delete(id)).thenReturn(false);

        webTestClient.delete()
                .uri("/users/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

        verify(userService, times(1)).delete(anyLong());
    }

    @Test
    @DisplayName("Test Update User")
    public void testUpdateUser() throws ResourceNotFoundException {

        when(userService.update(id, testUser)).thenReturn(userMono);

        webTestClient.put()
                .uri("/users/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(testUser), User.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class);

        verify(userService, times(1)).update(anyLong(), any(User.class));
    }

    @Test
    @DisplayName("Test List All Users")
    public void testGetAllUsers() {

        when(userService.list()).thenReturn(userFlux);

        webTestClient.get()
                .uri("/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class);

        verify(userService, times(1)).list();
    }

}