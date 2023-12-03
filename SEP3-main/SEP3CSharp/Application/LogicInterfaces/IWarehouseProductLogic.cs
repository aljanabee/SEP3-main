using Shared.Dtos;
using Shared.Models;

namespace Application.LogicInterfaces;
public interface IWarehouseProductLogic {
    Task<WarehouseProduct> CreateWarehouseProduct(WarehouseProductCreationDto dto);
    Task<WarehouseProduct> AlterWarehouseProduct(WarehouseProductCreationDto dto);
    Task<WarehouseProduct> GetWarehouseProductByIdAsync(long productId, long warehouseId);
    Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsAsync();
    Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsByProductIdAsync(long id);
    Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsByWarehouseIdAsync(long id);
}
