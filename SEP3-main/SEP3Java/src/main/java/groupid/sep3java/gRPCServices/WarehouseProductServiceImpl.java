package groupid.sep3java.gRPCServices;

import groupid.sep3java.exceptions.AlreadyExistsException;
import groupid.sep3java.exceptions.NotFoundException;
import groupid.sep3java.gRPCFactory.GRPCWarehouseProductFactory;
import groupid.sep3java.models.*;
import groupid.sep3java.repositories.ProductRepository;
import groupid.sep3java.repositories.WarehouseProductRepository;
import groupid.sep3java.repositories.WarehouseRepository;
import grpc.WarehouseProduct.*;
import grpc.WarehouseProductGrpcServiceGrpc.*;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.reflection.v1alpha.ErrorResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;

@GrpcService
public class WarehouseProductServiceImpl extends WarehouseProductGrpcServiceImplBase {
	private final WarehouseProductRepository warehouseProductRepository;
	private final ProductRepository productRepository;
	private final WarehouseRepository warehouseRepository;

	public WarehouseProductServiceImpl(
			WarehouseProductRepository warehouseProductRepository,
			ProductRepository productRepository,
			WarehouseRepository warehouseRepository) {
		this.warehouseProductRepository = warehouseProductRepository;
		this.productRepository = productRepository;
		this.warehouseRepository = warehouseRepository;
	}

	@Override public void createWarehouseProduct(
			CreateWarehouseProductRequest request,
			StreamObserver<WarehouseProductResponse> responseObserver) {
		try {
			Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new NotFoundException("product with id:" + request.getProductId() + "was not found"));
			Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId()).orElseThrow(() -> new NotFoundException("Warehouse with WarehouseId:" + request.getWarehouseId() + "was not found"));

			if (warehouseProductRepository.findById(new WarehouseProductID(
					product.getId(), warehouse.getId())).isPresent()) throw new AlreadyExistsException();

			WarehouseProduct warehouseProductToSave = GRPCWarehouseProductFactory.create(request,product,warehouse);
			WarehouseProduct newProduct = warehouseProductRepository.save(warehouseProductToSave);

