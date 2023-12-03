namespace Shared.Exceptions;

public class InsufficientStockException : Exception {
    public InsufficientStockException() : base("There was insufficient stock on a product") {
    }

    public InsufficientStockException(string? message) : base(message) {
    }

    public InsufficientStockException(string? message, Exception? innerException) : base(message, innerException) {
    }
}
