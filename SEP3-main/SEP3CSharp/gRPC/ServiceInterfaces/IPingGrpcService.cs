namespace gRPC.ServiceInterfaces; 

public interface IPingGrpcService {
	Task<PingResponse> pingAsync();
}