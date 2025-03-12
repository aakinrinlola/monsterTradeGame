package at.fhtw.monsterTGame.model;

import at.fhtw.monsterTGame.model.enums.ElementTypeEnum;
import at.fhtw.monsterTGame.persistence.repository.*;
import at.fhtw.monsterTGame.persistence.UnitOfWork;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Battle {
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final UnitOfWork unitOfWork;
    private final Deck playerOneDeck;
    private final Deck playerTwoDeck;
    private final List<String> battleHistory;
    private static final int MAX_TURNS = 100;
    private static final int BONUS_COINS = 10;

    public Battle(int playerOneId, int playerTwoId) throws Exception {
        this.unitOfWork = new UnitOfWork();
        this.deckRepository = new DeckRepositoryImpl(unitOfWork);
        this.cardRepository = new CardRepositoryImpl(unitOfWork);
        this.userRepository = new UserRepositoryImpl(unitOfWork);

        this.playerOneDeck = deckRepository.getDeckByUserId(playerOneId);
        this.playerTwoDeck = deckRepository.getDeckByUserId(playerTwoId);

        if (playerOneDeck == null || playerTwoDeck == null) {
            throw new IllegalArgumentException("One of the players does not have a valid deck.");
        }

        this.battleHistory = new ArrayList<>();
    }

    public String commenceBattle() throws SQLException {
        int rounds = 0;

        while (!playerOneDeck.getCards().isEmpty() && !playerTwoDeck.getCards().isEmpty() && rounds < MAX_TURNS) {
            rounds++;
            battleHistory.add("Turn " + rounds + ":");

            Cards firstCard = pickRandomCard(playerOneDeck);
            Cards secondCard = pickRandomCard(playerTwoDeck);

            battleHistory.add("Player One selects: " + firstCard.getName());
            battleHistory.add("Player Two selects: " + secondCard.getName());

            int outcome = determineWinner(firstCard, secondCard);

            if (outcome > 0) {
                // Player One siegt -> Übernimmt Karte von Player Two
                playerOneDeck.getCards().add(secondCard);
                playerTwoDeck.getCards().remove(secondCard);
                transferCardOwnership(secondCard.getCardId(), playerOneDeck.getUserId());
                battleHistory.add("Player One wins this round.");
            } else if (outcome < 0) {
                // Player Two siegt -> Übernimmt Karte von Player One
                playerTwoDeck.getCards().add(firstCard);
                playerOneDeck.getCards().remove(firstCard);
                transferCardOwnership(firstCard.getCardId(), playerTwoDeck.getUserId());
                battleHistory.add("Player Two wins this round.");
            } else {
                battleHistory.add("This turn is a draw.");
            }

            deckRepository.updateDeck(playerOneDeck.getUserId(), playerOneDeck);
            deckRepository.updateDeck(playerTwoDeck.getUserId(), playerTwoDeck);
        }
        return finalizeBattle(rounds);
    }

    public Cards pickRandomCard(Deck deck) {
        List<Cards> availableCards = deck.getCards();
        if (availableCards.isEmpty()) {
            throw new IllegalStateException("Deck contains no cards.");
        }
        return availableCards.get((int) (Math.random() * availableCards.size()));
    }

    int determineWinner(Cards cardOne, Cards cardTwo) {
        double damageOne = cardOne.getDamage();
        double damageTwo = cardTwo.getDamage();

        if (cardOne.isSpell() || cardTwo.isSpell()) {
            if (cardOne.isEffectiveAgainst(cardTwo)) {
                damageOne *= 2;
            } else if (cardOne.isIneffectiveAgainst(cardTwo)) {
                damageOne /= 2;
            }

            if (cardTwo.isEffectiveAgainst(cardOne)) {
                damageTwo *= 2;
            } else if (cardTwo.isIneffectiveAgainst(cardOne)) {
                damageTwo /= 2;
            }
        }
        return Double.compare(damageOne, damageTwo);
    }

    public String finalizeBattle(int turns) throws SQLException {
        String resultMessage;
        boolean playerOneWins = false;
        boolean drawGame = false;
        int eloChangeP1 = 0;
        int eloChangeP2 = 0;

        if (playerOneDeck.getCards().isEmpty() && playerTwoDeck.getCards().isEmpty()) {
            resultMessage = "The match ended in a draw after " + turns + " turns.";
            drawGame = true;
        } else if (playerOneDeck.getCards().isEmpty()) {
            resultMessage = "Match over! Player Two wins.";
            eloChangeP1 = -5;
            eloChangeP2 = +3;
            grantWinnerCoins(playerTwoDeck.getUserId());
        } else {
            resultMessage = "Match over! Player One wins.";
            eloChangeP1 = +3;
            eloChangeP2 = -5;
            playerOneWins = true;
            grantWinnerCoins(playerOneDeck.getUserId());
        }

        userRepository.updateELOAndGamesPlayed(playerOneDeck.getUserId(), eloChangeP1);
        userRepository.updateELOAndGamesPlayed(playerTwoDeck.getUserId(), eloChangeP2);
        userRepository.updateWinLossRecord(playerOneDeck.getUserId(), playerOneWins, drawGame);
        userRepository.updateWinLossRecord(playerTwoDeck.getUserId(), !playerOneWins && !drawGame, drawGame);
        return resultMessage;
    }

    public void grantWinnerCoins(int userId) throws SQLException {
        userRepository.updateCoinsForExtraPrice(userId, BONUS_COINS);
        battleHistory.add("Winner rewarded with " + BONUS_COINS + " extra coins!");
    }

    public List<String> getBattleHistory() {
        return battleHistory;
    }

    public void transferCardOwnership(String cardId, int newUserId) throws SQLException {
        boolean success = cardRepository.updateCardUser(cardId, newUserId);
        if (!success) {
            battleHistory.add("Error: Card ownership update failed for card " + cardId);
            throw new SQLException("Could not transfer ownership for card ID: " + cardId);
        }
    }
}
