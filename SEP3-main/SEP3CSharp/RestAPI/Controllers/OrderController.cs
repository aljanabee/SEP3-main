using Application.LogicInterfaces;
using Microsoft.AspNetCore.Mvc;
using Shared.Dtos;
using Shared.Exceptions;
using Shared.Models;

namespace RestAPI.Controllers;

[ApiController]
[Route("[controller]")]
public class OrderController : ControllerBase {

    private readonly IOrderLogic _orderLogic;

    public OrderController(IOrderLogic orderLogic) {
        _orderLogic = orderLogic;
    }

    [HttpPost]
    public async Task<ActionResult<Order>> CreateOrderAsync(OrderCreationDto dto) {
        try {
            Order order = await _orderLogic.CreateOrderAsync(dto);
            return Created($"/order/{order.Id}", order);
        }
        catch (NotFoundException e) {
            Console.WriteLine(e.Message);
            return NotFound(e.Message);
        }
        catch (InsufficientStockException e) {
            Console.WriteLine(e.Message);
            return StatusCode(412, e.Message);
        }
        catch (ServiceUnavailableException e) {
            Console.WriteLine(e);
            return StatusCode(503, e.Message);
        }
        catch (Exception e) {
            Console.WriteLine(e);
            return StatusCode(500, e.Message);
        }
    }

    [HttpPut]
    public async Task<ActionResult<Order>> UpdateOrderAsync(Order updatedOrder) {
        try {
            string responseMessage = await _orderLogic.UpdateOrderAsync(updatedOrder);
            return Ok(responseMessage);
        }
        catch (NotFoundException e) {
            Console.WriteLine(e.Message);
            return NotFound(e.Message);
        }
        catch (ServiceUnavailableException e) {
            Console.WriteLine(e);
            return StatusCode(503, e.Message);
        }
        catch (Exception e) {
            Console.WriteLine(e);
            return StatusCode(500, e.Message);
        }
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<Order>>> GetOrdersAsync() {
        try {
            IEnumerable<Order> order = await _orderLogic.GetOrdersAsync();
            return Ok(order);
        }
        catch (ServiceUnavailableException e) {
            Console.WriteLine(e);
            return StatusCode(503, e.Message);
        }
        catch (Exception e) {
            Console.WriteLine(e);
            return StatusCode(500, e.Message);
        }
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<Order>> GetOrderByIdAsync([FromRoute] long id) {
        try {
            Order order = await _orderLogic.GetOrderByIdAsync(id);
            return Ok(order);
        }
        catch (NotFoundException e) {
            Console.WriteLine(e.Message);
            return NotFound(e.Message);
        }
        catch (ServiceUnavailableException e) {
            Console.WriteLine(e);
            return StatusCode(503, e.Message);
        }
        catch (Exception e) {
            Console.WriteLine(e);
            return StatusCode(500, e.Message);
        }
    }

    [HttpGet("bywarehouseid/{id}")]
    public async Task<ActionResult<IEnumerable<Order>>> GetOrdersByWarehouseIdAsync([FromRoute] long id) {
        try {
            IEnumerable<Order> orders = await _orderLogic.GetOrdersByWarehouseIdAsync(id);
            return Ok(orders);
        }
        catch (NotFoundException e) {
            Console.WriteLine(e.Message);
            return NotFound(e.Message);
        }
        catch (ServiceUnavailableException e) {
            Console.WriteLine(e);
            return StatusCode(503, e.Message);
        }
        catch (Exception e) {
            Console.WriteLine(e);
            return StatusCode(500, e.Message);
        }
    }
}
