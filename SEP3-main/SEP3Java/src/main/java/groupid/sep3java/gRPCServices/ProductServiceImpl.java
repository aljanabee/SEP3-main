package groupid.sep3java.gRPCServices;

import groupid.sep3java.exceptions.NotFoundException;
import groupid.sep3java.gRPCFactory.GRPCProductFactory;
import groupid.sep3java.models.Product;
import groupid.sep3java.repositories.ProductRepository;
import grpc.Product.*;
import grpc.ProductGrpcServiceGrpc;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.reflection.v1alpha.ErrorResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
public class ProductServiceImpl extends
		ProductGrpcServiceGrpc.ProductGrpcServiceImplBase {
	private final ProductRepository repository;

	public ProductServiceImpl(ProductRepository repository) {
		this.repository = repository;
	}

	@Override public void createProduct(CreateProductRequest request,
			StreamObserver<ProductResponse> responseObserver) {
		Product productToSave = GRPCProductFactory.create(request);
		Product newProduct = repository.save(productToSave);

		ProductResponse response = GRPCProductFactory.createProductResponse(newProduct);
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override public void getProduct(GetProductRequest request,
			StreamObserver<ProductResponse> responseObserver) {
		try {
			Product product = repository.findById(request.getId())
					.orElseThrow(() -> new NotFoundException("product with id:" + request.getId() + "was not found"));
			ProductResponse productResponse = GRPCProductFactory.createProductResponse(product);
			responseObserver.onNext(productResponse);
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

	@Override public void getProducts(GetProductsRequest request,
			StreamObserver<GetProductsResponse> responseObserver) {
		List<Product> products = repository.findAll();

		GetProductsResponse getProductsResponse = GRPCProductFactory.createGetProductsResponse(products);
		responseObserver.onNext(getProductsResponse);
		responseObserver.onCompleted();
	}
}
