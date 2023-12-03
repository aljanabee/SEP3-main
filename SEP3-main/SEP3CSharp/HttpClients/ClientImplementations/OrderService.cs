using HttpClients.ClientIntefaces;
using Shared.Dtos;
using Shared.Models;
using System.Net.Http.Json;
using System.Text;
using System.Text.Json;

namespace HttpClients.ClientImplementations;

public class OrderService : IOrderService {
    private readonly HttpClient _httpClient;

    public OrderService(HttpClient httpClient) {
        _httpClient = httpClient;
    }

    public async Task<Order> CreateOrderAsync(OrderCreationDto dto) {
        HttpResponseMessage response = await _httpClient.PostAsJsonAsync("/order", dto);
        string content = await response.Content.ReadAsStringAsync();

        if (!response.IsSuccessStatusCode) {
            string result = await response.Content.ReadAsStringAsync();
            throw new Exception(result);
        }

        Order order = JsonSerializer.Deserialize<Order>(content, new JsonSerializerOptions {
            PropertyNameCaseInsensitive = true
        })!;
        return order;
    }

    public async Task UpdateOrderAsync(Order updatedOrder) {
        string updatedOrderAsJson = JsonSerializer.Serialize(updatedOrder);
        HttpContent httpContent = new StringContent(updatedOrderAsJson, Encoding.UTF8, "application/json");
        HttpResponseMessage response = await _httpClient.PutAsync("/order", httpContent);
        await response.Content.ReadAsStringAsync();

        if (!response.IsSuccessStatusCode) {
            string result = await response.Content.ReadAsStringAsync();
            throw new Exception(result);
        }
    }

    public async Task<Order> GetOrderByIdAsync(long id) {
        HttpResponseMessage response = await _httpClient.GetAsync($"/order/{id}");
        string content = await response.Content.ReadAsStringAsync();
        if (!response.IsSuccessStatusCode) {
            throw new Exception(content);
        }
        Order order = JsonSerializer.Deserialize<Order>(content, new JsonSerializerOptions {
            PropertyNameCaseInsensitive = true
        })!;
        return order;
    }

    public async Task<IEnumerable<Order>> GetOrdersAsync() {
        HttpResponseMessage response = await _httpClient.GetAsync("/order");
        string content = await response.Content.ReadAsStringAsync();
        if (!response.IsSuccessStatusCode) {
            throw new Exception(content);
        }
        IEnumerable<Order> orders = JsonSerializer.Deserialize<IEnumerable<Order>>(content, new JsonSerializerOptions {
            PropertyNameCaseInsensitive = true
        })!;
        return orders;
    }

    public async Task<IEnumerable<Order>> GetOrdersByWarehouseIdAsync(long id) {
        HttpResponseMessage response = await _httpClient.GetAsync($"/order/bywarehouseid/{id}");
        string content = await response.Content.ReadAsStringAsync();
        if (!response.IsSuccessStatusCode) {
            throw new Exception(content);
        }
        IEnumerable<Order> orders = JsonSerializer.Deserialize<IEnumerable<Order>>(content, new JsonSerializerOptions {
            PropertyNameCaseInsensitive = true
        })!;
        return orders;
    }
}
