package groupid.sep3java.gRPCServices;

import groupid.sep3java.exceptions.NotFoundException;
import groupid.sep3java.gRPCFactory.GRPCWarehouseFactory;
import groupid.sep3java.repositories.WarehouseRepository;
import grpc.Warehouse.*;
import groupid.sep3java.models.Warehouse;
import grpc.WarehouseGrpcServiceGrpc;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.reflection.v1alpha.ErrorResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
public class WarehouseServiceImpl extends WarehouseGrpcServiceGrpc.WarehouseGrpcServiceImplBase
{
  private final WarehouseRepository repository;

    public WarehouseServiceImpl(WarehouseRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public void createWarehouse(CreateWarehouseRequest request, StreamObserver<WarehouseResponse> responseObserver)
    {
        Warehouse warehouseToSave = GRPCWarehouseFactory.create(request);
        Warehouse newWarehouse = repository.save(warehouseToSave);

        WarehouseResponse response = GRPCWarehouseFactory.createWarehouseResponse(newWarehouse);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getWarehouse(GetWarehouseRequest request, StreamObserver<WarehouseResponse> responseObserver) {
    try {
        Warehouse warehouse = repository.findById(request.getWarehouseId())
                .orElseThrow(() -> new NotFoundException("Warehouse with WarehouseId:" + request.getWarehouseId() + "was not found"));
        WarehouseResponse warehouseResponse = GRPCWarehouseFactory.createWarehouseResponse(warehouse);
        responseObserver.onNext(warehouseResponse);
        responseObserver.onCompleted();
    }
    catch (NotFoundException e) {
        Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
        ErrorResponse errorResponse = ErrorResponse.newBuilder().setErrorMessage(e.getMessage()).build();
        Metadata metadata = new Metadata();
        metadata.put(errorResponseKey, errorResponse);
        responseObserver.onError(Status.NOT_FOUND.withDescription("Warehouse was not found")
                .asRuntimeException(metadata));
    }
}

    @Override public void getWarehouses(GetWarehousesRequest request,
                                      StreamObserver<GetWarehousesResponse> responseObserver) {
        List<Warehouse> warehouses = repository.findAll();

        GetWarehousesResponse getWarehousesResponse = GRPCWarehouseFactory.createGetWarehousesResponse(warehouses);
        responseObserver.onNext(getWarehousesResponse);
        responseObserver.onCompleted();
    }

}
