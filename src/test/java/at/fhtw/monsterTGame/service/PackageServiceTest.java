package at.fhtw.monsterTGame.service;

import at.fhtw.monsterTGame.model.Cards;
import at.fhtw.monsterTGame.model.User;
import at.fhtw.monsterTGame.persistence.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PackagesServiceTest {

    private PackagesService packagesService;
    private PackagesRepository packagesRepository;
    private CardService cardService;
    private UserRepository userRepository;
    private UserService userService;
    private CardRepository cardRepository;

    @BeforeEach
    void setUp() {
        packagesRepository = mock(PackagesRepository.class);
        cardService = mock(CardService.class);
        userRepository = mock(UserRepository.class);
        userService = mock(UserService.class);
        cardRepository = mock(CardRepository.class);

        packagesService = new PackagesService() {
            @Override
            public boolean addNewPackage(String packageName, List<Cards> cards) throws SQLException, JsonProcessingException {
                return packagesRepository.addNewPackage(packageName, cards);
            }

            @Override
            public List<Cards> buyPackage(String authToken) throws SQLException {
                return packagesRepository.getCardsPackageId(1);
            }

            @Override
            public boolean arePackagesAvailable(int requiredPackages) throws SQLException {
                return packagesRepository.countAvailablePackages() >= requiredPackages;
            }
        };
    }

    @Test
    void testAddNewPackage_Success() throws SQLException, JsonProcessingException {
        List<Cards> cards = List.of(
                new Cards("1", "Dragon", null, null, 50, 1),
                new Cards("2", "Ork", null, null, 30, 1),
                new Cards("3", "WaterGoblin", null, null, 20, 1),
                new Cards("4", "FireElf", null, null, 40, 1),
                new Cards("5", "Knight", null, null, 35, 1)
        );

        when(packagesRepository.addNewPackage(anyString(), anyList())).thenReturn(true);

        boolean result = packagesService.addNewPackage("TestPackage", cards);

        assertTrue(result);
    }

    @Test
    void testAddNewPackage_Fail_WrongCardCount() {
        List<Cards> cards = List.of(new Cards("1", "Dragon", null, null, 50, 1)); // Nur eine Karte

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            packagesService.addNewPackage("TestPackage", cards);
        });

        assertEquals("A package must contain exactly 5 cards.", exception.getMessage());
    }

    @Test
    void testBuyPackage_Success() throws SQLException {
        User user = new User(1, "testuser", "password123", "authToken-123");
        user.setCoins(10);
        when(userRepository.findToken("authToken-123")).thenReturn(user);
        when(packagesRepository.getFreePackageID()).thenReturn(1);
        when(packagesRepository.getCardsPackageId(1)).thenReturn(List.of(new Cards("1", "Dragon", null, null, 50, 1)));

        List<Cards> result = packagesService.buyPackage("authToken-123");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testBuyPackage_Fail_NotEnoughCoins() throws SQLException {
        User user = new User(1, "testuser", "password123", "authToken-123");
        user.setCoins(3); // Nicht genug Coins
        when(userRepository.findToken("authToken-123")).thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            packagesService.buyPackage("authToken-123");
        });

        assertEquals("Not enough coins to buy a package.", exception.getMessage());
    }

    @Test
    void testBuyPackage_Fail_NoAvailablePackages() throws SQLException {
        User user = new User(1, "testuser", "password123", "authToken-123");
        user.setCoins(10);
        when(userRepository.findToken("authToken-123")).thenReturn(user);
        when(packagesRepository.getFreePackageID()).thenReturn(-1); // Keine Pakete verfügbar

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            packagesService.buyPackage("authToken-123");
        });

        assertEquals("No packages available for purchase.", exception.getMessage());
    }

    @Test
    void testArePackagesAvailable() throws SQLException {
        when(packagesRepository.countAvailablePackages()).thenReturn(3);

        boolean result = packagesService.arePackagesAvailable(1);

        assertTrue(result);
    }
}
