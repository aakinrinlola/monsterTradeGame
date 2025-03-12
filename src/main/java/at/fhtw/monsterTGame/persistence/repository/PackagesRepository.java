package at.fhtw.monsterTGame.persistence.repository;

import at.fhtw.monsterTGame.model.Cards;
import at.fhtw.monsterTGame.model.Packages;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface PackagesRepository {
    int getCoinsByToken(String authToken) throws SQLException;
    int getFreePackageID() throws SQLException;
    List<Cards> getCardsPackageId(int packageId) throws SQLException;
    void updateCoinsUser(String authToken, int updatedCoins) throws SQLException;
    void setPackageAsSold(int packageId, int userId) throws SQLException;
    boolean addNewPackage(String packageName, List<Cards> cards) throws SQLException, JsonProcessingException;
    Collection<Packages> findPackagesFromToken(String authToken) throws SQLException;
}
