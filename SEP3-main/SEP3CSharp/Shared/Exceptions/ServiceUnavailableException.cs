namespace Shared.Exceptions;

public class ServiceUnavailableException : Exception {
    public ServiceUnavailableException() : base("The service is unavailable") {
    }

    public ServiceUnavailableException(string? message) : base(message) {
    }

    public ServiceUnavailableException(string? message, Exception? innerException) : base(message, innerException) {
    }
}