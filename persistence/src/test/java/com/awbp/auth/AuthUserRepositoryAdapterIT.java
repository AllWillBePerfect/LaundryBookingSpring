package com.awbp.auth;

import com.awbp.AbstractRepositoryIT;
import com.awbp.identity.domain.AuthProvider;
import com.awbp.identity.domain.UserAccount;
import com.awbp.identity.domain.UserIdentity;
import com.awbp.user.UserIdentityJpaRepository;
import com.awbp.user.UserJpaRepository;
import com.awbp.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthUserRepositoryAdapterIT extends AbstractRepositoryIT {

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private UserIdentityJpaRepository userIdentityJpaRepository;

    private AuthUserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AuthUserRepositoryAdapter(userJpaRepository, userIdentityJpaRepository);
    }

    @Test
    void givenPasswordRegistration_whenCreateUserWithPassword_thenPersistsUserAndIdentity() {
        UserAccount account = adapter.createUserWithPassword("john", "John", "hash", UserRole.USER);

        assertNotNull(account.id());
        assertEquals("John", account.displayName());
        assertEquals(UserRole.USER, account.role());

        Optional<UserIdentity> identity = adapter.findIdentity(AuthProvider.PASSWORD, "john");
        assertTrue(identity.isPresent());
        assertEquals(account.id(), identity.get().userId());
        assertEquals("hash", identity.get().secret());
    }

    @Test
    void givenTelegramAuth_whenCreateUserWithTelegram_thenPersistsIdentityWithoutSecret() {
        UserAccount account = adapter.createUserWithTelegram("tg-1", "John", UserRole.USER);

        Optional<UserIdentity> identity = adapter.findIdentity(AuthProvider.TELEGRAM, "tg-1");
        assertTrue(identity.isPresent());
        assertEquals(account.id(), identity.get().userId());
        assertNull(identity.get().secret());
    }

    @Test
    void givenNoIdentity_whenFindIdentity_thenEmpty() {
        assertTrue(adapter.findIdentity(AuthProvider.PASSWORD, "missing").isEmpty());
    }

    @Test
    void givenExistingIdentity_whenIdentityExists_thenReflectsPresence() {
        adapter.createUserWithPassword("john", "John", "hash", UserRole.USER);

        assertTrue(adapter.identityExists(AuthProvider.PASSWORD, "john"));
        assertFalse(adapter.identityExists(AuthProvider.PASSWORD, "other"));
    }

    @Test
    void givenUser_whenFindUserById_thenReturnsAccount() {
        UserAccount created = adapter.createUserWithPassword("john", "John", "hash", UserRole.USER);

        Optional<UserAccount> found = adapter.findUserById(created.id());

        assertTrue(found.isPresent());
        assertEquals("John", found.get().displayName());
    }

    @Test
    void givenNoUser_whenFindUserById_thenEmpty() {
        assertTrue(adapter.findUserById(9999L).isEmpty());
    }
}
