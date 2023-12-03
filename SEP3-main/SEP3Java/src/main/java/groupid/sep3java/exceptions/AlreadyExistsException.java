package groupid.sep3java.exceptions;

public class AlreadyExistsException extends RuntimeException {
	public AlreadyExistsException() {
		super("Item Already exists");
	}

	public AlreadyExistsException(String message) {
		super(message);
	}
}
