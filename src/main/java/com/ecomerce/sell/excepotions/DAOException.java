package com.ecomerce.sell.excepotions;

public class DAOException extends RuntimeException {

    private final DAOResponse error;

    public DAOException(DAOResponse error) {
        super(error.getMessage());
        this.error = error;
    }

    public String getCode() {
        return error.getCode();
    }

    public String getErrorMessage() {
        return error.getMessage();
    }

    public DAOResponse getError() {
        return error;
    }
}