			WarehouseProductResponse response = GRPCWarehouseProductFactory.createWarehouseProductResponse(newProduct);
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}
		catch (AlreadyExistsException e){
			Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
			ErrorResponse errorResponse = ErrorResponse.newBuilder().setErrorMessage(e.getMessage()).build();
			Metadata metadata = new Metadata();
			metadata.put(errorResponseKey, errorResponse);
			responseObserver.onError(Status.ALREADY_EXISTS.withDescription("WarehouseProduct could not be created as it already exists")
					.asRuntimeException(metadata));
		}
		catch (NotFoundException e) {
			Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
			ErrorResponse errorResponse = ErrorResponse.newBuilder().setErrorMessage(e.getMessage()).build();
			Metadata metadata = new Metadata();
			metadata.put(errorResponseKey, errorResponse);
			responseObserver.onError(Status.NOT_FOUND.withDescription("WarehouseProduct could not be created as warehouse or product could not be found")
					.asRuntimeException(metadata));
		}
		catch (ConstraintViolationException e) {
			Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
			ErrorResponse errorResponse = ErrorResponse.newBuilder().setErrorMessage(e.getCause().getMessage()).build();
			Metadata metadata = new Metadata();
			metadata.put(errorResponseKey, errorResponse);
			responseObserver.onError(Status.ABORTED.withDescription("WarehouseProduct could not be created as it violates data constrains")
					.asRuntimeException(metadata));
		}
	}

	@Override public void alterWarehouseProduct(
			CreateWarehouseProductRequest request,
			StreamObserver<WarehouseProductResponse> responseObserver) {
		try {
			Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new NotFoundException("product with id:" + request.getProductId() + "was not found"));
			Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId()).orElseThrow(() -> new NotFoundException("Warehouse with WarehouseId:" + request.getWarehouseId() + "was not found"));

			if (warehouseProductRepository.findById(new WarehouseProductID(
					product.getId(), warehouse.getId())).isEmpty()) throw new NotFoundException("Warehouse was not found");

			WarehouseProduct warehouseProductToSave = GRPCWarehouseProductFactory.create(request,product,warehouse);
			WarehouseProduct newProduct = warehouseProductRepository.save(warehouseProductToSave);

			WarehouseProductResponse response = GRPCWarehouseProductFactory.createWarehouseProductResponse(newProduct);
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}
		catch (NotFoundException e) {
			Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
			ErrorResponse errorResponse = ErrorResponse.newBuilder().setErrorMessage(e.getMessage()).build();
			Metadata metadata = new Metadata();
			metadata.put(errorResponseKey, errorResponse);
			responseObserver.onError(Status.NOT_FOUND.withDescription("WarehouseProduct could not be altered as, it didn't exist, or warehouse or product could not be found")
					.asRuntimeException(metadata));
		}
		catch (ConstraintViolationException e) {
			Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
			ErrorResponse errorResponse = ErrorResponse.newBuilder().setErrorMessage(e.getCause().getMessage()).build();
			Metadata metadata = new Metadata();
			metadata.put(errorResponseKey, errorResponse);
			responseObserver.onError(Status.ABORTED.withDescription("WarehouseProduct could not be altered as it violates data constrains")
					.asRuntimeException(metadata));
		}
	}

	@Override public void getWarehouseProduct(
			GetWarehouseProductRequest request,
			StreamObserver<WarehouseProductResponse> responseObserver) {
		try {
			WarehouseProduct warehouseProduct = warehouseProductRepository.findById(new WarehouseProductID(request.getProductId(),
							request.getWarehouseId()))
					.orElseThrow(() -> new RuntimeException("product with id:" + request.getProductId()
							+ " or warehouse with id:" + request.getWarehouseId() + " was not found"));
			WarehouseProductResponse response = GRPCWarehouseProductFactory.createWarehouseProductResponse(warehouseProduct);
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}
		catch (RuntimeException e) {
			Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
			ErrorResponse errorResponse = ErrorResponse.newBuilder().setErrorMessage(e.getMessage()).build();
			Metadata metadata = new Metadata();
			metadata.put(errorResponseKey, errorResponse);
			responseObserver.onError(Status.NOT_FOUND.withDescription("Warehouse or Product could not be found")
					.asRuntimeException(metadata));
		}
	}

	@Override public void getWarehouseProducts(
			GetWarehouseProductsRequest request,
			StreamObserver<GetWarehouseProductsResponse> responseObserver) {
		List<WarehouseProduct> products = warehouseProductRepository.findAll();

		GetWarehouseProductsResponse response = GRPCWarehouseProductFactory.createGetWarehouseProductsResponse(products);
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override public void getWarehouseProductByProductId(
			QueryByPartialIdRequest request,
			StreamObserver<GetWarehouseProductsResponse> responseObserver) {
		try {
			productRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("Product with id: " + request.getId() + " was not found"));
			List<WarehouseProduct> products = warehouseProductRepository.findWarehouseProductsByProductId(
					request.getId());

			GetWarehouseProductsResponse response = GRPCWarehouseProductFactory.createGetWarehouseProductsResponse(products);
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}
		catch (NotFoundException e) {
			Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
			ErrorResponse errorResponse = ErrorResponse.newBuilder().setErrorMessage(e.getMessage()).build();
			Metadata metadata = new Metadata();
			metadata.put(errorResponseKey, errorResponse);
			responseObserver.onError(Status.NOT_FOUND.withDescription("Product was not found")
					.asRuntimeException(metadata));
		}
	}

	@Override public void getWarehouseProductByWarehouseId(
			QueryByPartialIdRequest request,
			StreamObserver<GetWarehouseProductsResponse> responseObserver) {
		try {
			warehouseRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("Warehouse with id: " + request.getId() + " was not found"));
			List<WarehouseProduct> products = warehouseProductRepository.findWarehouseProductsByWarehouseId(
					request.getId());

			GetWarehouseProductsResponse response = GRPCWarehouseProductFactory.createGetWarehouseProductsResponse(products);
			responseObserver.onNext(response);
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
}
