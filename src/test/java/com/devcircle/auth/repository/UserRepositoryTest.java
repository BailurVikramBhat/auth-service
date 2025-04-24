
package com.devcircle.auth.repository;

import com.devcircle.auth.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveUserSuccessfully() {
        User user = new User();
        user.setUsername("vikram");
        user.setEmail("vikram@dc.com");
        user.setPassword("secret");

        userRepository.save(user);

        List<User> allUsers = userRepository.findAll();
        assertEquals(1, allUsers.size());
        assertEquals("vikram", allUsers.get(0).getUsername());
    }
}
