using Shared.Models;

namespace gRPC.ServiceInterfaces;
public interface IWarehouseGrpcService {
    Task <IEnumerable<Warehouse>> GetWarehousesAsync();
    Task <Warehouse> GetWarehouseByIdAsync(long id);
}
