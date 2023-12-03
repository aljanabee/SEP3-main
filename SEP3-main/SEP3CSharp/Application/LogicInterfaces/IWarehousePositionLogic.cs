using Shared.Models;

namespace Application.LogicInterfaces; 

public interface IWarehousePositionLogic {
	Task<WarehousePosition> CreateAsync(string position);
	Task<WarehousePosition> GetByIdAsync(long id);
	Task<IEnumerable<WarehousePosition>> GetAsync();
}