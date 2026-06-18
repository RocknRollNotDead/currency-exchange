package ru.codeportfolio.exceptions;

import java.sql.SQLException;

public class DataAccessException extends RuntimeException{
    public DataAccessException(String message, SQLException e) {
        super(message, e);
    }

    public DataAccessException(SQLException e){
        super(e);
    }

    public DataAccessException(String message) {
        super(message);
    }
}
