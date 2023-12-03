using Application.LogicInterfaces;
using gRPC;
using gRPC.ServiceInterfaces;

namespace Application.Logic; 

public class PingLogic : IPingLogic {
	private IPingGrpcService _client;

	public PingLogic(IPingGrpcService client) {
		_client = client;
	}

	public async Task<PingResponse> PingAsync() {
		var reply = await _client.pingAsync();
		return reply;
	}
}