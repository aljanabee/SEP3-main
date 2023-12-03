using BlazorClient;
using BlazorClient.Auth;
using BlazorClient.Util;
using HttpClients.ClientImplementations;
using HttpClients.ClientIntefaces;
using HttpClients.ClientInterfaces;
using Microsoft.AspNetCore.Components.Authorization;
using Microsoft.AspNetCore.Components.Web;
using Microsoft.AspNetCore.Components.WebAssembly.Hosting;
using Radzen;
using Shared.Auth;

var builder = WebAssemblyHostBuilder.CreateDefault(args);
builder.RootComponents.Add<App>("#app");
builder.RootComponents.Add<HeadOutlet>("head::after");

// Authentication
builder.Services.AddScoped<AuthenticationStateProvider, CustomAuthProvider>();
builder.Services.AddScoped<IAuthService, JwtAuthService>();

// Http Services
builder.Services.AddScoped<IPingService, PingService>();
builder.Services.AddScoped<IWarehouseService, WarehouseService>();
builder.Services.AddScoped<IProductService, ProductService>();
builder.Services.AddScoped<ICustomerService, CustomerService>();
builder.Services.AddScoped<IOrderService, OrderService>();
builder.Services.AddScoped<IWarehouseProductService, WarehouseProductService>();
builder.Services.AddScoped<IWarehousePositionService, WarehousePositionService>();

// Util
builder.Services.AddScoped<INotifierService, NotifierService>();

//Radzen Services
builder.Services.AddScoped<DialogService>();
builder.Services.AddScoped<NotificationService>();
builder.Services.AddScoped<TooltipService>();
builder.Services.AddScoped<ContextMenuService>();

AuthorizationPolicies.AddPolicies(builder.Services);

builder.Services.AddScoped(_ => new HttpClient { BaseAddress = new Uri("http://localhost:5103") });

await builder.Build().RunAsync();
