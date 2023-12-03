using Shared.Models;

namespace Application.DaoInterfaces; 

public interface IWarehousePositionDao {
	Task<WarehousePosition> CreateAsync(string position);
	Task<WarehousePosition> GetByIdAsync(long id);
	Task<IEnumerable<WarehousePosition>> GetAsync();
}