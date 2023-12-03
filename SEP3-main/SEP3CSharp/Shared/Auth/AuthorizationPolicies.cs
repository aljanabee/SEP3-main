using Microsoft.Extensions.DependencyInjection;

namespace Shared.Auth; 

public static class AuthorizationPolicies {
	public static void AddPolicies(IServiceCollection services) {
		services.AddAuthorizationCore(options => {
			options.AddPolicy("MustBeManager",a => 
				a.RequireAuthenticatedUser().RequireRole("Manager"));
			
			options.AddPolicy("MustBeOfWarehouseTeam",a => 
				a.RequireAuthenticatedUser().RequireRole("Warehouse"));
			
			options.AddPolicy("MustBeCashier",a => 
				a.RequireAuthenticatedUser().RequireRole("Cashier"));
			
			options.AddPolicy("MustBeSysAdmin",a => 
				a.RequireAuthenticatedUser().RequireRole("SysAdmin"));
		});
	}
}