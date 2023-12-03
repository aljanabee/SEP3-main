using Application.DaoInterfaces;
using Application.LogicInterfaces;
using Shared.Models;

namespace Application.Logic;

public class AuthLogic : IAuthLogic {
    private readonly IEmployeeDao _employeeDao;

    public AuthLogic(IEmployeeDao employeeDao) {
        _employeeDao = employeeDao;
    }

    public async Task<Employee> ValidateEmployee(string username, string password) {
        Employee? employee = await _employeeDao.GetByUsernameAsync(username);
        if (employee == null) {
            throw new Exception("User not found");
        }

        if (!employee.Password.Equals(password)) {
            throw new Exception("Password mismatch");
        }
        
        return employee;
    }
}