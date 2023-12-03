package groupid.sep3java.services;

import groupid.sep3java.gRPCServices.CustomerServiceImpl;
import groupid.sep3java.gRPCServices.WarehouseServiceImpl;
import groupid.sep3java.models.Warehouse;
import groupid.sep3java.repositories.WarehouseRepository;
import grpc.Customer;
import grpc.WarehouseProduct;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.internal.testing.StreamRecorder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
public class WarehouseServiceImplTest {
	@Autowired WarehouseRepository repository;

	private WarehouseServiceImpl service;

	@BeforeAll public void SetUp() {
		ArrayList<Warehouse> warehouses = new ArrayList<>();
		warehouses.add(new Warehouse("Via Warehouse", "Banegårdsgade 2, 8700 Horsens, Danmark"));
		warehouses.add(new Warehouse("Test Warehouse", "Testadresse 123, 8700 Horsens, Danmark"));
		repository.saveAll(warehouses);

		service = new WarehouseServiceImpl(repository);
	}

	@DisplayName("Create Warehouse Expecting WarehouseResponse")
	@Test public void createWarehouseExpectingWarehouseResponse() throws Exception {
		grpc.Warehouse.CreateWarehouseRequest createWarehouseRequest = grpc.Warehouse.CreateWarehouseRequest.newBuilder()
				.setName("Salling Group").setAddress("Something Street 56 2900")
				.build();
		StreamRecorder<grpc.Warehouse.WarehouseResponse> responseObserver = StreamRecorder.create();
		service.createWarehouse(createWarehouseRequest, responseObserver);
		if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
			Assertions.fail("The call did not terminate in time");
		}
		Assertions.assertThat(responseObserver.getError()).isNull();
		List<grpc.Warehouse.WarehouseResponse> results = responseObserver.getValues();
		Assertions.assertThat(results.size()).isEqualTo(1);
		grpc.Warehouse.WarehouseResponse response = results.get(0);
		Assertions.assertThat(response).isEqualTo(
				grpc.Warehouse.WarehouseResponse.newBuilder().setWarehouseId(response.getWarehouseId())
						.setName(createWarehouseRequest.getName())
						.setAddress(createWarehouseRequest.getAddress()).build());
	}

	@DisplayName("Get Warehouse By Id Expecting WarehouseResponse")
	@Test public void getWarehouseByIdExpectingWarehouseResponse () throws Exception{
		grpc.Warehouse.GetWarehouseRequest getWarehouseRequest = grpc.Warehouse.GetWarehouseRequest.newBuilder()
				.setWarehouseId(2).build();

		StreamRecorder<grpc.Warehouse.WarehouseResponse> responseObserver = StreamRecorder.create();
		service.getWarehouse(getWarehouseRequest, responseObserver);
		if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
			Assertions.fail("The call did not terminate in time");
		}
		Assertions.assertThat(responseObserver.getError()).isNull();
		List<grpc.Warehouse.WarehouseResponse> results = responseObserver.getValues();
		Assertions.assertThat(results.size()).isEqualTo(1);
		grpc.Warehouse.WarehouseResponse response = results.get(0);
		Assertions.assertThat(response).isEqualTo(
				grpc.Warehouse.WarehouseResponse.newBuilder().setWarehouseId(2)
						.setName("Test Warehouse")
						.setAddress("Testadresse 123, 8700 Horsens, Danmark")
						.build());

	}

	@DisplayName("Get Warehouse With Invalid Id Expecting GRPC NotFound")
	@Test public void getWarehouseWithInvalidIdExpectingGRPCNotFound() throws Exception {
		grpc.Warehouse.GetWarehouseRequest getWarehouseRequest = grpc.Warehouse.GetWarehouseRequest.newBuilder()
				.setWarehouseId(5) // No SuchId
				.build();

		StreamRecorder<grpc.Warehouse.WarehouseResponse> responseObserver = StreamRecorder.create();
		service.getWarehouse(getWarehouseRequest, responseObserver);
		if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
			Assertions.fail("The call did not terminate in time");
		}
		Assertions.assertThat(responseObserver.getError()).isNotNull();
		Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
				.isEqualTo(Status.NOT_FOUND.getCode());
	}

	@DisplayName("Get Warehouses Expecting GetWarehousesResponse With More Than Zero Warehouses")
	@Test public void getWarehousesExpectingGetWarehousesResponseWithMoreThanZeroWarehouses () throws Exception{
		grpc.Warehouse.GetWarehousesRequest getWarehousesRequest = grpc.Warehouse.GetWarehousesRequest.newBuilder().build();


		StreamRecorder<grpc.Warehouse.GetWarehousesResponse> responseObserver = StreamRecorder.create();
		service.getWarehouses(getWarehousesRequest, responseObserver);
		if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
			Assertions.fail("The call did not terminate in time");
		}
		Assertions.assertThat(responseObserver.getError()).isNull();
		List<grpc.Warehouse.GetWarehousesResponse> results = responseObserver.getValues();
		Assertions.assertThat(results.size()).isEqualTo(1);
		grpc.Warehouse.GetWarehousesResponse response = results.get(0);
		Assertions.assertThat(response.getWarehousesCount()).isGreaterThan(0);
	}

}



