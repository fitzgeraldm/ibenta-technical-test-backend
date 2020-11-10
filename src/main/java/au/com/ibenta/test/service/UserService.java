package au.com.ibenta.test.service;

import au.com.ibenta.test.exception.ResourceNotFoundException;
import au.com.ibenta.test.model.User;
import au.com.ibenta.test.persistence.UserEntity;
import au.com.ibenta.test.persistence.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserService {

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ObjectMapper objectMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
    }


    public Mono<User> create(User user) {
        UserEntity createdUser = userRepository.save(convertToEntity(user));
        return Mono.just(convertToDto(createdUser));
    }

    public Mono<User> get(Long id) throws ResourceNotFoundException {
        Optional<UserEntity> found = userRepository.findById(id);
        return Mono.just(found
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Resource Not Found")));
    }

    public Mono<User> update(Long id, User updatedUserDetails) throws ResourceNotFoundException {

        UserEntity userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource Not Found"));

        updatedUserDetails.setId(userToUpdate.getId());

        UserEntity updatedUser = userRepository.save(convertToEntity(updatedUserDetails));

        return Mono.just(convertToDto(updatedUser));
    }

    public boolean delete(Long id) {

        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Flux<User> list() {

        return Mono.just(userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()))
                .flux().flatMap(Flux::fromIterable);
    }

    private User convertToDto(UserEntity userEntity) {
        return objectMapper.convertValue(userEntity, User.class);
    }

    private UserEntity convertToEntity(User user) {
        UserEntity userEntity = objectMapper.convertValue(user, UserEntity.class);
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        return userEntity;
    }
}
