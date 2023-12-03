using Shared.Models;

namespace Application.LogicInterfaces;

public interface IAuthLogic
{
    Task<Employee> ValidateEmployee(string username, string password);
}