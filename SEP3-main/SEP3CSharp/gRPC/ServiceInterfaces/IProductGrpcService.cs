using Shared.Dtos;
using Shared.Models;

namespace gRPC.ServiceInterfaces;
public interface IProductGrpcService {
    Task<Product> CreateProductAsync(ProductCreationDto dto);
    Task<IEnumerable<Product>> GetProductsAsync();
    Task<Product> GetProductByIdAsync(long id);
}