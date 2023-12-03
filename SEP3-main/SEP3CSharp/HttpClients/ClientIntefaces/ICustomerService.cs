using Shared.Dtos;
using Shared.Models;

namespace HttpClients.ClientIntefaces;

public interface ICustomerService
{
    Task<Customer> CreateCustomerAsync(CustomerCreationDto dto);
    Task<Customer> AlterCustomerAsync(Customer customer);
    Task<Customer> GetCustomerByIdAsync(long id);
    Task<IEnumerable<Customer>> GetCustomersAsync();
    
}