package au.com.ibenta.test.service;

import au.com.ibenta.test.exception.ResourceNotFoundException;
import au.com.ibenta.test.model.User;
import au.com.ibenta.test.persistence.UserEntity;
import au.com.ibenta.test.persistence.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private static final Long ID = 1L;
    private static final String SAMPLE_ENCODED_PASSWORD = "ABCDE";
    private static final UserEntity TEST_USER_ENTITY = new UserEntity();
    private static final UserEntity UPDATED_USER_DETAILS_ENTITY = new UserEntity();
    private static User testUser;
    private static User updatedUserDetails;


    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        userService = new UserService(userRepository, objectMapper, passwordEncoder);

        initializeTestData();
        initializeStubs();
    }

    @Test
    public void testGetUser() throws ResourceNotFoundException {
        when(objectMapper.convertValue(any(), eq(User.class))).thenReturn(testUser);

        Mono<User> userMono = userService.get(ID);
        userMono.subscribe(fetchedUser -> {
            assertEquals(testUser.getFirstName(), fetchedUser.getFirstName());
            assertEquals(testUser.getLastName(), fetchedUser.getLastName());
            assertEquals(testUser.getEmail(), fetchedUser.getEmail());
            assertEquals(ID, fetchedUser.getId());
        });
    }

    @Test
    public void testCreateUser() {
        when(objectMapper.convertValue(any(), eq(User.class))).thenReturn(testUser);
        when(objectMapper.convertValue(any(), eq(UserEntity.class))).thenReturn(TEST_USER_ENTITY);
        when(userRepository.save(any(UserEntity.class))).thenReturn(TEST_USER_ENTITY);

        Mono<User> userMono = userService.create(testUser);
        userMono.subscribe(createdUser -> {
            assertEquals(testUser.getFirstName(), createdUser.getFirstName());
            assertEquals(testUser.getLastName(), createdUser.getLastName());
            assertEquals(testUser.getEmail(), createdUser.getEmail());
            assertEquals(ID, createdUser.getId());
        });
    }

    @Test
    public void testDeleteExistingUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        boolean result = userService.delete(ID);
        assertTrue(result);
    }

    @Test
    public void testDeleteNonExistingUser() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        boolean result = userService.delete(ID);
        assertFalse(result);
    }

    @Test
    public void testUpdateUser() throws ResourceNotFoundException {
        when(objectMapper.convertValue(any(), eq(User.class))).thenReturn(updatedUserDetails);
        when(objectMapper.convertValue(any(), eq(UserEntity.class))).thenReturn(UPDATED_USER_DETAILS_ENTITY);
        when(userRepository.save(any(UserEntity.class))).thenReturn(UPDATED_USER_DETAILS_ENTITY);

        Mono<User> userMono = userService.update(ID, updatedUserDetails);
        userMono.subscribe(updatedUser -> {
            assertEquals(updatedUserDetails.getFirstName(), updatedUser.getFirstName());
            assertEquals(updatedUserDetails.getLastName(), updatedUser.getLastName());
            assertEquals(updatedUserDetails.getEmail(), updatedUser.getEmail());
            assertEquals(ID, updatedUser.getId());
        });
    }

    @Test
    public void testGetAllUsers() {
        when(objectMapper.convertValue(any(), eq(User.class))).thenReturn(testUser);
        when(userRepository.findAll()).thenReturn(Collections.singletonList(TEST_USER_ENTITY));

        Flux<User> userFlux = userService.list();

        userFlux.subscribe(fetchedUser -> {
            assertEquals(TEST_USER_ENTITY.getFirstName(), fetchedUser.getFirstName());
            assertEquals(TEST_USER_ENTITY.getLastName(), fetchedUser.getLastName());
            assertEquals(TEST_USER_ENTITY.getEmail(), fetchedUser.getEmail());
            assertEquals(TEST_USER_ENTITY.getId(), fetchedUser.getId());
        });
    }

    private void initializeTestData() {
        testUser = User.builder()
                .id(ID)
                .email("abc@gmail.com")
                .firstName("Alex")
                .lastName("Gonzaga")
                .password("password123")
                .build();

        TEST_USER_ENTITY.setId(ID);
        TEST_USER_ENTITY.setFirstName(testUser.getFirstName());
        TEST_USER_ENTITY.setLastName(testUser.getLastName());
        TEST_USER_ENTITY.setEmail(testUser.getEmail());

        updatedUserDetails = User.builder()
                .email("abcde@gmail.com")
                .firstName("Alexis")
                .lastName("Gonzales")
                .password("password12345")
                .build();

        UPDATED_USER_DETAILS_ENTITY.setId(ID);
        UPDATED_USER_DETAILS_ENTITY.setFirstName(updatedUserDetails.getFirstName());
        UPDATED_USER_DETAILS_ENTITY.setLastName(updatedUserDetails.getLastName());
        UPDATED_USER_DETAILS_ENTITY.setEmail(updatedUserDetails.getEmail());
    }

    private void initializeStubs() {
        when(passwordEncoder.encode(anyString())).thenReturn(SAMPLE_ENCODED_PASSWORD);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(TEST_USER_ENTITY));
    }
}