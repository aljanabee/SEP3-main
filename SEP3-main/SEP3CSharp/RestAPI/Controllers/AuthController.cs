using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using Application.LogicInterfaces;
using Shared.Dtos;
using Shared.Models;

namespace RestAPI.Controllers;

[ApiController]
[Route("[controller]")]
public class AuthController : ControllerBase
{
    private readonly IConfiguration _config;
    private readonly IAuthLogic _authLogic;

    public AuthController(IConfiguration config, IAuthLogic authLogic)
    {
        _config = config;
        _authLogic = authLogic;
    }
    
    [HttpPost, Route("login")]
    public async Task<ActionResult> Login([FromBody] EmployeeLoginDto employeeLoginDto)
    {
        try
        {
            Employee employee = await _authLogic.ValidateEmployee(employeeLoginDto.UserId, employeeLoginDto.Password);
            string token = GenerateJwt(employee);
    
            return Ok(token);
        }
        catch (Exception e)
        {
            return BadRequest(e.Message);
        }
    }
    
    private List<Claim> GenerateClaims(Employee employee)
    {
        var claims = new[]
        {
            new Claim(JwtRegisteredClaimNames.Sub, _config["Jwt:Subject"]!),
            new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString()),
            new Claim(JwtRegisteredClaimNames.Iat, DateTime.UtcNow.ToString()),
            new Claim(ClaimTypes.Name, employee.Username),
            new Claim(ClaimTypes.Role, employee.Role),
            new Claim("DisplayName", employee.FullName),
            new Claim("Email", employee.Mail),
            new Claim("Warehouse","1")
        };
        return claims.ToList();
    }
    
    private string GenerateJwt(Employee employee)
    {
        List<Claim> claims = GenerateClaims(employee);
    
        SymmetricSecurityKey key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_config["Jwt:Key"]!));
        SigningCredentials signIn = new SigningCredentials(key, SecurityAlgorithms.HmacSha512);
    
        JwtHeader header = new JwtHeader(signIn);
    
        JwtPayload payload = new JwtPayload(
            _config["Jwt:Issuer"],
            _config["Jwt:Audience"],
            claims, 
            null,
            DateTime.UtcNow.AddMinutes(60));
    
        JwtSecurityToken token = new JwtSecurityToken(header, payload);
    
        string serializedToken = new JwtSecurityTokenHandler().WriteToken(token);
        return serializedToken;
    }
}