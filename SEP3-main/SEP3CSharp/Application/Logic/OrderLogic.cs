using Application.LogicInterfaces;
using gRPC.ServiceInterfaces;
using Shared.Dtos;
using Shared.Models;

namespace Application.Logic;
public class OrderLogic : IOrderLogic {
    private readonly IOrderGrpcService _orderService;

    public OrderLogic(IOrderGrpcService orderService) {
        _orderService = orderService;
    }

    public async Task<Order> CreateOrderAsync(OrderCreationDto dto) {
        Order order = await _orderService.CreateOrderAsync(dto);
        return order;
    }

    public async Task<Order> GetOrderByIdAsync(long id) {
        Order order = await _orderService.GetOrderByIdAsync(id);
        return order;
    }

    public async Task<IEnumerable<Order>> GetOrdersAsync() {
        IEnumerable<Order> orders = await _orderService.GetOrdersAsync();
        return orders;
    }

    public async Task<IEnumerable<Order>> GetOrdersByWarehouseIdAsync(long id) {
        IEnumerable<Order> orders = await _orderService.GetOrdersByWarehouseIdAsync(id);
        return orders;
    }

    public async Task<string> UpdateOrderAsync(Order updatedOrder) {
        string responseMessage = await _orderService.UpdateOrderAsync(updatedOrder);
        return responseMessage;
    }
}
