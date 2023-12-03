using Shared.Dtos;
using Shared.Models;

namespace HttpClients.ClientIntefaces;

public interface IOrderService {
    Task<Order> CreateOrderAsync(OrderCreationDto dto);
    Task UpdateOrderAsync(Order updatedOrder);
    Task<IEnumerable<Order>> GetOrdersAsync();
    Task<IEnumerable<Order>> GetOrdersByWarehouseIdAsync(long id);
    Task<Order> GetOrderByIdAsync(long id);
}
