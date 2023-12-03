package groupid.sep3java.services;


import groupid.sep3java.gRPCServices.WarehouseProductServiceImpl;
import groupid.sep3java.models.Product;
import groupid.sep3java.models.Warehouse;
import groupid.sep3java.models.WarehouseProduct;
import groupid.sep3java.models.WarehouseProductID;
import groupid.sep3java.repositories.ProductRepository;
import groupid.sep3java.repositories.WarehouseProductRepository;
import groupid.sep3java.repositories.WarehouseRepository;
import grpc.WarehouseProduct.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.internal.testing.StreamRecorder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
public class WarehouseProductServiceImplTest {

	@Autowired WarehouseProductRepository warehouseProductRepository;
	@Autowired WarehouseRepository warehouseRepository;
	@Autowired ProductRepository productRepository;

	private WarehouseProductServiceImpl service;

	@BeforeAll public void SetUp() {
		ArrayList<Warehouse> warehouses = new ArrayList<>();
		/* ID 1*/ warehouses.add(new Warehouse("Via Warehouse", "Banegårdsgade 2, 8700 Horsens, Danmark"));
		/* ID 2*/ warehouses.add(new Warehouse("Test Warehouse", "Testadresse 123, 8700 Horsens, Danmark"));
		warehouseRepository.saveAll(warehouses);

		ArrayList<Product> products = new ArrayList<>();
		/* ID 3*/ products.add(new Product("Bose Acoustimass 5 Series III Speaker System - AM53BK", "A description of the product...", 399));
		/* ID 4*/ products.add(new Product("Bose 27028 161 Bookshelf Pair Speakers In White - 161WH", "A description of the product...", 158));
		/* ID 5*/ products.add(new Product("Panasonic Integrated Telephone System - KXTS108W", "A description of the product...", 44));
		/* ID 6*/ products.add(new Product("Panasonic Hands-Free Headset - KXTCA86", "A description of the product...", 14.95));
		/* ID 7*/ products.add(new Product("Sanus Euro Foundations Satellite Speaker Stand - EFSATS", "A description of the product...", 79.99));
		productRepository.saveAll(products);

		List<Product> productsFromDatabase = productRepository.findAll();
		List<Warehouse> warehousesFromDatabase = warehouseRepository.findAll();
		ArrayList<groupid.sep3java.models.WarehouseProduct> warehouseProducts = new ArrayList<>();

		// WID = WarehouseID, PID = ProductID
		// WarehouseProducts from Via Warehouse
		/* WID 1, PID 3*/  warehouseProducts.add(new WarehouseProduct(productsFromDatabase.get(0), warehousesFromDatabase.get(0), 3, 2, "C0102"));
		/* WID 1, PID 4*/  warehouseProducts.add(new WarehouseProduct(productsFromDatabase.get(1), warehousesFromDatabase.get(0), 5, 1, "C0103"));
		/* WID 1, PID 5*/  warehouseProducts.add(new WarehouseProduct(productsFromDatabase.get(2), warehousesFromDatabase.get(0), 7, 3, "C0110"));

		// WarehouseProducts from Test Warehouse
		/* WID 2, PID 6*/  warehouseProducts.add(new WarehouseProduct(productsFromDatabase.get(3), warehousesFromDatabase.get(1), 0, 2, "C0111"));
		/* WID 2, PID 7*/ warehouseProducts.add(new WarehouseProduct(productsFromDatabase.get(4), warehousesFromDatabase.get(1), 4, 2, "B0101"));
		warehouseProductRepository.saveAll(warehouseProducts);

		service = new WarehouseProductServiceImpl(warehouseProductRepository,productRepository, warehouseRepository);
	}

