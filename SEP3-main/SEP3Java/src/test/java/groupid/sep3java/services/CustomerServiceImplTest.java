package groupid.sep3java.services;

import groupid.sep3java.gRPCServices.CustomerServiceImpl;
import groupid.sep3java.repositories.CustomerRepository;
import grpc.Customer;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS) @DataJpaTest public class CustomerServiceImplTest {
	@Autowired CustomerRepository repository;

	private CustomerServiceImpl service;

	@BeforeAll public void SetUp() {
		ArrayList<groupid.sep3java.models.Customer> customers = new ArrayList<>();
		customers.add(new groupid.sep3java.models.Customer("Peter Griffin", "12131456", "31 Spooner Street Quahog", "Griffen@gmail.com"));
		customers.add(new groupid.sep3java.models.Customer("Joe Swanson", "64859635", "33 Spooner Street Quahog", "Swanson@gmail.com"));
		customers.add(new groupid.sep3java.models.Customer("Cleveland Brown", "78964532", "29 Spooner Street Quahog", "Brown@gmail.com"));

		repository.saveAll(customers);

		service = new CustomerServiceImpl(repository);
	}

	@DisplayName("Create Customer Expecting CustomerResponse")
	@Test public void createCustomerExpectingCustomerResponse() throws Exception {
		grpc.Customer.CreateCustomerRequest createCustomerRequest = grpc.Customer.CreateCustomerRequest.newBuilder()
				.setFullname("Peter Griffin").setPhoneNo("12131456")
				.setAddress("31 Spooner Street Quahog").setMail("Griffen@gmail.com")
				.build();
		StreamRecorder<Customer.CustomerResponse> responseObserver = StreamRecorder.create();
		service.createCustomer(createCustomerRequest, responseObserver);
		if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
			Assertions.fail("The call did not terminate in time");
		}
		Assertions.assertThat(responseObserver.getError()).isNull();
		List<Customer.CustomerResponse> results = responseObserver.getValues();
		Assertions.assertThat(results.size()).isEqualTo(1);
		grpc.Customer.CustomerResponse response = results.get(0);
		Assertions.assertThat(response).isEqualTo(
				grpc.Customer.CustomerResponse.newBuilder().setId(4)
						.setFullname("Peter Griffin").setPhoneNo("12131456")
						.setAddress("31 Spooner Street Quahog").setMail("Griffen@gmail.com")
						.build());
	}

	@DisplayName("Get Customer By Id Expecting CustomerResponse")
	@Test public void getCustomerByIdExpectingCustomerResponse() throws Exception {

		Customer.GetCustomerRequest getCustomerRequest = Customer.GetCustomerRequest.newBuilder()
				.setId(2).build();

		StreamRecorder<Customer.CustomerResponse> responseObserver = StreamRecorder.create();
		service.getCustomer(getCustomerRequest, responseObserver);
		if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
			Assertions.fail("The call did not terminate in time");
		}
		Assertions.assertThat(responseObserver.getError()).isNull();
		List<Customer.CustomerResponse> results = responseObserver.getValues();
		Assertions.assertThat(results.size()).isEqualTo(1);
		grpc.Customer.CustomerResponse response = results.get(0);
		Assertions.assertThat(response).isEqualTo(
				grpc.Customer.CustomerResponse.newBuilder().setId(2)
						.setFullname("Joe Swanson").setPhoneNo("64859635")
						.setAddress("33 Spooner Street Quahog").setMail("Swanson@gmail.com")
						.build());

	}

	@DisplayName("Get Customer With Invalid Id Expecting GRPC NotFound")
	@Test public void getCustomerWithInvalidIdExpectingGRPCNotFound() throws Exception {

		Customer.GetCustomerRequest getCustomerRequest = Customer.GetCustomerRequest.newBuilder()
				.setId(5).build();//no such Id exists
		StreamRecorder<Customer.CustomerResponse> responseObserver = StreamRecorder.create();
		service.getCustomer(getCustomerRequest, responseObserver);
		if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
			Assertions.fail("The call did not terminate in time");
		}
		Assertions.assertThat(responseObserver.getError()).isNotNull();
		Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
				.isEqualTo(Status.NOT_FOUND.getCode());

	}


	@DisplayName("Get Customers Expecting GetCustomersResponse With More Than Zero Customers")
	@Test public void getCustomersExpectingGetCustomersResponseWithMoreThanZeroCustomers() throws Exception	{
		Customer.GetCustomersRequest getCustomersRequest = Customer.GetCustomersRequest.newBuilder().build();


		StreamRecorder<Customer.GetCustomersResponse> responseObserver = StreamRecorder.create();
		service.getCustomers(getCustomersRequest, responseObserver);
		if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
			Assertions.fail("The call did not terminate in time");
		}
		Assertions.assertThat(responseObserver.getError()).isNull();
		List<Customer.GetCustomersResponse> results = responseObserver.getValues();
		Assertions.assertThat(results.size()).isEqualTo(1);
		Customer.GetCustomersResponse response = results.get(0);
		Assertions.assertThat(response.getCustomersCount()).isEqualTo(3);

	}

	@DisplayName("Alter Customer Test Expecting NotFound")
	@Test public void alterCustomerTestExpectingNotFound() {
		Customer.AlterCustomerRequest alterCustomerRequest = Customer.AlterCustomerRequest.newBuilder()
				.setId(99) // This id doesn't exist
				.setFullname("Walter white").setPhoneNo("20082013")
				.setAddress("meth st").setMail("cook@jesse.gov").build();
		StreamRecorder<Customer.CustomerResponse> responseObserver = StreamRecorder.create();
		service.alterCustomer(alterCustomerRequest, responseObserver);
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

	@DisplayName("Alter Customer Test Expecting CustomerResponse")
	@Test public void alterCustomerTestExpectingCustomerResponse() {
		Customer.AlterCustomerRequest alterCustomerRequest = Customer.AlterCustomerRequest.newBuilder()
				.setId(2).setFullname("Walter white").setPhoneNo("20082013")
				.setAddress("meth st").setMail("cook@jesse.gov").build();
		StreamRecorder<Customer.CustomerResponse> responseObserver = StreamRecorder.create();
		service.alterCustomer(alterCustomerRequest, responseObserver);
		try {
			if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
				Assertions.fail("The call did not terminate in time");
			}

			Assertions.assertThat(responseObserver.getError()).isNull();
			List<Customer.CustomerResponse> results = responseObserver.getValues();
			Assertions.assertThat(results.size()).isEqualTo(1);
			grpc.Customer.CustomerResponse response = results.get(0);
			Assertions.assertThat(response).isEqualTo(
					grpc.Customer.CustomerResponse.newBuilder().setId(2)
							.setFullname("Walter white").setPhoneNo("20082013")
							.setAddress("meth st").setMail("cook@jesse.gov").build());
		}
		catch (Exception e){
			Assertions.fail(e.getMessage());
		}
	}
}

