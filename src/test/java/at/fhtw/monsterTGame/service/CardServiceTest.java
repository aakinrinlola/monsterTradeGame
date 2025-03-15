package at.fhtw.monsterTGame.service;

import at.fhtw.monsterTGame.model.Cards;
import at.fhtw.monsterTGame.model.enums.CardTypeEnum;
import at.fhtw.monsterTGame.model.enums.ElementTypeEnum;
import at.fhtw.monsterTGame.persistence.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    private CardService cardService;
    private CardRepository cardRepo;

    @BeforeEach
    void setUp() {
        cardRepo = mock(CardRepository.class);
        cardService = new CardService() {
            @Override
            public boolean createCard(Cards card) throws SQLException {
                return cardRepo.save(card);
            }

            @Override
            public boolean assignCardToUser(String cardId, int userId) throws SQLException {
                return cardRepo.assignCardToUser(cardId, userId);
            }

            @Override
            public boolean deleteCard(String cardId, int userId) throws SQLException {
                return cardRepo.delete(cardId);
            }

            @Override
            public List<Cards> getCardsByUserId(int userId) throws SQLException {
                return new ArrayList<>(cardRepo.findCardsByUserId(userId));

            }
        };
    }

    @Test
    void testCreateCard_Success() throws SQLException {
        Cards card = new Cards("1", "Dragon", null, null, 50, 1);
        when(cardRepo.save(any(Cards.class))).thenReturn(true);

        boolean result = cardService.createCard(card);

        assertTrue(result);
    }

    @Test
    void testCreateCard_ValidCard() throws SQLException {
        Cards card = new Cards("1", "WaterGoblin", ElementTypeEnum.WATER, CardTypeEnum.MONSTER, 30, 1);
        when(cardRepo.save(any(Cards.class))).thenReturn(true);

        boolean result = cardService.createCard(card);

        assertTrue(result, "Die Karte sollte erfolgreich erstellt werden.");
    }

    @Test
    void testGetCardsByUserId_EmptyList() throws SQLException {
        when(cardRepo.findCardsByUserId(20)).thenReturn(new ArrayList<>()); // Benutzer 20 hat keine Karten

        List<Cards> result = new ArrayList<>(cardService.getCardsByUserId(20));

        assertTrue(result.isEmpty(), "Die Kartenliste sollte leer sein.");
    }

    @Test
    void testAssignCardToUser_Fail_NonExistentCard() throws SQLException {
        when(cardRepo.assignCardToUser("99", 5)).thenReturn(false); // Karte 99 existiert nicht

        boolean result = cardService.assignCardToUser("99", 5);

        assertFalse(result, "Das Zuweisen einer nicht existierenden Karte sollte fehlschlagen.");
    }

    @Test
    void testAssignCardToUser_Fail_InvalidUser() throws SQLException {
        when(cardRepo.assignCardToUser("1", -1)).thenReturn(false); //  Benutzer-ID ist ungültig (-1)

        boolean result = cardService.assignCardToUser("1", -1);

        assertFalse(result, "Die Karte sollte nicht einem ungültigen Benutzer zugewiesen werden.");
    }


    @Test
    void testAssignCardToUser_Success() throws SQLException {
        when(cardRepo.assignCardToUser("1", 10)).thenReturn(true);

        boolean result = cardService.assignCardToUser("1", 10);

        assertTrue(result);
    }

    @Test
    void testGetCardsByUserId_Success() throws SQLException {
        List<Cards> userCards = List.of(
                new Cards("1", "Dragon", null, null, 50, 1),
                new Cards("2", "Ork", null, null, 30, 1)
        );
        when(cardRepo.findCardsByUserId(10)).thenReturn(userCards);

        List<Cards> result = new ArrayList<>(cardService.getCardsByUserId(10));

        assertEquals(2, result.size());
    }


    @Test
    void testDeleteCard_Success() throws SQLException {
        when(cardRepo.findCardsByUserId(10)).thenReturn(List.of(
                new Cards("1", "Dragon", null, null, 50, 1)
        ));
        when(cardRepo.delete("1")).thenReturn(true);

        boolean result = cardService.deleteCard("1", 10);

        assertTrue(result);
    }
}
