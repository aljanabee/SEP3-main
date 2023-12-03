package groupid.sep3java.gRPCServices;

import groupid.sep3java.exceptions.InsufficientStockException;
import groupid.sep3java.exceptions.NotFoundException;
import groupid.sep3java.gRPCFactory.GRPCOrderFactory;
import groupid.sep3java.models.*;
import groupid.sep3java.repositories.*;
import grpc.Order.*;
import grpc.OrderGrpcServiceGrpc.*;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.reflection.v1alpha.ErrorResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.aspectj.weaver.ast.Not;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GrpcService
public class OrderServiceImpl extends OrderGrpcServiceImplBase {
	private final OrderRepository orderRepository;
	private final CustomerRepository customerRepository;
	private final WarehouseRepository warehouseRepository;
	private final ProductRepository productRepository;
	private final WarehouseProductRepository warehouseProductRepository;

	public OrderServiceImpl(OrderRepository orderRepository, CustomerRepository customerRepository,
							WarehouseRepository warehouseRepository, ProductRepository productRepository,
							WarehouseProductRepository warehouseProductRepository) {
		this.orderRepository = orderRepository;
		this.customerRepository = customerRepository;
		this.warehouseRepository = warehouseRepository;
		this.productRepository = productRepository;
		this.warehouseProductRepository = warehouseProductRepository;
	}

	@Override public void createOrder(CreateOrderRequest request,
			StreamObserver<OrderResponse> responseObserver) {
		try {
			Customer customer = customerRepository.findById(request.getCustomerId()).orElseThrow(() -> new NotFoundException("Customer with id: " + request.getCustomerId() + " was not found"));
			Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId()).orElseThrow(() -> new NotFoundException("Warehouse with id: " + request.getWarehouseId() + " was not found"));
			ArrayList<Product> orderedProducts = new ArrayList<>();
			Map<Long, Integer> productCount = new HashMap<>();
			for (long productId : request.getProductIdsList()) {
				orderedProducts.add(productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product with id: " + productId + " was not found")));
				productCount.putIfAbsent(productId, 0);
				Integer currentCount = productCount.get(productId);
				currentCount++;
				productCount.put(productId, currentCount);
			}
			ArrayList<WarehouseProduct> warehouseProductsToUpdate = new ArrayList<>();
			productCount.forEach((k, v) -> {
				WarehouseProduct warehouseProduct = warehouseProductRepository.findById(new WarehouseProductID(k, warehouse.getId())).orElseThrow(() -> new NotFoundException("Product with id: " + k + " was not found in warehouse with id: " + warehouse.getId()));
				if (warehouseProduct.getQuantity() < v) throw new InsufficientStockException("Insufficient stock on product with id: " + k + " in warehouse with id: " + warehouse.getId());
				warehouseProduct.setQuantity(warehouseProduct.getQuantity() - v);
				warehouseProductsToUpdate.add(warehouseProduct);

			});
			warehouseProductRepository.saveAll(warehouseProductsToUpdate);
			Order orderToCreate = GRPCOrderFactory.create(request, customer, warehouse, orderedProducts);
			Order order = orderRepository.save(orderToCreate);

			OrderResponse response = GRPCOrderFactory.createOrderResponse(order);
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}
		catch (NotFoundException e) {
			Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
			ErrorResponse errorResponse = ErrorResponse.newBuilder().setErrorMessage(e.getMessage()).build();
			Metadata metadata = new Metadata();
			metadata.put(errorResponseKey, errorResponse);
			responseObserver.onError(Status.NOT_FOUND.withDescription("An id reference was not found")
					.asRuntimeException(metadata));
		}
		catch (InsufficientStockException e) {
			Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
			ErrorResponse errorResponse = ErrorResponse.newBuilder().setErrorMessage(e.getMessage()).build();
			Metadata metadata = new Metadata();
			metadata.put(errorResponseKey, errorResponse);
			responseObserver.onError(Status.FAILED_PRECONDITION.withDescription("There was insufficient stock on one of the products")
					.asRuntimeException(metadata));
		}
	}

	@Override
	public void updateOrder(UpdateOrderRequest request, StreamObserver<UpdateOrderResponse> responseObserver) {
		try {
			orderRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("Order with id: " + request.getId() + " was not found"));
			Customer customer = customerRepository.findById(request.getCustomerId()).orElseThrow(() -> new NotFoundException("Customer with id: " + request.getCustomerId() + " was not found"));
			Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId()).orElseThrow(() -> new NotFoundException("Warehouse with id: " + request.getWarehouseId() + " was not found"));
			ArrayList<Product> orderedProducts = new ArrayList<>();
			for (long productId : request.getProductIdsList()) {
				orderedProducts.add(productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product with id: " + productId + " was not found")));
			}
			Order orderToUpdate = GRPCOrderFactory.create(request, customer, warehouse, orderedProducts);
			orderRepository.save(orderToUpdate);

			UpdateOrderResponse response = GRPCOrderFactory.createUpdateOrderResponse("Order " + request.getId() + " was successfully updated");
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}
		catch (NotFoundException e) {
			Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
			ErrorResponse errorResponse = ErrorResponse.newBuilder().setErrorMessage(e.getMessage()).build();
			Metadata metadata = new Metadata();
			metadata.put(errorResponseKey, errorResponse);
			responseObserver.onError(Status.NOT_FOUND.withDescription("An id reference was not found")
					.asRuntimeException(metadata));
		}
	}

	@Override public void getOrder(GetOrderRequest request,
								   StreamObserver<OrderResponse> responseObserver) {
		try {
			Order order = orderRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("Customer with id:" + request.getId() + "was not found"));
			OrderResponse response = GRPCOrderFactory.createOrderResponse(order);
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}
		catch (NotFoundException e) {
			Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
			ErrorResponse errorResponse = ErrorResponse.newBuilder().setErrorMessage(e.getMessage()).build();
			Metadata metadata = new Metadata();
			metadata.put(errorResponseKey, errorResponse);
			responseObserver.onError(Status.NOT_FOUND.withDescription("Order was not found")
					.asRuntimeException(metadata));
		}
	}

	@Override public void getOrders(GetOrdersRequest request,
			StreamObserver<GetOrdersResponse> responseObserver) {
		List<Order> orders = orderRepository.findAll();

		GetOrdersResponse response = GRPCOrderFactory.createGetOrdersResponse(orders);
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void getOrdersByWarehouseId(GetOrdersByWarehouseIdRequest request,
			   StreamObserver<GetOrdersResponse> responseObserver) {
		try {
			warehouseRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("Warehouse with id: " + request.getId() + " was not found"));
			List<Order> orders = orderRepository.findByWarehouse_Id(request.getId());

			GetOrdersResponse response = GRPCOrderFactory.createGetOrdersResponse(orders);
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
