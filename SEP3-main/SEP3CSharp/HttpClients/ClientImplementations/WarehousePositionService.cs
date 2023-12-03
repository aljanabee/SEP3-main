using System.Net.Http.Json;
using System.Text.Json;
using HttpClients.ClientIntefaces;
using Shared.Models;

namespace HttpClients.ClientImplementations; 

public class WarehousePositionService : IWarehousePositionService {
	private readonly HttpClient _httpClient;

	public WarehousePositionService(HttpClient httpClient) {
		_httpClient = httpClient;
	}

	public async Task<WarehousePosition> CreateAsync(string position) {
		HttpResponseMessage response = await _httpClient.PostAsJsonAsync("/warehousePosition", position);
		string content = await response.Content.ReadAsStringAsync();

		if (!response.IsSuccessStatusCode) {
			string result = await response.Content.ReadAsStringAsync();
			throw new Exception(result);
		}

		WarehousePosition warehousePosition = JsonSerializer.Deserialize<WarehousePosition>(content,
			new JsonSerializerOptions {
				PropertyNameCaseInsensitive = true
			})!;
		return warehousePosition;
	}

	public async Task<WarehousePosition> GetByIdAsync(long id) {
		HttpResponseMessage response = await _httpClient.GetAsync($"/warehousePosition/{id}");
		string content = await response.Content.ReadAsStringAsync();

		if (!response.IsSuccessStatusCode) {
			string result = await response.Content.ReadAsStringAsync();
			throw new Exception(result);
		}

		WarehousePosition warehousePosition = JsonSerializer.Deserialize<WarehousePosition>(content,
			new JsonSerializerOptions {
				PropertyNameCaseInsensitive = true
			})!;
		return warehousePosition;
	}

	public async Task<IEnumerable<WarehousePosition>> GetAsync() {
		HttpResponseMessage response = await _httpClient.GetAsync("/warehousePosition");
		string content = await response.Content.ReadAsStringAsync();

		if (!response.IsSuccessStatusCode) {
			string result = await response.Content.ReadAsStringAsync();
			throw new Exception(result);
		}

		IEnumerable<WarehousePosition> warehousePosition = JsonSerializer.Deserialize<IEnumerable<WarehousePosition>>(content,
			new JsonSerializerOptions {
				PropertyNameCaseInsensitive = true
			})!;
		return warehousePosition;
	}
}