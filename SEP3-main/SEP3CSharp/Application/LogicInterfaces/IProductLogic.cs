using Shared.Dtos;
using Shared.Models;

namespace Application.LogicInterfaces;
public interface IProductLogic {
    Task<Product> CreateProductAsync(ProductCreationDto dto);
    Task<Product> GetProductByIdAsync(long id);
    Task<IEnumerable<Product>> GetProductsAsync();
}
