using HttpClients.ClientInterfaces;
using System.Text.Json;

namespace HttpClients.ClientImplementations;
public class PingService : IPingService {
    private readonly HttpClient _client;

    public PingService(HttpClient client) { 
       this._client = client;
    }
    public async Task<long[]?> PingAsync() {
        HttpResponseMessage response = await _client.GetAsync("/Ping");
        string result = await response.Content.ReadAsStringAsync();

        if (!response.IsSuccessStatusCode) {
            throw new Exception(result);
        }

        long[]? dateTimes = JsonSerializer.Deserialize<long[]>(result, new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true
        });

        return dateTimes;
    }
}
