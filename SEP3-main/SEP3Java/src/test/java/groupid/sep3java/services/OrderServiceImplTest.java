package groupid.sep3java.services;

import groupid.sep3java.gRPCServices.OrderServiceImpl;
import groupid.sep3java.repositories.*;
import groupid.sep3java.models.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.internal.testing.StreamRecorder;
import org.assertj.core.api.Assertions;
import grpc.Order.*;
import grpc.Customer.*;
import grpc.Warehouse.*;
import grpc.Product.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
public class OrderServiceImplTest {

	@Autowired OrderRepository orderRepository;
	@Autowired CustomerRepository customerRepository;
	@Autowired WarehouseRepository warehouseRepository;
	@Autowired ProductRepository productRepository;
	@Autowired WarehouseProductRepository warehouseProductRepository;

	private OrderServiceImpl grpcService;

	@BeforeAll
	public void SetUp(){
		/* ID refers to what ID an entity has */
		ArrayList<Customer> customers = new ArrayList<>();
		/* ID 1*/ customers.add(new Customer( "Peter Griffin", "12131456", "31 Spooner Street Quahog","Griffen@gmail.com"));
		/* ID 2*/ customers.add(new Customer("Joe Swanson", "64859635", "33 Spooner Street Quahog","Swanson@gmail.com"));
		/* ID 3*/ customers.add(new Customer("Cleveland Brown", "78964532", "29 Spooner Street Quahog","Brown@gmail.com"));
		customerRepository.saveAll(customers);

		ArrayList<Warehouse> warehouses = new ArrayList<>();
		/* ID 4*/ warehouses.add(new Warehouse("Via Warehouse", "Banegårdsgade 2, 8700 Horsens, Danmark"));
		/* ID 5*/ warehouses.add(new Warehouse("Test Warehouse", "Testadresse 123, 8700 Horsens, Danmark"));
		warehouseRepository.saveAll(warehouses);

		ArrayList<Product> products = new ArrayList<>();
		/* ID 6*/ products.add(new Product("Bose Acoustimass 5 Series III Speaker System - AM53BK", "A description of the product...", 399));
		/* ID 7*/ products.add(new Product("Bose 27028 161 Bookshelf Pair Speakers In White - 161WH", "A description of the product...", 158));
		/* ID 8*/ products.add(new Product("Panasonic Integrated Telephone System - KXTS108W", "A description of the product...", 44));
		/* ID 9*/ products.add(new Product("Panasonic Hands-Free Headset - KXTCA86", "A description of the product...", 14.95));
		/* ID 10*/ products.add(new Product("Sanus Euro Foundations Satellite Speaker Stand - EFSATS", "A description of the product...", 79.99));
		productRepository.saveAll(products);

		List<Product> productsFromDatabase = productRepository.findAll();
		List<Warehouse> warehousesFromDatabase = warehouseRepository.findAll();
		ArrayList<WarehouseProduct> warehouseProducts = new ArrayList<>();

		// WID = WarehouseID, PID = ProductID
		// WarehouseProducts from Via Warehouse
		/* WID 4, PID 6*/  warehouseProducts.add(new WarehouseProduct(productsFromDatabase.get(0), warehousesFromDatabase.get(0), 3, 2, "C0102"));
		/* WID 4, PID 7*/  warehouseProducts.add(new WarehouseProduct(productsFromDatabase.get(1), warehousesFromDatabase.get(0), 5, 1, "C0103"));
		/* WID 4, PID 8*/  warehouseProducts.add(new WarehouseProduct(productsFromDatabase.get(2), warehousesFromDatabase.get(0), 7, 3, "C0110"));

		// WarehouseProducts from Test Warehouse
		/* WID 5, PID 9*/  warehouseProducts.add(new WarehouseProduct(productsFromDatabase.get(3), warehousesFromDatabase.get(1), 0, 2, "C0111"));
		/* WID 5, PID 10*/ warehouseProducts.add(new WarehouseProduct(productsFromDatabase.get(4), warehousesFromDatabase.get(1), 4, 2, "B0101"));
		warehouseProductRepository.saveAll(warehouseProducts);

		List<Customer> customersFromDatabase = customerRepository.findAll();
		ArrayList<Order> orders = new ArrayList<>();
		/* ID 11*/ orders.add(new Order(customersFromDatabase.get(0), warehousesFromDatabase.get(0),
				LocalDateTime.ofEpochSecond(1670841310, 0, ZoneOffset.UTC),
				false, LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
				Arrays.asList(
						productsFromDatabase.get(0),
						productsFromDatabase.get(0))
		));
		/* ID 12*/ orders.add(new Order(customersFromDatabase.get(1), warehousesFromDatabase.get(1),
				LocalDateTime.ofEpochSecond(1670624560, 0, ZoneOffset.UTC),
				false, LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
				Arrays.asList(
						productsFromDatabase.get(3),
						productsFromDatabase.get(4))
		));
		orderRepository.saveAll(orders);
		grpcService = new OrderServiceImpl(orderRepository, customerRepository, warehouseRepository, productRepository, warehouseProductRepository);
	}

