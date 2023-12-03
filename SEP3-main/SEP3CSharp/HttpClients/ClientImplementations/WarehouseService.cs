using HttpClients.ClientIntefaces;
using Shared.Models;
using System.Text.Json;

namespace HttpClients.ClientImplementations;
public class WarehouseService : IWarehouseService {
    private readonly HttpClient _httpClient;

    public WarehouseService(HttpClient httpClient) {
        _httpClient = httpClient;
    }

    public async Task<Warehouse> GetWarehouseByIdAsync(long id) {
        HttpResponseMessage response = await _httpClient.GetAsync($"/warehouse/{id}");
        string content = await response.Content.ReadAsStringAsync();
        if (!response.IsSuccessStatusCode) {
            throw new Exception(content);
        }
        Warehouse warehouse = JsonSerializer.Deserialize<Warehouse>(content, new JsonSerializerOptions {
            PropertyNameCaseInsensitive = true
        })!;
        return warehouse;
    }

    public async Task<IEnumerable<Warehouse>> GetWarehousesAsync() {
        HttpResponseMessage response = await _httpClient.GetAsync("/warehouse");
        string content = await response.Content.ReadAsStringAsync();
        if (!response.IsSuccessStatusCode) {
            throw new Exception(content);
        }
        IEnumerable<Warehouse> warehouses = JsonSerializer.Deserialize<IEnumerable<Warehouse>>(content, new JsonSerializerOptions {
            PropertyNameCaseInsensitive = true
        })!;
        return warehouses;
    }
}
