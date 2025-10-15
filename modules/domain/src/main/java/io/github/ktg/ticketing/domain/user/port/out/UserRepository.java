package io.github.ktg.ticketing.domain.user.port.out;

import io.github.ktg.ticketing.domain.user.model.User;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);
    User save(User user);

}
