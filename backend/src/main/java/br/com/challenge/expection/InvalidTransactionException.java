package br.com.challenge.expection;

public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(String message){
        super(message);
    }
}
