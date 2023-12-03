using Application.LogicInterfaces;
using Microsoft.AspNetCore.Mvc;
using Shared.Dtos;
using Shared.Exceptions;
using Shared.Models;

namespace RestAPI.Controllers;

[ApiController]
[Route("[controller]")]
public class ProductController : ControllerBase {
    private readonly IProductLogic _productLogic;

    public ProductController(IProductLogic productLogic) {
        _productLogic = productLogic;
    }

    [HttpPost]
    public async Task<ActionResult<Product>> CreateProductAsync(ProductCreationDto dto) {
        try {
            Product product = await _productLogic.CreateProductAsync(dto);
            return Created($"/product/{product.Id}", product);
        }
        catch (ServiceUnavailableException e) {
            Console.WriteLine(e);
            return StatusCode(503, e.Message);
        }
        catch (Exception e) {
            Console.WriteLine(e);
            return StatusCode(500, e.Message);
        }
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<Product>> GetProductByIdAsync([FromRoute] long id) {
        try {
            Product product = await _productLogic.GetProductByIdAsync(id);
            return Ok(product);
        }
        catch (NotFoundException e) {
            Console.WriteLine(e.Message);
            return NotFound(e.Message);
        }
        catch (ServiceUnavailableException e) {
            Console.WriteLine(e);
            return StatusCode(503, e.Message);
        }
        catch (Exception e) {
            Console.WriteLine(e);
            return StatusCode(500, e.Message);
        }
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<Product>>> GetProductsAsync() {
        try {
            IEnumerable<Product> product = await _productLogic.GetProductsAsync();
            return Ok(product);
        }
        catch (ServiceUnavailableException e) {
            Console.WriteLine(e);
            return StatusCode(503, e.Message);
        }
        catch (Exception e) {
            Console.WriteLine(e);
            return StatusCode(500, e.Message);
        }
    }
}
