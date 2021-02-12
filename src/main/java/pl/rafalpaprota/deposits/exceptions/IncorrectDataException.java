package pl.rafalpaprota.deposits.exceptions;


public class IncorrectDataException extends Exception {

    private static final String EXCEPTION_MESSAGE = "Cannot calculate future deposit.";

    public IncorrectDataException() {
        super(EXCEPTION_MESSAGE);
    }
}
