package groupid.sep3java.gRPCFactory;

import groupid.sep3java.models.Customer;
import grpc.Customer.*;

import java.util.List;

public class GRPCCustomerFactory {
	private GRPCCustomerFactory() {
	}

	public static Customer create(CreateCustomerRequest request) {
		Customer customer = new Customer(request.getFullname(),request.getPhoneNo(),
				request.getAddress(), request.getMail());
		return customer;
	}

	public static Customer createAlteration(AlterCustomerRequest request) {
		Customer customer = new Customer(request.getFullname(),request.getPhoneNo(),
				request.getAddress(), request.getMail());
		customer.setId(request.getId());
		return customer;
	}

	public static CustomerResponse createCustomerResponse(Customer customer) {
		CustomerResponse customerResponse = CustomerResponse.newBuilder()
				.setId(customer.getId()).setFullname(customer.getFullName())
				.setPhoneNo(customer.getPhoneNo()).setAddress(customer.getAddress())
				.setMail(customer.getMail()).build();
		return customerResponse;
	}

	public static GetCustomersResponse createGetCustomersResponse(List<Customer> customers) {
		GetCustomersResponse.Builder builder = GetCustomersResponse.newBuilder();
		for (Customer customer : customers)
		{
		    builder.addCustomers(createCustomerResponse(customer));
		}
		GetCustomersResponse response = builder.build();
		return response;
	}
}
