using gRPC.ServiceInterfaces;
using Grpc.Core;
using Shared.Dtos;
using Shared.Exceptions;
using Shared.Models;

namespace gRPC.ServiceImplementations;
public class ProductGrpcService : IProductGrpcService {

    private readonly gRPC.ProductGrpcService.ProductGrpcServiceClient _serviceClient;

    public ProductGrpcService(gRPC.ProductGrpcService.ProductGrpcServiceClient grpcServiceClient) {
        _serviceClient = grpcServiceClient;
    }

    public async Task<Product> CreateProductAsync(ProductCreationDto dto) {
        try {
            ProductResponse reply = await _serviceClient.CreateProductAsync(new CreateProductRequest() {
                Name = dto.Name,
                Description = dto.Description,
                Price = dto.Price
            });

            Product product = new Product {
                Id = reply.Id,
                Name = reply.Name,
                Description = reply.Description,
                Price = reply.Price
            };
            return product;
        }
        catch (RpcException e) {
            if (e.StatusCode == StatusCode.Unavailable) {
                throw new ServiceUnavailableException();
            }
            throw;
        }
    }

    public async Task<Product> GetProductByIdAsync(long id) {
        try {
            ProductResponse reply = await _serviceClient.GetProductAsync(new GetProductRequest { Id = id});

            Product product = new Product {
                Id = reply.Id,
                Name = reply.Name,
                Description = reply.Description,
                Price = reply.Price
            };
            return product;
        }
        catch (RpcException e) {
            if (e.StatusCode == StatusCode.Unavailable) {
                throw new ServiceUnavailableException();
            }
            if (e.StatusCode == StatusCode.NotFound) {
                throw new NotFoundException(e.Status.Detail);
            }
            throw;
        }
    }

    public async Task<IEnumerable<Product>> GetProductsAsync() {
        try {
            GetProductsResponse reply = await _serviceClient.GetProductsAsync(new GetProductsRequest());

            List<Product> products = new();
            foreach (ProductResponse pr in reply.Products) {
                products.Add(new Product() {
                    Id = pr.Id,
                    Name = pr.Name,
                    Description = pr.Description,
                    Price = pr.Price
                });
            }

            return products.AsEnumerable();
        }
        catch (RpcException e) {
            if (e.StatusCode == StatusCode.Unavailable) {
                throw new ServiceUnavailableException();
            }
            throw;
        }
    }
}