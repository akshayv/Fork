
class ForkException extends Exception {

    private ErrorCode errorCode;

    public ForkException() {
    }

    public ForkException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
