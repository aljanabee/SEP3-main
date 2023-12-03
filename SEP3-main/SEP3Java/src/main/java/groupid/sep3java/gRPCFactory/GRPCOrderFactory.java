package groupid.sep3java.gRPCFactory;

import groupid.sep3java.models.Customer;
import groupid.sep3java.models.Order;
import groupid.sep3java.models.Product;
import groupid.sep3java.models.Warehouse;
import grpc.Order.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class GRPCOrderFactory {
	private GRPCOrderFactory() {
	}

	public static Order create(CreateOrderRequest request, Customer customer, Warehouse warehouse, List<Product> orderedProducts) {
		LocalDateTime dateTimeOrdered = null;
		LocalDateTime dateTimeSent = null;
		if (request.getDateTimeOrdered() != 0) {
			dateTimeOrdered = LocalDateTime.ofEpochSecond(request.getDateTimeOrdered(), 0, ZoneOffset.UTC);
		}
		if (request.getDateTimeSent() != 0) {
			dateTimeSent = LocalDateTime.ofEpochSecond(request.getDateTimeSent(), 0, ZoneOffset.UTC);
		}
		Order order = new Order(customer, warehouse, dateTimeOrdered, request.getIsPacked(), dateTimeSent, orderedProducts);
		return order;
	}
	public static Order create(UpdateOrderRequest request, Customer customer, Warehouse warehouse, List<Product> orderedProducts) {
		LocalDateTime dateTimeOrdered = null;
		LocalDateTime dateTimeSent = null;
		if (request.getDateTimeOrdered() != 0) {
			dateTimeOrdered = LocalDateTime.ofEpochSecond(request.getDateTimeOrdered(), 0, ZoneOffset.UTC);
		}
		if (request.getDateTimeSent() != 0) {
			dateTimeSent = LocalDateTime.ofEpochSecond(request.getDateTimeSent(), 0, ZoneOffset.UTC);
		}
		Order order = new Order(customer, warehouse, dateTimeOrdered, request.getIsPacked(), dateTimeSent, orderedProducts);
		order.setId(request.getId());
		return order;
	}

	public static OrderResponse createOrderResponse(Order order) {
		Customer customer = order.getCustomer();
		Warehouse warehouse = order.getWarehouse();
		long dateTimeOrdered = 0;
		long dateTimeSent = 0;
		if (order.getDateOrdered() != null && order.getTimeOrdered() != null) {
			dateTimeOrdered = LocalDateTime.of(order.getDateOrdered(), order.getTimeOrdered()).toEpochSecond(ZoneOffset.UTC);
		}
		if (order.getDateSent() != null && order.getTimeSent() != null) {
			dateTimeSent = LocalDateTime.of(order.getDateSent(), order.getTimeSent()).toEpochSecond(ZoneOffset.UTC);
		}

		OrderResponse orderResponse = OrderResponse.newBuilder()
				.setId(order.getId())
				.setCustomer(grpc.Customer.CustomerResponse.newBuilder()
						.setId(customer.getId())
						.setFullname(customer.getFullName())
						.setPhoneNo(customer.getPhoneNo())
						.setAddress(customer.getAddress())
						.setMail(customer.getMail()).build())
				.setWarehouse(grpc.Warehouse.WarehouseResponse.newBuilder()
						.setWarehouseId(warehouse.getId())
						.setName(warehouse.getName())
						.setAddress(warehouse.getAddress()).build())
				.setDateTimeOrdered(dateTimeOrdered)
				.setIsPacked(order.isPacked())
				.setDateTimeSent(dateTimeSent)
				.setProducts(GRPCProductFactory.createGetProductsResponse(order.getOrderedProducts()))
				.build();
		return orderResponse;
	}

	public static GetOrdersResponse createGetOrdersResponse(List<Order> orders) {
		GetOrdersResponse.Builder builder = GetOrdersResponse.newBuilder();
		for (Order order : orders)
		{
		    builder.addOrders(createOrderResponse(order));
		}
		GetOrdersResponse response = builder.build();
		return response;
	}

	public static UpdateOrderResponse createUpdateOrderResponse(String responseMessage) {
		UpdateOrderResponse updateOrderResponse = UpdateOrderResponse.newBuilder()
				.setResponseMessage(responseMessage)
				.build();
		return updateOrderResponse;
	}
}
