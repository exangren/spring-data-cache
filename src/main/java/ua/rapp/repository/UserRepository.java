package ua.rapp.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import ua.rapp.data.User;

import javax.persistence.QueryHint;

import java.util.Optional;

import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;
import static org.hibernate.jpa.QueryHints.HINT_CACHE_REGION;

public interface UserRepository extends CrudRepository<User, Integer> {

    @QueryHints({
            @QueryHint(name = HINT_CACHEABLE, value = "true"),
            @QueryHint(name = HINT_CACHE_REGION, value = "query-cache-users")
    })
    @Override
    Iterable<User> findAll();

    @Query(value = "SELECT name FROM user WHERE email = :email", nativeQuery = true)
    Optional<String> findNameByEmail(String email);
}
