package at.fhtw.monsterTGame.service;

import at.fhtw.monsterTGame.model.User;
import at.fhtw.monsterTGame.persistence.UnitOfWork;
import at.fhtw.monsterTGame.persistence.repository.UserRepository;
import at.fhtw.monsterTGame.persistence.repository.UserRepositoryImpl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public class UserService {
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepositoryImpl(new UnitOfWork());
    }
    //Benutzer registrieren
    public boolean registerUser(User user) throws SQLException {
        if (userRepository.findName(user.getUsername()) != null) {
            //das mache ich damit Controller 409 zurückgibt
            return false;
        }
        return userRepository.saveUser(user);
    }


    // Benutzer anhand des Namens abrufen
    public User findUserByName(String username) throws SQLException {
        return userRepository.findName(username);
    }

    // Benutzer anhand der ID abrufen
    public User findUserById(int userId) throws SQLException {
        return userRepository.findId(userId);
    }

    // Authentifizierung des Benutzers (Login)
    public String authenticateUser(String username, String password) throws SQLException {
        User existingUser = userRepository.findName(username);

        if (existingUser == null || !existingUser.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // Token wird nur erzeugt, wenn der Benutzer noch keines hat
        if (existingUser.getToken() == null || existingUser.getToken().isEmpty()) {
            String sessionToken = "authToken-" + UUID.randomUUID();
            existingUser.setToken(sessionToken);
            userRepository.updateToken(username, sessionToken);
        }

        return existingUser.getToken();
    }


    // Alle Benutzer abrufen
    public Collection<User> getAllUsers() throws SQLException {
        return userRepository.findAllUsers();
    }

    // Benutzer anhand des Tokens abrufen
    public User findUserByToken(String token) throws SQLException {
        return userRepository.findToken(token);
    }

    // Benutzer löschen
    public void deleteUser(int userId) throws SQLException {
        User user = userRepository.findId(userId);
        if (user != null) {
            userRepository.deleteUser(userId);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }



}