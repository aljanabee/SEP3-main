using Shared.Models;

namespace HttpClients.ClientIntefaces;
public interface IWarehouseService {
    Task<IEnumerable<Warehouse>> GetWarehousesAsync();
    Task<Warehouse> GetWarehouseByIdAsync(long id);
}
