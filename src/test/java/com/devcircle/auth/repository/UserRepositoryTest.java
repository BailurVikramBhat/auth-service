package com.devcircle.auth.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.devcircle.auth.model.User;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveAndFindByEmail() {
        User user = new User();
        user.setFullName("Vikram Bhat");
        user.setEmail("vikram@dc.com");
        user.setPassword("secret");
        userRepository.save(user);

        // findAll
        List<User> allUsers = userRepository.findAll();
        assertEquals(1, allUsers.size());
        assertEquals("Vikram Bhat", allUsers.get(0).getFullName());

        // findByEmail
        var opt = userRepository.findByEmail("vikram@dc.com");
        assertTrue(opt.isPresent());
        assertEquals(user.getEmail(), opt.get().getEmail());

        // existsByEmail
        assertTrue(userRepository.existsByEmail("vikram@dc.com"));
        assertFalse(userRepository.existsByEmail("other@dc.com"));
    }

    @Test
    void shouldEnforceUniqueEmailConstraint() {
        User u1 = User.build("A", "a@dc.com", "pw", null);
        userRepository.saveAndFlush(u1);

        User u2 = User.build("B", "a@dc.com", "pw2", null);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(u2));
    }
}
