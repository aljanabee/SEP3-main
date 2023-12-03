using System.Text;
using Grpc.Core;
using gRPC.ServiceInterfaces;
using Shared.Dtos;
using Shared.Exceptions;
using Shared.Models;

namespace gRPC.ServiceImplementations;

public class WarehouseProductGrpcService : IWarehouseProductGrpcService
{
    private readonly gRPC.WarehouseProductGrpcService.WarehouseProductGrpcServiceClient _warehouseProductGrpcServiceClient;

    public WarehouseProductGrpcService(gRPC.WarehouseProductGrpcService.WarehouseProductGrpcServiceClient warehouseProductGrpcServiceClient)
    {
        _warehouseProductGrpcServiceClient = warehouseProductGrpcServiceClient;
    }

   

    public async Task<WarehouseProduct> CreateWarehouseProductAsync(WarehouseProductCreationDto dto)
    {
        try {
            WarehouseProductResponse reply = await _warehouseProductGrpcServiceClient.CreateWarehouseProductAsync(
                new CreateWarehouseProductRequest()
                {
                    WarehouseId = dto.WarehouseId,
                    WarehousePosition = dto.WarehousePosition,
                    MinimumQuantity = dto.MinimumQuantity,
                    Quantity = dto.Quantity,
                    ProductId = dto.ProductId
                });

            WarehouseProduct warehouseProduct = new WarehouseProduct()
            {
                Warehouse = new Warehouse {
                    Id = reply.Warehouse.WarehouseId,
                    Name = reply.Warehouse.Name,
                    Address = reply.Warehouse.Address
                },
                Product = new Product {
                  Id = reply.Product.Id,
                  Name = reply.Product.Name,
                  Description = reply.Product.Description,
                  Price = reply.Product.Price
                },
                WarehousePosition = reply.WarehousePosition,
                MinimumQuantity = reply.MinimumQuantity,
                Quantity = reply.Quantity
            };
            return warehouseProduct;
        }
        catch (RpcException e) {
            if (e.StatusCode == StatusCode.Unavailable) {
                throw new ServiceUnavailableException();
            }
            if (e.StatusCode == StatusCode.NotFound) {
                var trailer = e.Trailers.Get("grpc.reflection.v1alpha.errorresponse-bin")!;
                throw new NotFoundException(e.Status.Detail + "\nDetails: " + Encoding.UTF8.GetString(trailer.ValueBytes).Substring(2));
            }

            if (e.StatusCode == StatusCode.AlreadyExists) {
                throw new AlreadyExistsException(e.Status.Detail);
            }
            throw;
        }
    }

    public async Task<WarehouseProduct> AlterWarehouseProductAsync(WarehouseProductCreationDto dto) {
        try {
            WarehouseProductResponse reply = await _warehouseProductGrpcServiceClient.AlterWarehouseProductAsync(
                new CreateWarehouseProductRequest()
                {
                    WarehouseId = dto.WarehouseId,
                    ProductId = dto.ProductId,
                    WarehousePosition = dto.WarehousePosition,
                    MinimumQuantity = dto.MinimumQuantity,
                    Quantity = dto.Quantity
                });

            WarehouseProduct warehouseProduct = new WarehouseProduct()
            {
                Warehouse = new Warehouse {
                    Id = reply.Warehouse.WarehouseId,
                    Name = reply.Warehouse.Name,
                    Address = reply.Warehouse.Address
                },
                Product = new Product {
                    Id = reply.Product.Id,
                    Name = reply.Product.Name,
                    Description = reply.Product.Description,
                    Price = reply.Product.Price
                },
                WarehousePosition = reply.WarehousePosition,
                MinimumQuantity = reply.MinimumQuantity,
                Quantity = reply.Quantity
            };
            return warehouseProduct;
        }
        catch (RpcException e) {
            if (e.StatusCode == StatusCode.Unavailable) {
                throw new ServiceUnavailableException();
            }
            if (e.StatusCode == StatusCode.NotFound) {
                var trailer = e.Trailers.Get("grpc.reflection.v1alpha.errorresponse-bin")!;
                throw new NotFoundException(e.Status.Detail + "\nDetails: " + Encoding.UTF8.GetString(trailer.ValueBytes).Substring(2));
            }
            throw;
        }
    }

