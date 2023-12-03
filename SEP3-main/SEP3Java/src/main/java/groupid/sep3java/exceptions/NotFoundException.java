package groupid.sep3java.exceptions;

public class NotFoundException extends RuntimeException {
	public NotFoundException() {
		super("Item could not be found");
	}

	public NotFoundException(String message) {
		super(message);
	}
}
