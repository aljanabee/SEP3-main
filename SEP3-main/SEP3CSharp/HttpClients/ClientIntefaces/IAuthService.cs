using System.Security.Claims;
using Shared.Models;

namespace HttpClients.ClientInterfaces; 

public interface IAuthService {
	public Task LoginAsync(string username, string password);
	public Task LogoutAsync();
	public Task RegisterAsync(Employee employee);
	public Task<ClaimsPrincipal> GetAuthAsync();
	public Action<ClaimsPrincipal> OnAuthStateChanged { get; set; }
}