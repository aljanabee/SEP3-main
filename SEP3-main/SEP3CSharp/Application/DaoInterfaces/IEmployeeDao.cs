using Shared.Dtos;
using Shared.Models;

namespace Application.DaoInterfaces; 

public interface IEmployeeDao {
	Task<Employee> CreateAsync(EmployeeCreationDto employeeDto);
	Task<Employee?> GetByUsernameAsync(string userName);	
}