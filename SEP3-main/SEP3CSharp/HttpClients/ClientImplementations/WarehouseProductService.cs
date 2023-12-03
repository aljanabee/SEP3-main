using System.Net.Http.Json;
using System.Text;
using System.Text.Json;
using HttpClients.ClientIntefaces;
using Shared.Dtos;
using Shared.Models;

namespace HttpClients.ClientImplementations; 

public class WarehouseProductService : IWarehouseProductService {
	private readonly HttpClient _httpClient;

	public WarehouseProductService(HttpClient httpClient) {
		_httpClient = httpClient;
	}

	public async Task<WarehouseProduct> CreateWarehouseProductAsync(WarehouseProductCreationDto dto) {
		HttpResponseMessage response = await _httpClient.PostAsJsonAsync("/warehouseProduct", dto);
		string content = await response.Content.ReadAsStringAsync();
		
		if (!response.IsSuccessStatusCode) {
			string result = await response.Content.ReadAsStringAsync();
			throw new Exception(result);
		}

		WarehouseProduct warehouseProduct = JsonSerializer.Deserialize<WarehouseProduct>(content,
			new JsonSerializerOptions {
				PropertyNameCaseInsensitive = true
			})!;
		return warehouseProduct;
	}

	public async Task<WarehouseProduct> AlterWarehouseProductAsync(WarehouseProductCreationDto dto) {
		string serialized = JsonSerializer.Serialize(dto);
		HttpContent httpContent = new StringContent(serialized,Encoding.UTF8,"application/json");
		HttpResponseMessage response = await _httpClient.PatchAsync("/warehouseProduct", httpContent);
		string content = await response.Content.ReadAsStringAsync();
		
		if (!response.IsSuccessStatusCode) {
			string result = await response.Content.ReadAsStringAsync();
			throw new Exception(result);
		}

		WarehouseProduct warehouseProduct = JsonSerializer.Deserialize<WarehouseProduct>(content,
			new JsonSerializerOptions {
				PropertyNameCaseInsensitive = true
			})!;
		return warehouseProduct;
	}

	public async Task<WarehouseProduct> GetWarehouseProductById(long productId, long warehouseId) {
		HttpResponseMessage response = await _httpClient.GetAsync($"/warehouseProduct/byid?productId={productId}&warehouseId={warehouseId}");
		string content = await response.Content.ReadAsStringAsync();

		if (!response.IsSuccessStatusCode) {
			string result = await response.Content.ReadAsStringAsync();
			throw new Exception(result);
		}

		WarehouseProduct warehouseProduct = JsonSerializer.Deserialize<WarehouseProduct>(content,
			new JsonSerializerOptions {
				PropertyNameCaseInsensitive = true
			})!;
		return warehouseProduct;
	}

	public async Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsAsync() {
		HttpResponseMessage response = await _httpClient.GetAsync("/warehouseProduct");
		string content = await response.Content.ReadAsStringAsync();

		if (!response.IsSuccessStatusCode) {
			string result = await response.Content.ReadAsStringAsync();
			throw new Exception(result);
		}
		
		IEnumerable<WarehouseProduct> warehouseProducts = JsonSerializer.Deserialize<IEnumerable<WarehouseProduct>>(content,
			new JsonSerializerOptions {
				PropertyNameCaseInsensitive = true
			})!;
		return warehouseProducts;
	}

	public async Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsByProductAsync(long id) {
		HttpResponseMessage response = await _httpClient.GetAsync($"/warehouseProduct/byproductid/{id}");
		string content = await response.Content.ReadAsStringAsync();

		if (!response.IsSuccessStatusCode) {
			string result = await response.Content.ReadAsStringAsync();
			throw new Exception(result);
		}
		
		IEnumerable<WarehouseProduct> warehouseProducts = JsonSerializer.Deserialize<IEnumerable<WarehouseProduct>>(content,
			new JsonSerializerOptions {
				PropertyNameCaseInsensitive = true
			})!;
		return warehouseProducts;
	}
	
	public async Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsByWarehouseAsync(long id) {
		HttpResponseMessage response = await _httpClient.GetAsync($"/warehouseProduct/bywarehouseid/{id}");
		string content = await response.Content.ReadAsStringAsync();

		if (!response.IsSuccessStatusCode) {
			string result = await response.Content.ReadAsStringAsync();
			throw new Exception(result);
		}
		
		IEnumerable<WarehouseProduct> warehouseProducts = JsonSerializer.Deserialize<IEnumerable<WarehouseProduct>>(content,
			new JsonSerializerOptions {
				PropertyNameCaseInsensitive = true
			})!;
		return warehouseProducts;
	}
}