    public async Task<WarehouseProduct> GetWarehouseProductByIdAsync(long productId, long warehouseId)
    {
        try {
            WarehouseProductResponse reply =
                await _warehouseProductGrpcServiceClient.GetWarehouseProductAsync(new GetWarehouseProductRequest
                    { ProductId = productId, WarehouseId = warehouseId });

            WarehouseProduct warehouseProduct = new WarehouseProduct
            {
                Warehouse = new Warehouse {
                    Id = reply.Warehouse.WarehouseId,
                    Name = reply.Warehouse.Name,
                    Address = reply.Warehouse.Address
                },
                Product = new Product {
                    Id = reply.Product.Id,
                    Name = reply.Product.Name,
                    Description = reply.Product.Description,
                    Price = reply.Product.Price
                },
                WarehousePosition = reply.WarehousePosition,
                MinimumQuantity = reply.MinimumQuantity,
                Quantity = reply.Quantity
            
            };
            return warehouseProduct;
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
    

    public async Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsAsync()
    {
        try {
            GetWarehouseProductsResponse reply = await _warehouseProductGrpcServiceClient.GetWarehouseProductsAsync(new GetWarehouseProductsRequest());
       
        
            List<WarehouseProduct> warehouseProducts = new();
            foreach (WarehouseProductResponse pr in reply.WarehouseProducts)
            {
                warehouseProducts.Add(new WarehouseProduct()
                {
                    Warehouse = new Warehouse {
                        Id = pr.Warehouse.WarehouseId,
                        Name = pr.Warehouse.Name,
                        Address = pr.Warehouse.Address
                    },
                    Product = new Product {
                        Id = pr.Product.Id,
                        Name = pr.Product.Name,
                        Description = pr.Product.Description,
                        Price = pr.Product.Price
                    },
                    WarehousePosition = pr.WarehousePosition,
                    MinimumQuantity = pr.MinimumQuantity,
                    Quantity = pr.Quantity
                });
            }

            return warehouseProducts.AsEnumerable();
        }
        catch (RpcException e) {
            if (e.StatusCode == StatusCode.Unavailable) {
                throw new ServiceUnavailableException();
            }
            
            throw;
        }
    }

    public async Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsByProductIdAsync(long id)
    {
        try
        {
            GetWarehouseProductsResponse reply =
                await _warehouseProductGrpcServiceClient.GetWarehouseProductByProductIdAsync(
                    new QueryByPartialIdRequest{Id = id});
            List<WarehouseProduct> warehouseProducts = new();
            foreach (WarehouseProductResponse pr in reply.WarehouseProducts)
            {
                warehouseProducts.Add(new WarehouseProduct()
                {

                    Warehouse = new Warehouse {
                        Id = pr.Warehouse.WarehouseId,
                        Name = pr.Warehouse.Name,
                        Address = pr.Warehouse.Address
                    },
                    Product = new Product {
                        Id = pr.Product.Id,
                        Name = pr.Product.Name,
                        Description = pr.Product.Description,
                        Price = pr.Product.Price
                    },
                    
                    WarehousePosition = pr.WarehousePosition,
                    MinimumQuantity = pr.MinimumQuantity,
                    Quantity = pr.Quantity,
                });
            }

            return warehouseProducts.AsEnumerable();
        }
        catch (RpcException e)
        {
            if (e.StatusCode == StatusCode.Unavailable)
            {
                throw new ServiceUnavailableException();
            }
            if (e.StatusCode == StatusCode.NotFound) {
                var trailer = e.Trailers.Get("grpc.reflection.v1alpha.errorresponse-bin")!;
                throw new NotFoundException(e.Status.Detail + "\nDetails: " + Encoding.UTF8.GetString(trailer.ValueBytes).Substring(2));
            }
            throw;
        }
    }

    public async Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsByWarehouseIdAsync(long id)
    {
        try
        {
            GetWarehouseProductsResponse reply = await _warehouseProductGrpcServiceClient.GetWarehouseProductByWarehouseIdAsync(new QueryByPartialIdRequest {Id = id});
            List<WarehouseProduct> warehouseProducts = new();
            foreach (WarehouseProductResponse pr in reply.WarehouseProducts)
            {
                warehouseProducts.Add(new WarehouseProduct()
                {

                    WarehousePosition = pr.WarehousePosition,
                    MinimumQuantity = pr.MinimumQuantity,
                  
                    Warehouse = new Warehouse {
                        Id = pr.Warehouse.WarehouseId,
                        Name = pr.Warehouse.Name,
                        Address = pr.Warehouse.Address
                    },
                    Product = new Product {
                        Id = pr.Product.Id,
                        Name = pr.Product.Name,
                        Description = pr.Product.Description,
                        Price = pr.Product.Price
                    },
                    Quantity = pr.Quantity,
                });
            }

            return warehouseProducts.AsEnumerable();
        }
        catch (RpcException e)
        {
            if (e.StatusCode == StatusCode.Unavailable)
            {
                throw new ServiceUnavailableException();
            }
            if (e.StatusCode == StatusCode.NotFound) {
                var trailer = e.Trailers.Get("grpc.reflection.v1alpha.errorresponse-bin")!;
                throw new NotFoundException(e.Status.Detail + "\nDetails: " + Encoding.UTF8.GetString(trailer.ValueBytes).Substring(2));
            }
            throw;
        }
    }
}
