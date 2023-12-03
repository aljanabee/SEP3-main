namespace Shared.Dtos; 

public class EmployeeCreationDto {
	public string FullName { get; set; } = null!;
	public string PhoneNo { get; set; } = null!;
    public string Address { get; set; } = null!;
    public string Mail { get; set; } = null!;
    public string Username { get; set; } = null!;
    public string Password { get; set; } = null!;
    public string Role { get; set; } = null!;
}