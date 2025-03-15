package at.fhtw.monsterTGame.service;

import at.fhtw.monsterTGame.model.User;
import at.fhtw.monsterTGame.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    private PaymentService paymentService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        paymentService = new PaymentService() {
            @Override
            public Map<String, Object> processPayment(String token) throws SQLException {
                return super.processPayment(token);
            }
        };
    }

    @Test
    void testProcessPayment_Success() throws SQLException {
        User user = new User(1, "testuser", "hashedPassword", "valid-token", 10);
        when(userRepository.findToken("valid-token")).thenReturn(user);

        Map<String, Object> result = paymentService.processPayment("valid-token");

        assertEquals("testuser", result.get("username"));
        assertEquals(10, result.get("previousBalance"));
        assertEquals(5, result.get("newBalance"));
        assertEquals("Payment successful", result.get("status"));
    }

    @Test
    void testProcessPayment_Fail_InvalidToken() throws SQLException {
        when(userRepository.findToken("invalid-token")).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment("invalid-token");
        });

        assertEquals("Invalid or expired token", exception.getMessage());
    }

    @Test
    void testProcessPayment_Fail_InsufficientFunds() throws SQLException {
        User user = new User(1, "testuser", "hashedPassword", "valid-token", 3);
        when(userRepository.findToken("valid-token")).thenReturn(user);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.processPayment("valid-token");
        });

        assertEquals("Insufficient balance", exception.getMessage());
    }

    @Test
    void testProcessPayment_CorrectBalanceUpdate() throws SQLException {
        User user = new User(1, "testuser", "hashedPassword", "valid-token", 10);
        when(userRepository.findToken("valid-token")).thenReturn(user);

        Map<String, Object> result = paymentService.processPayment("valid-token");

        verify(userRepository).updateCoinsForExtraPrice(1, -5);
        assertEquals(5, result.get("newBalance"));
    }

    @Test
    void testProcessPayment_Fail_DatabaseError() throws SQLException {
        when(userRepository.findToken("valid-token")).thenThrow(new SQLException("Database error"));

        Exception exception = assertThrows(SQLException.class, () -> {
            paymentService.processPayment("valid-token");
        });

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void testProcessPayment_ZeroBalance() throws SQLException {
        User user = new User(1, "testuser", "hashedPassword", "valid-token", 0);
        when(userRepository.findToken("valid-token")).thenReturn(user);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.processPayment("valid-token");
        });

        assertEquals("Insufficient balance", exception.getMessage());
    }

    @Test
    void testProcessPayment_NegativeBalance() throws SQLException {
        User user = new User(1, "testuser", "hashedPassword", "valid-token", -5);
        when(userRepository.findToken("valid-token")).thenReturn(user);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.processPayment("valid-token");
        });

        assertEquals("Insufficient balance", exception.getMessage());
    }

    @Test
    void testProcessPayment_ExactBalance() throws SQLException {
        User user = new User(1, "testuser", "hashedPassword", "valid-token", 5);
        when(userRepository.findToken("valid-token")).thenReturn(user);

        Map<String, Object> result = paymentService.processPayment("valid-token");

        assertEquals(0, result.get("newBalance"));
        assertEquals("Payment successful", result.get("status"));
    }

    @Test
    void testProcessPayment_RepositoryCalled() throws SQLException {
        User user = new User(1, "testuser", "hashedPassword", "valid-token", 10);
        when(userRepository.findToken("valid-token")).thenReturn(user);

        paymentService.processPayment("valid-token");

        verify(userRepository, times(1)).findToken("valid-token");
        verify(userRepository, times(1)).updateCoinsForExtraPrice(1, -5);
    }

    @Test
    void testProcessPayment_Fail_NullToken() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(null);
        });

        assertEquals("Invalid or expired token", exception.getMessage());
    }
}
