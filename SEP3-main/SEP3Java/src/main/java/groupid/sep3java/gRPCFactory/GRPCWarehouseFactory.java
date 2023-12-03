package groupid.sep3java.gRPCFactory;

import groupid.sep3java.models.Warehouse;
import grpc.Warehouse.*;

import java.util.List;

public class GRPCWarehouseFactory
{
    private GRPCWarehouseFactory()
    {

    }

    public static Warehouse create(CreateWarehouseRequest request){
        Warehouse newWarehouse = new Warehouse(request.getName(),request.getAddress());
        return newWarehouse;
    }

    public static WarehouseResponse createWarehouseResponse(Warehouse warehouse)
    {
        WarehouseResponse warehouseResponse = WarehouseResponse.newBuilder()
                .setWarehouseId(warehouse.getId())
                .setName(warehouse.getName())
                .setAddress(warehouse.getAddress())
                .build();
        return warehouseResponse;
    }

    public static GetWarehousesResponse createGetWarehousesResponse(List<Warehouse> warehouses)
    {
        GetWarehousesResponse.Builder builder = GetWarehousesResponse.newBuilder();
        for (Warehouse warehouse : warehouses)
        {
            builder.addWarehouses(createWarehouseResponse(warehouse));
        }
        GetWarehousesResponse getWarehousesResponse = builder.build();
        return getWarehousesResponse;
    }
}
