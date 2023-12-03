package groupid.sep3java.gRPCServices;

import groupid.sep3java.exceptions.NotFoundException;
import groupid.sep3java.gRPCFactory.GRPCCustomerFactory;
import groupid.sep3java.models.Customer;
import groupid.sep3java.repositories.CustomerRepository;
import grpc.Customer.*;
import grpc.CustomersGrpcServiceGrpc.*;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.reflection.v1alpha.ErrorResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
public class CustomerServiceImpl extends CustomersGrpcServiceImplBase {
	private final CustomerRepository repository;

	public CustomerServiceImpl(CustomerRepository repository) {
		this.repository = repository;
	}

	@Override public void createCustomer(CreateCustomerRequest request,
			StreamObserver<CustomerResponse> responseObserver) {
		Customer customerToCreate = GRPCCustomerFactory.create(request);
		Customer customer = repository.save(customerToCreate);

		CustomerResponse response = GRPCCustomerFactory.createCustomerResponse(customer);
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override public void alterCustomer(AlterCustomerRequest request,
			StreamObserver<CustomerResponse> responseObserver) {
		try {
			repository.findById(request.getId()).orElseThrow(() -> new NotFoundException("Customer with id:" + request.getId() + "was not found"));
			Customer customerToAlter = GRPCCustomerFactory.createAlteration(request);
			Customer customer = repository.save(customerToAlter);

			CustomerResponse response = GRPCCustomerFactory.createCustomerResponse(customer);
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}
		catch (NotFoundException e) {
			Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
			ErrorResponse errorResponse = ErrorResponse.newBuilder().setErrorMessage(e.getMessage()).build();
			Metadata metadata = new Metadata();
			metadata.put(errorResponseKey, errorResponse);
			responseObserver.onError(Status.NOT_FOUND.withDescription("Customer was not found")
					.asRuntimeException(metadata));
		}
	}

	@Override public void getCustomer(GetCustomerRequest request,
			StreamObserver<CustomerResponse> responseObserver) {
		try {
			Customer customer = repository.findById(request.getId()).orElseThrow(() -> new NotFoundException("Customer with id:" + request.getId() + "was not found"));
			CustomerResponse response = GRPCCustomerFactory.createCustomerResponse(customer);
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}
		catch (NotFoundException e) {
			Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
			ErrorResponse errorResponse = ErrorResponse.newBuilder().setErrorMessage(e.getMessage()).build();
			Metadata metadata = new Metadata();
			metadata.put(errorResponseKey, errorResponse);
			responseObserver.onError(Status.NOT_FOUND.withDescription("Customer was not found")
					.asRuntimeException(metadata));
		}
	}

	@Override public void getCustomers(GetCustomersRequest request,
			StreamObserver<GetCustomersResponse> responseObserver) {
		List<Customer> customers = repository.findAll();

		GetCustomersResponse response = GRPCCustomerFactory.createGetCustomersResponse(customers);
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
