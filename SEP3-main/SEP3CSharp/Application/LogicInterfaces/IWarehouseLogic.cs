using Shared.Models;

namespace Application.LogicInterfaces;
public interface IWarehouseLogic {
    Task<IEnumerable<Warehouse>> GetWarehousesAsync();
    Task<Warehouse> GetWarehouseByIdAsync(long id);
}
