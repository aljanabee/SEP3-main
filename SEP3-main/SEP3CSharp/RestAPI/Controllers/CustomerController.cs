using Application.LogicInterfaces;
using Microsoft.AspNetCore.Mvc;
using Shared.Dtos;
using Shared.Exceptions;
using Shared.Models;

namespace RestAPI.Controllers;

[ApiController]
[Route("[controller]")]
public class CustomerController : ControllerBase
{
    private readonly ICustomerLogic _customerLogic;

    public CustomerController(ICustomerLogic customerLogic) {
        _customerLogic = customerLogic;
    }

    [HttpPost]
    public async Task<ActionResult<Customer>> CreateCustomerAsync(CustomerCreationDto dto)
    {
        try
        {
            Customer customer = await _customerLogic.CreateCustomerAsync(dto);
            return Created($"/Customer/{customer.Id}", customer);
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
    
    [HttpPatch]
    public async Task<ActionResult<Customer>> AlterCustomerAsync(Customer customer)
    {
        try
        {
            Customer returnCustomer = await _customerLogic.AlterCustomerAsync(customer);
            return Ok(returnCustomer);
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
    
    [HttpGet("{id}")]
    public async Task<ActionResult<Customer>> GetCustomerByIdAsync([FromRoute] long id) {
        try {
            Customer customer = await _customerLogic.GetCustomerByIdAsync(id);
            return Ok(customer);
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
    public async Task<ActionResult<IEnumerable<Customer>>> GetCustomersAsync() {
        try {
            IEnumerable<Customer> customer = await _customerLogic.GetCustomersAsync();
            return Ok(customer);
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
