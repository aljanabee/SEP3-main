using Application.LogicInterfaces;
using gRPC.ServiceInterfaces;
using Shared.Dtos;
using Shared.Models;

namespace Application.Logic;
public class ProductLogic : IProductLogic {
    private readonly IProductGrpcService _productService;

    public ProductLogic(IProductGrpcService productService) {
        this._productService = productService;
    }

    public async Task<Product> CreateProductAsync(ProductCreationDto dto) {
        Product product = await _productService.CreateProductAsync(dto);
        return product;
    }

    public async Task<Product> GetProductByIdAsync(long id) {
        Product product = await _productService.GetProductByIdAsync(id);
        return product;
    }

    public async Task<IEnumerable<Product>> GetProductsAsync() {
        IEnumerable<Product> products = await _productService.GetProductsAsync();
        return products;
    }
}
