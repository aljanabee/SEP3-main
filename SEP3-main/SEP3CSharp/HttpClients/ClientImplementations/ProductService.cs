using HttpClients.ClientIntefaces;
using Shared.Dtos;
using Shared.Models;
using System.Net.Http.Json;
using System.Text.Json;

namespace HttpClients.ClientImplementations;
public class ProductService : IProductService {
    private readonly HttpClient _httpClient;

    public ProductService(HttpClient httpClient) {
        _httpClient = httpClient;
    }

    public async Task<Product> CreateProductAsync(ProductCreationDto dto) {
        HttpResponseMessage response = await _httpClient.PostAsJsonAsync("/product", dto);
        string content = await response.Content.ReadAsStringAsync();

        if (!response.IsSuccessStatusCode) {
            string result = await response.Content.ReadAsStringAsync();
            throw new Exception(result);
        }

        Product product = JsonSerializer.Deserialize<Product>(content, new JsonSerializerOptions {
            PropertyNameCaseInsensitive = true
        })!;
        return product;
    }

    public async Task<IEnumerable<Product>> GetProductsAsync() {
        HttpResponseMessage response = await _httpClient.GetAsync("/product");
        string content = await response.Content.ReadAsStringAsync();
        if (!response.IsSuccessStatusCode) {
            throw new Exception(content);
        }
        IEnumerable<Product> products = JsonSerializer.Deserialize<IEnumerable<Product>>(content, new JsonSerializerOptions {
            PropertyNameCaseInsensitive = true
        })!;
        return products;
    }
}
