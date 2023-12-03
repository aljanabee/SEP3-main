using gRPC.ServiceInterfaces;

namespace gRPC.ServiceImplementations; 

public class PingGrpcService : IPingGrpcService{
	private readonly Ping.PingClient _client;

	public PingGrpcService(Ping.PingClient client) {
		_client = client;
	}

	public async Task<PingResponse> pingAsync() {
		var reply = await _client.pingAsync(new PingRequest { DateTime = DateTimeOffset.Now.ToUnixTimeMilliseconds() });
		return reply;
	}
}