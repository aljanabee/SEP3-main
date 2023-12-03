using gRPC.ServiceInterfaces;
using Grpc.Core;
using Shared.Dtos;
using Shared.Exceptions;
using Shared.Models;

namespace gRPC.ServiceImplementations;

public class CustomerGrpcService : ICustomerGrpcService
{
    private readonly CustomersGrpcService.CustomersGrpcServiceClient _serviceClient;

    public CustomerGrpcService(CustomersGrpcService.CustomersGrpcServiceClient serviceClient)
    {
        _serviceClient = serviceClient;
    }

    public async Task<Customer> CreateCustomerAsync(CustomerCreationDto dto)
    {
        try {
            CustomerResponse reply = await _serviceClient.CreateCustomerAsync(new CreateCustomerRequest()
            { 
                Fullname = dto.FullName,
                PhoneNo = dto.PhoneNo,
                Address = dto.Address,
                Mail = dto.Mail
            });
            Customer customer = new Customer()
            {
                Id = reply.Id,
                FullName = reply.Fullname,
                PhoneNo = reply.PhoneNo,
                Address = reply.Address,
                Mail = reply.Mail
            };
            return customer;
        }
        catch (RpcException e) {
            if (e.StatusCode == StatusCode.Unavailable) {
                throw new ServiceUnavailableException();
            }
            throw;
        }
    }

    public async Task<Customer> AlterCustomerAsync(Customer customer) {
        try {
            CustomerResponse reply = await _serviceClient.AlterCustomerAsync(new AlterCustomerRequest {
                Id = customer.Id,
                Fullname = customer.FullName,
                Address = customer.Address,
                Mail = customer.Mail,
                PhoneNo = customer.PhoneNo
            });
            Customer returnCustomer = new Customer()
            {
                Id = reply.Id,
                FullName = reply.Fullname,
                PhoneNo = reply.PhoneNo,
                Address = reply.Address,
                Mail = reply.Mail
            };
            return returnCustomer;
        }
        catch (RpcException e) {
            if (e.StatusCode == StatusCode.Unavailable) {
                throw new ServiceUnavailableException();
            }
            if (e.StatusCode == StatusCode.NotFound) {
                throw new NotFoundException(e.Status.Detail);
            }
            throw;
        }
    }

    public async Task<IEnumerable<Customer>> GetCustomersAsync()
    {
        try {
            GetCustomersResponse reply = await _serviceClient.GetCustomersAsync(new GetCustomersRequest());

            List<Customer> customers = new();
            foreach (CustomerResponse pr in reply.Customers)
            {
                customers.Add(new Customer()
                    {
                        Id = pr.Id,
                        Address = pr.Address,
                        FullName = pr.Fullname,
                        Mail = pr.Mail,
                        PhoneNo = pr.PhoneNo
                    });
            }

            return customers.AsEnumerable();
        }
        catch (RpcException e) {
            if (e.StatusCode == StatusCode.Unavailable) {
                throw new ServiceUnavailableException();
            }
            throw;
        }
    }

    public async Task<Customer> GetCustomerByIdAsync(long id)
    {
        try {
            CustomerResponse reply = await _serviceClient.GetCustomerAsync(new GetCustomerRequest()
            {
                Id = id
            });

            Customer customer = new Customer() {
                    Id = reply.Id,
                    FullName = reply.Fullname,
                    PhoneNo = reply.PhoneNo,
                    Address = reply.Address,
                    Mail = reply.Mail
                };

            return customer;
        }
        catch (RpcException e) {
            if (e.StatusCode == StatusCode.Unavailable) {
                throw new ServiceUnavailableException();
            }
            if (e.StatusCode == StatusCode.NotFound) {
                throw new NotFoundException(e.Status.Detail);
            }
            throw;
        }
    }
}