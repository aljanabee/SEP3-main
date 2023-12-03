using Shared.Dtos;
using Shared.Models;

namespace HttpClients.ClientIntefaces; 

public interface IWarehouseProductService {
	Task<WarehouseProduct> CreateWarehouseProductAsync(WarehouseProductCreationDto dto);
	Task<WarehouseProduct> AlterWarehouseProductAsync(WarehouseProductCreationDto dto);
	Task<WarehouseProduct> GetWarehouseProductById(long productId, long warehouseId);
	Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsAsync();
	Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsByProductAsync(long id);
	Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsByWarehouseAsync(long id);
}