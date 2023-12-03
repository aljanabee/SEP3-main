using Shared.Dtos;
using Shared.Models;

namespace gRPC.ServiceInterfaces;

public interface IWarehouseProductGrpcService
{
    Task<WarehouseProduct> CreateWarehouseProductAsync(WarehouseProductCreationDto dto);
    Task<WarehouseProduct> AlterWarehouseProductAsync(WarehouseProductCreationDto dto);
    Task<WarehouseProduct> GetWarehouseProductByIdAsync(long productId, long warehouseId);
    Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsAsync();
    Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsByProductIdAsync(long id);
    Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsByWarehouseIdAsync(long id);
}   