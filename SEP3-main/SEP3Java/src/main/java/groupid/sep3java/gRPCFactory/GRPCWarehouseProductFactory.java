package groupid.sep3java.gRPCFactory;

import groupid.sep3java.models.*;
import grpc.WarehouseProduct.*;
import grpc.Product.*;
import grpc.Warehouse.*;

import java.util.List;

public class GRPCWarehouseProductFactory {

	private GRPCWarehouseProductFactory() {
	}

	public static WarehouseProduct create(CreateWarehouseProductRequest request,
			Product product, Warehouse warehouse) {
		WarehouseProduct newWarehouseProduct = new WarehouseProduct(product,
				warehouse, request.getQuantity(), request.getMinimumQuantity(),
				request.getWarehousePosition());
		return newWarehouseProduct;
	}

	public static WarehouseProductResponse createWarehouseProductResponse(
			WarehouseProduct warehouseProduct) {
		Product product = warehouseProduct.getProductId();
		Warehouse warehouse = warehouseProduct.getWarehouseId();
		WarehouseProductResponse warehouseProductResponse = WarehouseProductResponse.newBuilder()
				.setProduct(ProductResponse.newBuilder()
						.setId(product.getId()).setName(product.getName())
						.setDescription(product.getDescription())
						.setPrice(product.getPrice()).build())
				.setWarehouse(WarehouseResponse.newBuilder()
						.setWarehouseId(warehouse.getId()).setName(warehouse.getName())
						.setAddress(warehouse.getAddress()).build())
				.setQuantity(warehouseProduct.getQuantity())
				.setMinimumQuantity(warehouseProduct.getMinimumQuantity())
				.setWarehousePosition(warehouseProduct.getWarehousePosition()).build();

		return warehouseProductResponse;
	}

	public static GetWarehouseProductsResponse createGetWarehouseProductsResponse(
			List<WarehouseProduct> warehouseProducts) {
		GetWarehouseProductsResponse.Builder builder = GetWarehouseProductsResponse.newBuilder();
		for (WarehouseProduct warehouseProduct : warehouseProducts) {
			builder.addWarehouseProducts(
					createWarehouseProductResponse(warehouseProduct));
		}
		GetWarehouseProductsResponse getWarehouseProductsResponse = builder.build();
		return getWarehouseProductsResponse;
	}

}




