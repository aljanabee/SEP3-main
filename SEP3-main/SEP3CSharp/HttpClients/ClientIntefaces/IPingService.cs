namespace HttpClients.ClientInterfaces;
public interface IPingService {
    Task<long[]?> PingAsync();
}
