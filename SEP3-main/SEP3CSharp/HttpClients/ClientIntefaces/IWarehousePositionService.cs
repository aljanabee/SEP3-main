using Shared.Models;

namespace HttpClients.ClientIntefaces; 

public interface IWarehousePositionService {
	Task<WarehousePosition> CreateAsync(string position);
	Task<WarehousePosition> GetByIdAsync(long id);
	Task<IEnumerable<WarehousePosition>> GetAsync();
}