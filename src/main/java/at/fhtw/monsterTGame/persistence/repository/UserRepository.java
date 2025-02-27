package at.fhtw.monsterTGame.persistence.repository;

import at.fhtw.monsterTGame.model.User;

import java.sql.SQLException;
import java.util.Collection;

public interface UserRepository {
    //eventuelle Erweiterungen noch
    User findName(String username) throws SQLException;
    User findId(int userId) throws SQLException;
    Collection<User>findAllUsers() throws SQLException;
    boolean saveUser(User user) throws SQLException;
    void updateToken (String username, String token) throws SQLException;
    User findToken(String token) throws SQLException;
    void deleteUser(int userId) throws SQLException;

}