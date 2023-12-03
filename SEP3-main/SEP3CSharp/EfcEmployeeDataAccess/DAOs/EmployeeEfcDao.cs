using Application.DaoInterfaces;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.ChangeTracking;
using Shared.Dtos;
using Shared.Models;

namespace EfcEmployeeDataAccess.DAOs; 

public class EmployeeEfcDao : IEmployeeDao{
	private readonly DataContext _context;

	public EmployeeEfcDao(DataContext context) {
		_context = context;
	}

	public async Task<Employee> CreateAsync(EmployeeCreationDto employeeDto) {
		Employee employee = new Employee() {
			Address = employeeDto.Address,
			FullName = employeeDto.FullName,
			Mail = employeeDto.Mail,
			Password = employeeDto.Password,
			PhoneNo = employeeDto.PhoneNo,
			Role = employeeDto.Role,
			Username = employeeDto.Username
		};
		
		EntityEntry<Employee> newEmployee = await _context.Employees.AddAsync(employee);
		await _context.SaveChangesAsync();
		return newEmployee.Entity;
	}

	public async Task<Employee?> GetByUsernameAsync(string userName) {
		Employee? existing = await _context.Employees.FirstOrDefaultAsync(u =>
			u.Username.ToLower().Equals(userName.ToLower())
		);

		return existing;
	}
}