	@DisplayName("Create WarehouseProduct Expecting grpc not found")
	@Test
	public void warehouseOrProductCouldBeFoundOnCreateTestExpectingGRPCNotFound() {
		CreateWarehouseProductRequest request = CreateWarehouseProductRequest.newBuilder()
				.setWarehouseId(3).setProductId(3) // this warehouse id doesn't exits
				.setQuantity(3).setMinimumQuantity(3)
				.setWarehousePosition("C0101").build();
		StreamRecorder<WarehouseProductResponse> responseObserver = StreamRecorder.create();
		service.createWarehouseProduct(request,responseObserver);
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

	@DisplayName("Create WarehouseProduct Expecting grpc already exists")
	@Test
	public void warehouseProductAlreadyExitsCreateTestExpectingGRPCAlreadyExists() {
		CreateWarehouseProductRequest request = CreateWarehouseProductRequest.newBuilder()
				.setWarehouseId(1).setProductId(3) // this id combination is already in the repository
				.setQuantity(3).setMinimumQuantity(3)
				.setWarehousePosition("C0101").build();
		StreamRecorder<WarehouseProductResponse> responseObserver = StreamRecorder.create();
		service.createWarehouseProduct(request,responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			Assertions.assertThat(responseObserver.getError()).isNotNull();
			Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
					.isEqualTo(Status.ALREADY_EXISTS.getCode());
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	/**
 	* not working, test environment doesn't behave as runtime environment
 	* */
	@DisplayName("Create WarehouseProduct Expecting grpc aborted")
	@Test
	public void warehouseProductViolateConstraintCreateTestExpectingGRPCAborted() {
		System.out.println(productRepository.findById(6L).orElseThrow());
		System.out.println(warehouseRepository.findById(1L).orElseThrow());
		System.out.println(warehouseProductRepository.findAll());
		CreateWarehouseProductRequest request = CreateWarehouseProductRequest.newBuilder() // this combination of warehouse and position is already in the repository
				.setWarehouseId(1).setProductId(6)
				.setQuantity(3).setMinimumQuantity(3)
				.setWarehousePosition("C0102").build();
		StreamRecorder<WarehouseProductResponse> responseObserver = StreamRecorder.create();
		service.createWarehouseProduct(request,responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}
			System.out.println(warehouseProductRepository.getReferenceById(new WarehouseProductID(6,1)));
			Assertions.assertThat(responseObserver.getError()).isNotNull();
			System.out.println(responseObserver.getError().getMessage());
			Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
					.isEqualTo(Status.ABORTED.getCode());
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("Create WarehouseProduct without errors expecting WarehouseProductResponse")
	@Test
	public void warehouseProductCreateTest() {
		CreateWarehouseProductRequest request = CreateWarehouseProductRequest.newBuilder()
				.setWarehouseId(2).setProductId(3)
				.setQuantity(3).setMinimumQuantity(3)
				.setWarehousePosition("C0101").build();
		StreamRecorder<WarehouseProductResponse> responseObserver = StreamRecorder.create();
		service.createWarehouseProduct(request,responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}

			Assertions.assertThat(responseObserver.getError()).isNull();
			List<WarehouseProductResponse> results = responseObserver.getValues();
			Assertions.assertThat(results.size()).isEqualTo(1);
			WarehouseProductResponse response = results.get(0);
			Assertions.assertThat(new long[] {response.getProduct().getId(),response.getWarehouse().getWarehouseId()})
					.isEqualTo(new long[] {3, 2});
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("Alter WarehouseProduct expecting grpc not found")
	@Test
	public void warehouseProductCouldBeFoundOnAlterTestExpectingGRPCNotFound() {
		CreateWarehouseProductRequest request = CreateWarehouseProductRequest.newBuilder()
				.setWarehouseId(2).setProductId(4) // this id combination is already in the repository
				.setQuantity(3).setMinimumQuantity(3)
				.setWarehousePosition("C0101").build();
		StreamRecorder<WarehouseProductResponse> responseObserver = StreamRecorder.create();
		service.alterWarehouseProduct(request,responseObserver);
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

	@DisplayName("Alter WarehouseProduct without errors expecting WarehouseProductResponse")
	@Test
	public void warehouseProductAlterTest() {
		CreateWarehouseProductRequest request = CreateWarehouseProductRequest.newBuilder()
				.setWarehouseId(2).setProductId(6)
				.setQuantity(0).setMinimumQuantity(3)
				.setWarehousePosition("C0101").build();
		StreamRecorder<WarehouseProductResponse> responseObserver = StreamRecorder.create();
		service.alterWarehouseProduct(request,responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}

			Assertions.assertThat(responseObserver.getError()).isNull();
			List<WarehouseProductResponse> results = responseObserver.getValues();
			Assertions.assertThat(results.size()).isEqualTo(1);
			WarehouseProductResponse response = results.get(0);
			Assertions.assertThat(response.getQuantity()).isEqualTo(
					0);
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("get WarehouseProduct by id expecting grpc not found, as id combination doesn't exist")
	@Test
	public void warehouseProductGetByIdExpectingNotFound(){
		GetWarehouseProductRequest request = GetWarehouseProductRequest.newBuilder()
				.setProductId(7).setWarehouseId(1).build(); // this id combination doesn't exist
		StreamRecorder<WarehouseProductResponse> responseObserver = StreamRecorder.create();
		service.getWarehouseProduct(request,responseObserver);
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

	@DisplayName("get WarehouseProduct by id expecting WarehouseProductResponse")
	@Test
	public void warehouseProductGetById(){
		GetWarehouseProductRequest request = GetWarehouseProductRequest.newBuilder()
				.setProductId(3).setWarehouseId(1).build();
		StreamRecorder<WarehouseProductResponse> responseObserver = StreamRecorder.create();
		service.getWarehouseProduct(request,responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}

			Assertions.assertThat(responseObserver.getError()).isNull();
			List<WarehouseProductResponse> results = responseObserver.getValues();
			Assertions.assertThat(results.size()).isEqualTo(1);
			WarehouseProductResponse response = results.get(0);
			Assertions.assertThat(new long[] {response.getProduct().getId(),response.getWarehouse().getWarehouseId()})
					.isEqualTo(new long[] {3, 1});
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("get WarehouseProduct by warehouse id expecting grpc not found, as id doesn't match")
	@Test
	public void warehouseProductGetByWarehouseIdExpectingNotFound(){
		QueryByPartialIdRequest request = QueryByPartialIdRequest.newBuilder()
				.setId(3).build(); // this id doesn't exist
		StreamRecorder<GetWarehouseProductsResponse> responseObserver = StreamRecorder.create();
		service.getWarehouseProductByWarehouseId(request,responseObserver);
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

	@DisplayName("get WarehouseProduct by warehouse id expecting WarehouseProductResponse")
	@Test
	public void warehouseProductGetByWarehouseId(){
		QueryByPartialIdRequest request = QueryByPartialIdRequest.newBuilder()
				.setId(1).build();
		StreamRecorder<GetWarehouseProductsResponse> responseObserver = StreamRecorder.create();
		service.getWarehouseProductByWarehouseId(request,responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}

			Assertions.assertThat(responseObserver.getError()).isNull();
			List<GetWarehouseProductsResponse> results = responseObserver.getValues();
			Assertions.assertThat(results.size()).isEqualTo(1);
			GetWarehouseProductsResponse response = results.get(0);
			Assertions.assertThat(response.getWarehouseProductsCount()).isGreaterThan(0);
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}

	@DisplayName("get WarehouseProduct by product id expecting grpc not found, as id doesn't match")
	@Test
	public void warehouseProductGetByProductIdExpectingNotFound(){
		QueryByPartialIdRequest request = QueryByPartialIdRequest.newBuilder()
				.setId(9).build(); // this id doesn't exist
		StreamRecorder<GetWarehouseProductsResponse> responseObserver = StreamRecorder.create();
		service.getWarehouseProductByProductId(request,responseObserver);
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

	@DisplayName("get WarehouseProduct by product id expecting WarehouseProductResponse")
	@Test
	public void warehouseProductGetByProductId(){
		QueryByPartialIdRequest request = QueryByPartialIdRequest.newBuilder()
				.setId(3).build();
		StreamRecorder<GetWarehouseProductsResponse> responseObserver = StreamRecorder.create();
		service.getWarehouseProductByProductId(request,responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}

			Assertions.assertThat(responseObserver.getError()).isNull();
			List<GetWarehouseProductsResponse> results = responseObserver.getValues();
			Assertions.assertThat(results.size()).isEqualTo(1);
			GetWarehouseProductsResponse response = results.get(0);
			Assertions.assertThat(response.getWarehouseProductsCount()).isGreaterThan(0);
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}
}