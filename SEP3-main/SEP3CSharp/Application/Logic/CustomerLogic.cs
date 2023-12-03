using Application.LogicInterfaces;
using gRPC.ServiceInterfaces;
using Shared.Dtos;
using Shared.Models;

namespace Application.Logic;

public class CustomerLogic : ICustomerLogic
{
    private readonly ICustomerGrpcService _customerService;
    public CustomerLogic(ICustomerGrpcService customerService) {
        this._customerService = customerService;
    }

    public async Task<Customer> CreateCustomerAsync(CustomerCreationDto dto)
    {
        Customer customer = await _customerService.CreateCustomerAsync(dto);
        return customer;
    }

    public async Task<Customer> AlterCustomerAsync(Customer customer) {
        Customer returnCustomer = await _customerService.AlterCustomerAsync(customer);
        return returnCustomer;
    }

    public async Task<Customer> GetCustomerByIdAsync(long id)
    {
        Customer customer = await _customerService.GetCustomerByIdAsync(id);
        return customer;
    }

    public async Task<IEnumerable<Customer>> GetCustomersAsync()
    {
        IEnumerable<Customer> customers = await _customerService.GetCustomersAsync();
        return customers;
    }
}
