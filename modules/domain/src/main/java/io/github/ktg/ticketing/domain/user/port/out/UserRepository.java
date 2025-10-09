package io.github.ktg.ticketing.domain.user.port.out;

import io.github.ktg.ticketing.domain.user.model.User;

public interface UserRepository {

    User findByEmail(String email);
    User save(User user);

}
