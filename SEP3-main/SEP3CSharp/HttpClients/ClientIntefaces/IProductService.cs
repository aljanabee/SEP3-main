using Shared.Dtos;
using Shared.Models;

namespace HttpClients.ClientIntefaces;
public interface IProductService {
    Task<Product> CreateProductAsync(ProductCreationDto dto);
    Task<IEnumerable<Product>> GetProductsAsync();
}
