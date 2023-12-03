using Shared.Dtos;
using Shared.Models;

namespace Application.LogicInterfaces;

public interface ICustomerLogic
{
    Task<Customer> CreateCustomerAsync(CustomerCreationDto dto);
    Task<Customer> AlterCustomerAsync(Customer customer);
    Task<Customer> GetCustomerByIdAsync(long id);
    Task<IEnumerable<Customer>> GetCustomersAsync();
}