package at.fhtw.monsterTGame.persistence;

public class DataAccessException extends RuntimeException {

    public DataAccessException(String errorMessage) {
        super(errorMessage);
    }

    public DataAccessException(String errorMessage, Throwable rootCause) {
        super(errorMessage, rootCause);
    }

    public DataAccessException(Throwable rootCause) {
        super(rootCause);
    }
}
