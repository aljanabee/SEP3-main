package groupid.sep3java.services;

import groupid.sep3java.gRPCServices.CustomerServiceImpl;
import groupid.sep3java.gRPCServices.ProductServiceImpl;
import groupid.sep3java.models.Product;
import groupid.sep3java.repositories.CustomerRepository;
import groupid.sep3java.repositories.ProductRepository;
import grpc.Customer;
import grpc.Product.GetProductsRequest;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS) @DataJpaTest  public class ProductServiceImplTest {
	@Autowired ProductRepository repository;

	private ProductServiceImpl service;

	@BeforeAll public void SetUp() {

		ArrayList<Product> products = new ArrayList<>();
		products.add(new Product("Bose Acoustimass 5 Series III Speaker System - AM53BK", "A description of the product...", 399));
		products.add(new Product("Bose 27028 161 Bookshelf Pair Speakers In White - 161WH", "A description of the product...", 158));
		products.add(new Product("Panasonic Integrated Telephone System - KXTS108W", "A description of the product...", 44));
		products.add(new Product("Panasonic Hands-Free Headset - KXTCA86", "A description of the product...", 14.95));
		products.add(new Product("Sanus Euro Foundations Satellite Speaker Stand - EFSATS", "A description of the product...", 79.99));
		repository.saveAll(products);

		service = new ProductServiceImpl(repository);
	}

	@DisplayName("Create Product Expecting ProductResponse")
	@Test public void createProductExpectingProductResponse() throws Exception {
		grpc.Product.CreateProductRequest createProductRequest = grpc.Product.CreateProductRequest.newBuilder()
				.setName("OnePlus 11 Pro max")
				.setDescription("Much better then Apple, with Hasselblad QuadCams")
				.setPrice(6500).build();
				
		StreamRecorder<grpc.Product.ProductResponse> responseObserver = StreamRecorder.create();
		service.createProduct(createProductRequest, responseObserver);
		if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
			Assertions.fail("The call did not terminate in time");
		}
		Assertions.assertThat(responseObserver.getError()).isNull();
		List<grpc.Product.ProductResponse> results = responseObserver.getValues();
		Assertions.assertThat(results.size()).isEqualTo(1);
		grpc.Product.ProductResponse response = results.get(0);
		Assertions.assertThat(response).isEqualTo(
				grpc.Product.ProductResponse.newBuilder().setId(response.getId())
						.setName(createProductRequest.getName())
						.setDescription(createProductRequest.getDescription())
						.setPrice(createProductRequest.getPrice())
						.build());
	}

	@DisplayName("Get Product By Id Expecting ProductResponse")
	@Test public void getProductByIdExpectingProductResponse ()throws Exception
	{
		grpc.Product.GetProductRequest getProductRequest = grpc.Product.GetProductRequest.newBuilder()
				.setId(2).build();

		StreamRecorder<grpc.Product.ProductResponse> responseObserver = StreamRecorder.create();
		service.getProduct(getProductRequest, responseObserver);
		if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
			Assertions.fail("The call did not terminate in time");
		}
		Assertions.assertThat(responseObserver.getError()).isNull();
		List<grpc.Product.ProductResponse> results = responseObserver.getValues();
		Assertions.assertThat(results.size()).isEqualTo(1);
		grpc.Product.ProductResponse response = results.get(0);
		Assertions.assertThat(response).isEqualTo(
				grpc.Product.ProductResponse.newBuilder().setId(response.getId())
						.setName("Bose 27028 161 Bookshelf Pair Speakers In White - 161WH")
						.setDescription("A description of the product...")
						.setPrice(158.00)
						.build());

	}
	@DisplayName("Get Product By Invalid Id Expecting GRPC NotFound")
	@Test public void getProductByInvalidIdExpectingGRPCNotFound() throws Exception {

		grpc.Product.GetProductRequest getProductRequest = grpc.Product.GetProductRequest.newBuilder()
				.setId(99).build();// no such Id exists
		StreamRecorder<grpc.Product.ProductResponse> responseObserver = StreamRecorder.create();
		service.getProduct(getProductRequest, responseObserver);
		if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
			Assertions.fail("The call did not terminate in time");
		}
		Assertions.assertThat(responseObserver.getError()).isNotNull();
		Assertions.assertThat(((StatusRuntimeException) responseObserver.getError()).getStatus().getCode())
				.isEqualTo(Status.NOT_FOUND.getCode());
	}

	@DisplayName("Get Products Expecting GetProductResponse With More Than Zero Products")
	@Test public void getProductsExpectingGetProductResponseWithMoreThanZeroProducts() throws Exception	{
		GetProductsRequest getProductsRequest = GetProductsRequest.newBuilder().build();


		StreamRecorder<grpc.Product.GetProductsResponse> responseObserver = StreamRecorder.create();
		service.getProducts(getProductsRequest, responseObserver);
		if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
			Assertions.fail("The call did not terminate in time");
		}
		Assertions.assertThat(responseObserver.getError()).isNull();
		List<grpc.Product.GetProductsResponse> result = responseObserver.getValues();
		Assertions.assertThat(result.size()).isEqualTo(1);
		grpc.Product.GetProductsResponse response = result.get(0);
		Assertions.assertThat(response.getProductsCount()).isGreaterThan(0);
	}


}

