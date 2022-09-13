package ua.rapp.controller;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.rapp.data.User;
import ua.rapp.repository.UserRepository;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

@Controller
@RequestMapping(path = "/users")
public class UserController {

    private static final String CACHED_NAMES = "nameByEmail";
    private UserRepository userRepository;
    private ConcurrentMap<String, String> nameByEmailMap;

    public UserController(UserRepository userRepository, HazelcastInstance hazelcastInstance) {
        this.userRepository = userRepository;
        nameByEmailMap = hazelcastInstance.getMap(CACHED_NAMES);
    }

    @PostMapping(path = "/add")
    public @ResponseBody String addNewUser(@RequestParam String name, @RequestParam String email) {

        User n = new User();
        n.setName(name);
        n.setEmail(email);
        userRepository.save(n);
        return "OK";
    }

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping(path = "{id}")
    public @ResponseBody ResponseEntity<User> getUser(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok().body(user))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/email/{email}")
    public @ResponseBody ResponseEntity<String> getNameByEmail(@PathVariable String email) {

        return getFromCache(email)
                .map(name -> ResponseEntity.ok().body(name))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Optional<String> getFromCache(String email) {
        return Optional.ofNullable(nameByEmailMap.computeIfAbsent(email,
                (e) -> userRepository.findNameByEmail(e).orElse(null)));
    }
}
