using gRPC;

namespace Application.LogicInterfaces; 

public interface IPingLogic {
	Task<PingResponse> PingAsync();
}