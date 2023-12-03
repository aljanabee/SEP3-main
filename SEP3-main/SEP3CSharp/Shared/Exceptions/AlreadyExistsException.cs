using System.Runtime.Serialization;

namespace Shared.Exceptions;

public class AlreadyExistsException : Exception {
	public AlreadyExistsException() {
	}

	public AlreadyExistsException(string message) : base(message) {
	}

	public AlreadyExistsException(string message, Exception inner) : base(message, inner) {
	}

	protected AlreadyExistsException(
		SerializationInfo info,
		StreamingContext context) : base(info, context) {
	}
}
