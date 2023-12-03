package groupid.sep3java.gRPCFactory;

import grpc.Product.*;
import groupid.sep3java.models.Product;

import java.util.List;

public class GRPCProductFactory {

	private GRPCProductFactory() {
	}

	public static Product create(CreateProductRequest request){
		Product newProduct = new Product(request.getName(),request.getDescription(),
				request.getPrice());
		return newProduct;
	}

	public static ProductResponse createProductResponse(Product product) {
		ProductResponse productResponse = ProductResponse.newBuilder()
				.setId(product.getId())
				.setName(product.getName())
				.setDescription(product.getDescription())
				.setPrice(product.getPrice())
				.build();
		return productResponse;
	}

	public static GetProductsResponse createGetProductsResponse(
			List<Product> products) {
		GetProductsResponse.Builder builder = GetProductsResponse.newBuilder();
		for (Product product : products){
			builder.addProducts(createProductResponse(product));
		}
		GetProductsResponse getProductsResponse = builder.build();
		return getProductsResponse;
	}
}
