package exception;

public class ResponseException extends Exception {

    final private int statusCode;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int statusCode() {return statusCode;}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResponseException that=(ResponseException) o;

        return statusCode == that.statusCode;
    }

    @Override
    public int hashCode() {
        return statusCode;
    }

    public String messageToJSON() {
        return "{\"message\" : \"" + super.getMessage() + "\"}";
    }
}
