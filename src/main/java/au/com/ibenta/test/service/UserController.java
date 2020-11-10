package au.com.ibenta.test.service;


import au.com.ibenta.test.exception.ResourceNotFoundException;
import au.com.ibenta.test.model.User;
import io.swagger.annotations.Api;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Api(tags = "users")
@RestController
@RequestMapping("/users")
@Profile("users")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Mono<User>> create(@RequestBody User user) {

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(user));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Mono<User>> get(@PathVariable Long id) throws ResourceNotFoundException {

        return ResponseEntity.status(HttpStatus.OK).body(userService.get(id));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Mono<Void>> delete(@PathVariable Long id) {

        if (userService.delete(id)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.notFound().build();

    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Mono<User>> update(@PathVariable Long id, @RequestBody User user)
            throws ResourceNotFoundException {

        return ResponseEntity.status(HttpStatus.OK).body(userService.update(id, user));
    }

    @GetMapping
    public Flux<User> list() {
        return userService.list();
    }
}