	@DisplayName("Create Order With Non-Existing Customer Expecting GRPC NotFound")
	@Test()
	public void createOrderWithNonExistingCustomerExpectingGRPCNotFound() {
		CreateOrderRequest createOrderRequest = CreateOrderRequest.newBuilder()
				.setCustomerId(4) // This id doesn't exist
				.setWarehouseId(4)
				.setDateTimeOrdered(1670841310)
				.setIsPacked(false)
				.setDateTimeSent(0)
				.addProductIds(6)
				.addProductIds( 7)
				.build();
		StreamRecorder<OrderResponse> responseObserver = StreamRecorder.create();
		grpcService.createOrder(createOrderRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNotNull();
			Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
					.isEqualTo(Status.NOT_FOUND.getCode());
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("Create Order With Non-Existing Warehouse Expecting GRPC NotFound")
	@Test
	public void createOrderWithNonExistingWarehouseExpectingGRPCNotFound() {
		CreateOrderRequest createOrderRequest = CreateOrderRequest.newBuilder()
				.setCustomerId(1)
				.setWarehouseId(3) // This id doesn't exist
				.setDateTimeOrdered(1670841310)
				.setIsPacked(false)
				.setDateTimeSent(0)
				.addProductIds(6)
				.addProductIds( 7)
				.build();
		StreamRecorder<OrderResponse> responseObserver = StreamRecorder.create();
		grpcService.createOrder(createOrderRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNotNull();
			Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
					.isEqualTo(Status.NOT_FOUND.getCode());
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("Create Order With Non-Existing Product Expecting GRPC NotFound")
	@Test
	public void createOrderWithNonExistingProductExpectingGRPCNotFound() {
		CreateOrderRequest createOrderRequest = CreateOrderRequest.newBuilder()
				.setCustomerId(1)
				.setWarehouseId(4)
				.setDateTimeOrdered(1670841310)
				.setIsPacked(false)
				.setDateTimeSent(0)
				.addProductIds(7)
				.addProductIds(99) // This id doesn't exist
				.build();
		StreamRecorder<OrderResponse> responseObserver = StreamRecorder.create();
		grpcService.createOrder(createOrderRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNotNull();
			Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
					.isEqualTo(Status.NOT_FOUND.getCode());
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("Create Order With Non-Existing WarehouseProduct Expecting GRPC NotFound")
	@Test
	public void createOrderWithNonExistingWarehouseProductExpectingGRPCNotFound() {
		CreateOrderRequest createOrderRequest = CreateOrderRequest.newBuilder()
				.setCustomerId(1)
				.setWarehouseId(4)
				.setDateTimeOrdered(1670841310)
				.setIsPacked(false)
				.setDateTimeSent(0)
				.addProductIds(7)
				.addProductIds(9) // This product id doesn't exist in warehouse 4
				.build();
		StreamRecorder<OrderResponse> responseObserver = StreamRecorder.create();
		grpcService.createOrder(createOrderRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNotNull();
			Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
					.isEqualTo(Status.NOT_FOUND.getCode());
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("Create Order With Product That Has Insufficient Stock Expecting GRPC FailedPrecondition")
	@Test
	public void createOrderWithProductThatHasInsufficientStockExpectingGRPCFailedPrecondition() {
		CreateOrderRequest createOrderRequest = CreateOrderRequest.newBuilder()
				.setCustomerId(1)
				.setWarehouseId(4)
				.setDateTimeOrdered(1670841310)
				.setIsPacked(false)
				.setDateTimeSent(0)
				.addProductIds(6)
				.addProductIds(6)
				.addProductIds(6)
				.addProductIds(6) // Adding 4 of product id 6 to order, but there is only 3 in stock
				.build();
		StreamRecorder<OrderResponse> responseObserver = StreamRecorder.create();
		grpcService.createOrder(createOrderRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNotNull();
			Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
					.isEqualTo(Status.FAILED_PRECONDITION.getCode());
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("Create Order Without Errors Expecting OrderResponse")
	@Test()
	public void createOrderWithoutErrorsExpectingOrderResponse() {
		CreateOrderRequest createOrderRequest = CreateOrderRequest.newBuilder()
				.setCustomerId(1)
				.setWarehouseId(4)
				.setDateTimeOrdered(1670841310)
				.setIsPacked(false)
				.setDateTimeSent(0)
				.addProductIds(6)
				.addProductIds( 7)
				.build();
		StreamRecorder<OrderResponse> responseObserver = StreamRecorder.create();
		grpcService.createOrder(createOrderRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNull();
			List<OrderResponse> results = responseObserver.getValues();
			Assertions.assertThat(results.size()).isEqualTo(1);
			OrderResponse response = results.get(0);
			Assertions.assertThat(response).isEqualTo(OrderResponse.newBuilder()
					.setId(response.getId())
					.setCustomer(CustomerResponse.newBuilder()
							.setId(1)
							.setFullname("Peter Griffin")
							.setPhoneNo("12131456")
							.setAddress("31 Spooner Street Quahog")
							.setMail("Griffen@gmail.com")
							.build())
					.setWarehouse(WarehouseResponse.newBuilder()
							.setWarehouseId(4)
							.setName("Via Warehouse")
							.setAddress("Banegårdsgade 2, 8700 Horsens, Danmark")
							.build())
					.setDateTimeOrdered(1670841310)
					.setIsPacked(false)
					.setDateTimeSent(0)
					.setProducts(grpc.Product.GetProductsResponse.newBuilder()
							.addProducts(ProductResponse.newBuilder()
									.setId(6)
									.setName("Bose Acoustimass 5 Series III Speaker System - AM53BK")
									.setDescription("A description of the product...")
									.setPrice(399)
									.build())
							.addProducts(ProductResponse.newBuilder()
									.setId(7)
									.setName("Bose 27028 161 Bookshelf Pair Speakers In White - 161WH")
									.setDescription("A description of the product...")
									.setPrice(158)
									.build())
							.build())
					.build()
			);
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("Get Order With Non-Existing Id Expecting GRPC NotFound")
	@Test
	public void getOrderWithNonExistingIdExpectingGRPCNotFound() {
		GetOrderRequest getOrderRequest = GetOrderRequest.newBuilder()
				.setId(99).build(); // This id doesn't exist
		StreamRecorder<OrderResponse> responseObserver = StreamRecorder.create();
		grpcService.getOrder(getOrderRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNotNull();
			Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
					.isEqualTo(Status.NOT_FOUND.getCode());
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("Get Order Without Errors Expecting OrderResponse")
	@Test
	public void getOrderWithoutErrorsExpectingOrderResponse() {
		GetOrderRequest getOrderRequest = GetOrderRequest.newBuilder()
				.setId(11).build();
		StreamRecorder<OrderResponse> responseObserver = StreamRecorder.create();
		grpcService.getOrder(getOrderRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNull();
			List<OrderResponse> results = responseObserver.getValues();
			Assertions.assertThat(results.size()).isEqualTo(1);
			OrderResponse response = results.get(0);
			Assertions.assertThat(response).isEqualTo(OrderResponse.newBuilder()
					.setId(response.getId())
					.setCustomer(CustomerResponse.newBuilder()
							.setId(1)
							.setFullname("Peter Griffin")
							.setPhoneNo("12131456")
							.setAddress("31 Spooner Street Quahog")
							.setMail("Griffen@gmail.com")
							.build())
					.setWarehouse(WarehouseResponse.newBuilder()
							.setWarehouseId(4)
							.setName("Via Warehouse")
							.setAddress("Banegårdsgade 2, 8700 Horsens, Danmark")
							.build())
					.setDateTimeOrdered(1670841310)
					.setIsPacked(false)
					.setDateTimeSent(0)
					.setProducts(grpc.Product.GetProductsResponse.newBuilder()
							.addProducts(ProductResponse.newBuilder()
									.setId(6)
									.setName("Bose Acoustimass 5 Series III Speaker System - AM53BK")
									.setDescription("A description of the product...")
									.setPrice(399)
									.build())
							.addProducts(ProductResponse.newBuilder()
									.setId(6)
									.setName("Bose Acoustimass 5 Series III Speaker System - AM53BK")
									.setDescription("A description of the product...")
									.setPrice(399)
									.build())
							.build())
					.build()
			);
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("Get Orders Expecting GetOrdersResponse With More Than Zero Orders")
	@Test
	public void getOrdersExpectingGetOrdersResponseWithMoreThanZeroOrders() {
		GetOrdersRequest getOrdersRequest = GetOrdersRequest.newBuilder().build();
		StreamRecorder<GetOrdersResponse> responseObserver = StreamRecorder.create();
		grpcService.getOrders(getOrdersRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNull();
			List<GetOrdersResponse> results = responseObserver.getValues();
			Assertions.assertThat(results.size()).isEqualTo(1);
			GetOrdersResponse response = results.get(0);
			Assertions.assertThat(response.getOrdersList().size()).isGreaterThan(0);
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("Get Orders By WarehouseId With Non-Existing Id Expecting GRPC NotFound")
	@Test
	public void getOrdersByWarehouseIdWithNonExistingIdExpectingGRPCNotFound() {
		GetOrdersByWarehouseIdRequest getOrdersByWarehouseIdRequest = GetOrdersByWarehouseIdRequest.newBuilder()
				.setId(99).build(); // This id doesn't exist
		StreamRecorder<GetOrdersResponse> responseObserver = StreamRecorder.create();
		grpcService.getOrdersByWarehouseId(getOrdersByWarehouseIdRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNotNull();
			Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
					.isEqualTo(Status.NOT_FOUND.getCode());
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("Get Orders By WarehouseId Without Errors Expecting GetOrdersResponse With More Than Zero Orders")
	@Test
	public void getOrdersByWarehouseIdWithoutErrorsExpectingGetOrdersResponseWithMoreThanZeroOrders() {
		GetOrdersByWarehouseIdRequest getOrdersByWarehouseIdRequest = GetOrdersByWarehouseIdRequest.newBuilder()
				.setId(4).build();
		StreamRecorder<GetOrdersResponse> responseObserver = StreamRecorder.create();
		grpcService.getOrdersByWarehouseId(getOrdersByWarehouseIdRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNull();
			List<GetOrdersResponse> results = responseObserver.getValues();
			Assertions.assertThat(results.size()).isEqualTo(1);
			GetOrdersResponse response = results.get(0);
			Assertions.assertThat(response.getOrdersList().size()).isGreaterThan(0);
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("Update Order With Non-Existing Customer Expecting GRPC NotFound")
	@Test
	public void updateOrderWithNonExistingCustomerExpectingGRPCNotFound() {
		Order orderToUpdate = orderRepository.findById(11L).orElseThrow();
		UpdateOrderRequest.Builder updateOrderRequestBuilder = UpdateOrderRequest.newBuilder()
				.setId(orderToUpdate.getId())
				.setCustomerId(4) // This updated id doesn't exist
				.setWarehouseId(orderToUpdate.getWarehouse().getId())
				.setDateTimeOrdered(LocalDateTime.of(orderToUpdate.getDateOrdered(),
						orderToUpdate.getTimeOrdered()).toEpochSecond(ZoneOffset.UTC))
				.setIsPacked(orderToUpdate.isPacked())
				.setDateTimeSent(LocalDateTime.of(orderToUpdate.getDateSent(),
						orderToUpdate.getTimeSent()).toEpochSecond(ZoneOffset.UTC));

		for (Product product : orderToUpdate.getOrderedProducts()) {
		    updateOrderRequestBuilder.addProductIds(product.getId());
		}
		UpdateOrderRequest updateOrderRequest = updateOrderRequestBuilder.build();
		StreamRecorder<UpdateOrderResponse> responseObserver = StreamRecorder.create();
		grpcService.updateOrder(updateOrderRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNotNull();
			Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
					.isEqualTo(Status.NOT_FOUND.getCode());
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("Update Order With Non-Existing Warehouse Expecting GRPC NotFound")
	@Test
	public void updateOrderWithNonExistingWarehouseExpectingGRPCNotFound() {
		Order orderToUpdate = orderRepository.findById(11L).orElseThrow();
		UpdateOrderRequest.Builder updateOrderRequestBuilder = UpdateOrderRequest.newBuilder()
				.setId(orderToUpdate.getId())
				.setCustomerId(orderToUpdate.getCustomer().getId())
				.setWarehouseId(99) // This id doesn't exist
				.setDateTimeOrdered(LocalDateTime.of(orderToUpdate.getDateOrdered(), orderToUpdate.getTimeOrdered()).toEpochSecond(ZoneOffset.UTC))
				.setIsPacked(orderToUpdate.isPacked())
				.setDateTimeSent(LocalDateTime.of(orderToUpdate.getDateSent(), orderToUpdate.getTimeSent()).toEpochSecond(ZoneOffset.UTC));
		for (Product product : orderToUpdate.getOrderedProducts())
		{
		    updateOrderRequestBuilder.addProductIds(product.getId());
		}
		UpdateOrderRequest updateOrderRequest = updateOrderRequestBuilder.build();
		StreamRecorder<UpdateOrderResponse> responseObserver = StreamRecorder.create();
		grpcService.updateOrder(updateOrderRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNotNull();
			Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
					.isEqualTo(Status.NOT_FOUND.getCode());
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("Update Order With Non-Existing Product Expecting GRPC NotFound")
	@Test
	public void updateOrderWithNonExistingProductExpectingGRPCNotFound() {
		Order orderToUpdate = orderRepository.findById(11L).orElseThrow();
		UpdateOrderRequest.Builder updateOrderRequestBuilder = UpdateOrderRequest.newBuilder()
				.setId(orderToUpdate.getId())
				.setCustomerId(orderToUpdate.getCustomer().getId())
				.setWarehouseId(orderToUpdate.getWarehouse().getId())
				.setDateTimeOrdered(LocalDateTime.of(orderToUpdate.getDateOrdered(), orderToUpdate.getTimeOrdered()).toEpochSecond(ZoneOffset.UTC))
				.setIsPacked(orderToUpdate.isPacked())
				.setDateTimeSent(LocalDateTime.of(orderToUpdate.getDateSent(), orderToUpdate.getTimeSent()).toEpochSecond(ZoneOffset.UTC));
		for (Product product : orderToUpdate.getOrderedProducts())
		{
			updateOrderRequestBuilder.addProductIds(product.getId());
		}
		updateOrderRequestBuilder.addProductIds(99); // This id doesn't exist
		UpdateOrderRequest updateOrderRequest = updateOrderRequestBuilder.build();
		StreamRecorder<UpdateOrderResponse> responseObserver = StreamRecorder.create();
		grpcService.updateOrder(updateOrderRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNotNull();
			Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
					.isEqualTo(Status.NOT_FOUND.getCode());
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	/**
	 * this test should fail as the service doesn't handle this scenario
	 * */
	@DisplayName("Update Order With Non-Existing WarehouseProduct Expecting GRPC NotFound")
	@Test
	public void updateOrderWithNonExistingWarehouseProductExpectingGRPCNotFound() {
		Order orderToUpdate = orderRepository.findById(11L).orElseThrow();
		UpdateOrderRequest.Builder updateOrderRequestBuilder = UpdateOrderRequest.newBuilder()
				.setId(orderToUpdate.getId())
				.setCustomerId(orderToUpdate.getCustomer().getId())
				.setWarehouseId(orderToUpdate.getWarehouse().getId())
				.setDateTimeOrdered(LocalDateTime.of(orderToUpdate.getDateOrdered(), orderToUpdate.getTimeOrdered()).toEpochSecond(ZoneOffset.UTC))
				.setIsPacked(orderToUpdate.isPacked())
				.setDateTimeSent(LocalDateTime.of(orderToUpdate.getDateSent(), orderToUpdate.getTimeSent()).toEpochSecond(ZoneOffset.UTC));
		for (Product product : orderToUpdate.getOrderedProducts())
		{
			updateOrderRequestBuilder.addProductIds(product.getId());
		}
		updateOrderRequestBuilder.addProductIds(9); // This product is not in the right warehouse
		UpdateOrderRequest updateOrderRequest = updateOrderRequestBuilder.build();
		StreamRecorder<UpdateOrderResponse> responseObserver = StreamRecorder.create();
		grpcService.updateOrder(updateOrderRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNotNull();
			Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
					.isEqualTo(Status.NOT_FOUND.getCode());
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

 /**
 	* this test should fail as the service doesn't handle this scenario
 	* */
	@DisplayName("Update Order With Product That Has Insufficient Stock Expecting GRPC FailedPrecondition")
	@Test
	public void updateOrderWithProductThatHasInsufficientStockExpectingGRPCFailedPrecondition() {
		Order orderToUpdate = orderRepository.findById(11L).orElseThrow();
		UpdateOrderRequest.Builder updateOrderRequestBuilder = UpdateOrderRequest.newBuilder()
				.setId(orderToUpdate.getId())
				.setCustomerId(orderToUpdate.getCustomer().getId())
				.setWarehouseId(orderToUpdate.getWarehouse().getId())
				.setDateTimeOrdered(LocalDateTime.of(orderToUpdate.getDateOrdered(), orderToUpdate.getTimeOrdered()).toEpochSecond(ZoneOffset.UTC))
				.setIsPacked(orderToUpdate.isPacked())
				.setDateTimeSent(LocalDateTime.of(orderToUpdate.getDateSent(), orderToUpdate.getTimeSent()).toEpochSecond(ZoneOffset.UTC));
		for (Product product : orderToUpdate.getOrderedProducts()) // adding double the quantity
		{
			updateOrderRequestBuilder.addProductIds(product.getId());
			updateOrderRequestBuilder.addProductIds(product.getId());
		}

		UpdateOrderRequest updateOrderRequest = updateOrderRequestBuilder.build();
		StreamRecorder<UpdateOrderResponse> responseObserver = StreamRecorder.create();
		grpcService.updateOrder(updateOrderRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNotNull();
			Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
					.isEqualTo(Status.FAILED_PRECONDITION.getCode());
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("Update Order Without Errors Expecting UpdateOrderResponse")
	@Test
	public void updateOrderWithoutErrorsExpectingUpdateOrderResponse() {
		Order orderToUpdate = orderRepository.findById(11L).orElseThrow();
		UpdateOrderRequest.Builder updateOrderRequestBuilder = UpdateOrderRequest.newBuilder()
				.setId(orderToUpdate.getId())
				.setCustomerId(3) // Updated Customer with an existing customer id
				.setWarehouseId(orderToUpdate.getWarehouse().getId())
				.setDateTimeOrdered(LocalDateTime.of(orderToUpdate.getDateOrdered(),
						orderToUpdate.getTimeOrdered()).toEpochSecond(ZoneOffset.UTC))
				.setIsPacked(orderToUpdate.isPacked())
				.setDateTimeSent(LocalDateTime.of(orderToUpdate.getDateSent(),
						orderToUpdate.getTimeSent()).toEpochSecond(ZoneOffset.UTC));

		for (Product product : orderToUpdate.getOrderedProducts()) {
			updateOrderRequestBuilder.addProductIds(product.getId());
		}
		UpdateOrderRequest updateOrderRequest = updateOrderRequestBuilder.build();
		StreamRecorder<UpdateOrderResponse> responseObserver = StreamRecorder.create();
		grpcService.updateOrder(updateOrderRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNull();
			List<UpdateOrderResponse> results = responseObserver.getValues();
			Assertions.assertThat(results.size()).isEqualTo(1);
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}
}
