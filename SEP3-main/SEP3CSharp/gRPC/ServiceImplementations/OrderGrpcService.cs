using gRPC.ServiceInterfaces;
using Grpc.Core;
using Shared.Dtos;
using Shared.Exceptions;
using Shared.Models;
using Shared.Util;
using System.Text;

namespace gRPC.ServiceImplementations;
public class OrderGrpcService : IOrderGrpcService {

    private readonly gRPC.OrderGrpcService.OrderGrpcServiceClient _serviceClient;

    public OrderGrpcService(gRPC.OrderGrpcService.OrderGrpcServiceClient serviceClient) {
        _serviceClient = serviceClient;
    }

    public async Task<Order> CreateOrderAsync(OrderCreationDto dto) {
        try {
            CreateOrderRequest createOrderRequest = new CreateOrderRequest {
                CustomerId = dto.CustomerId,
                WarehouseId = dto.WarehouseId,
                DateTimeOrdered = DateTimeConverter.DateTimeToUnixTimeStamp(dto.DateTimeOrdered),
                IsPacked = dto.IsPacked,
                DateTimeSent = DateTimeConverter.DateTimeToUnixTimeStamp(dto.DateTimeSent),
            };
            foreach (long productId in dto.ProductIds) {
                createOrderRequest.ProductIds.Add(productId);
            }

            OrderResponse reply = await _serviceClient.CreateOrderAsync(createOrderRequest);

            List<Product> products = new();
            foreach (ProductResponse pr in reply.Products.Products) {
                products.Add(new Product {
                    Id = pr.Id,
                    Name = pr.Name,
                    Description = pr.Description,
                    Price = pr.Price
                });
            }

            Order order = new Order {
                Id = reply.Id,
                Customer = new Customer {
                    Id = reply.Customer.Id,
                    FullName = reply.Customer.Fullname,
                    PhoneNo = reply.Customer.PhoneNo,
                    Address = reply.Customer.Address,
                    Mail = reply.Customer.Mail,
                },
                Warehouse = new Warehouse {
                    Id = reply.Warehouse.WarehouseId,
                    Name = reply.Warehouse.Name,
                    Address = reply.Warehouse.Address,
                },
                DateTimeOrdered = DateTimeConverter.UnixTimeStampToDateTime(reply.DateTimeOrdered),
                IsPacked = reply.IsPacked,
                DateTimeSent = DateTimeConverter.UnixTimeStampToDateTime(reply.DateTimeSent),
                OrderedProducts = products.AsEnumerable()
            };
            return order;
        }
        catch (RpcException e) {
            if (e.StatusCode == StatusCode.Unavailable) {
                throw new ServiceUnavailableException();
            }
            if (e.StatusCode == StatusCode.NotFound) {
                var trailer = e.Trailers.Get("grpc.reflection.v1alpha.errorresponse-bin")!;
                throw new NotFoundException(e.Status.Detail + "\nDetails: " + Encoding.UTF8.GetString(trailer.ValueBytes).Substring(2));
            }
            if (e.StatusCode == StatusCode.FailedPrecondition) {
                var trailer = e.Trailers.Get("grpc.reflection.v1alpha.errorresponse-bin")!;
                throw new InsufficientStockException(e.Status.Detail + "\nDetails: " + Encoding.UTF8.GetString(trailer.ValueBytes).Substring(2));
            }
            throw;
        }
    }

    public async Task<string> UpdateOrderAsync(Order updatedOrder) {
        try {
            UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest {
                Id = updatedOrder.Id,
                CustomerId = updatedOrder.Customer.Id,
                WarehouseId = updatedOrder.Warehouse.Id,
                DateTimeOrdered = DateTimeConverter.DateTimeToUnixTimeStamp(updatedOrder.DateTimeOrdered),
                IsPacked = updatedOrder.IsPacked,
                DateTimeSent = DateTimeConverter.DateTimeToUnixTimeStamp(updatedOrder.DateTimeSent),
            };

            foreach (Product product in updatedOrder.OrderedProducts) {
                updateOrderRequest.ProductIds.Add(product.Id);
            }

            UpdateOrderResponse reply = await _serviceClient.UpdateOrderAsync(updateOrderRequest);
            return reply.ResponseMessage;
        }
        catch (RpcException e) {
            if (e.StatusCode == StatusCode.Unavailable) {
                throw new ServiceUnavailableException();
            }
            if (e.StatusCode == StatusCode.NotFound) {
                var trailer = e.Trailers.Get("grpc.reflection.v1alpha.errorresponse-bin")!;
                throw new NotFoundException(e.Status.Detail + "\nDetails: " + Encoding.UTF8.GetString(trailer.ValueBytes).Substring(2));
            }
            throw;
        }
    }

    public async Task<Order> GetOrderByIdAsync(long id) {
        try {
            OrderResponse reply = await _serviceClient.GetOrderAsync(new GetOrderRequest { Id = id });

            List<Product> products = new();
            foreach (ProductResponse pr in reply.Products.Products) {
                products.Add(new Product {
                    Id = pr.Id,
                    Name = pr.Name,
                    Description = pr.Description,
                    Price = pr.Price
                });
            }

            Order order = new Order {
                Id = reply.Id,
                Customer = new Customer {
                    Id = reply.Customer.Id,
                    FullName = reply.Customer.Fullname,
                    PhoneNo = reply.Customer.PhoneNo,
                    Address = reply.Customer.Address,
                    Mail = reply.Customer.Mail,
                },
                Warehouse = new Warehouse {
                    Id = reply.Warehouse.WarehouseId,
                    Name = reply.Warehouse.Name,
                    Address = reply.Warehouse.Address,
                },
                DateTimeOrdered = DateTimeConverter.UnixTimeStampToDateTime(reply.DateTimeOrdered),
                IsPacked = reply.IsPacked,
                DateTimeSent = DateTimeConverter.UnixTimeStampToDateTime(reply.DateTimeSent),
                OrderedProducts = products
            };
            return order;
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

    public async Task<IEnumerable<Order>> GetOrdersAsync() {
        try {
            GetOrdersResponse reply = await _serviceClient.GetOrdersAsync(new GetOrdersRequest());

            List<Order> orders = new();
            foreach (OrderResponse or in reply.Orders) {
                orders.Add(new Order {
                    Id = or.Id,
                    Customer = new Customer {
                        Id = or.Customer.Id,
                        FullName = or.Customer.Fullname,
                        PhoneNo = or.Customer.PhoneNo,
                        Address = or.Customer.Address,
                        Mail = or.Customer.Mail,
                    },
                    Warehouse = new Warehouse {
                        Id = or.Warehouse.WarehouseId,
                        Name = or.Warehouse.Name,
                        Address = or.Warehouse.Address,
                    },
                    DateTimeOrdered = DateTimeConverter.UnixTimeStampToDateTime(or.DateTimeOrdered),
                    IsPacked = or.IsPacked,
                    DateTimeSent = DateTimeConverter.UnixTimeStampToDateTime(or.DateTimeSent),
                });
            }
            
            return orders.AsEnumerable();
        }
        catch (RpcException e) {
            if (e.StatusCode == StatusCode.Unavailable) {
                throw new ServiceUnavailableException();
            }
            throw;
        }
    }

    public async Task<IEnumerable<Order>> GetOrdersByWarehouseIdAsync(long id) {
        try {
            GetOrdersResponse reply = await _serviceClient.GetOrdersByWarehouseIdAsync(new GetOrdersByWarehouseIdRequest { Id = id});

            List<Order> orders = new();
            foreach (OrderResponse or in reply.Orders) {
                orders.Add(new Order {
                    Id = or.Id,
                    Customer = new Customer {
                        Id = or.Customer.Id,
                        FullName = or.Customer.Fullname,
                        PhoneNo = or.Customer.PhoneNo,
                        Address = or.Customer.Address,
                        Mail = or.Customer.Mail,
                    },
                    Warehouse = new Warehouse {
                        Id = or.Warehouse.WarehouseId,
                        Name = or.Warehouse.Name,
                        Address = or.Warehouse.Address,
                    },
                    DateTimeOrdered = DateTimeConverter.UnixTimeStampToDateTime(or.DateTimeOrdered),
                    IsPacked = or.IsPacked,
                    DateTimeSent = DateTimeConverter.UnixTimeStampToDateTime(or.DateTimeSent),
                });
            }

            return orders.AsEnumerable();
        }
        catch (RpcException e) {
            if (e.StatusCode == StatusCode.Unavailable) {
                throw new ServiceUnavailableException();
            }
            if (e.StatusCode == StatusCode.NotFound) {
                var trailer = e.Trailers.Get("grpc.reflection.v1alpha.errorresponse-bin")!;
                throw new NotFoundException(e.Status.Detail + "\nDetails: " + Encoding.UTF8.GetString(trailer.ValueBytes).Substring(2));
            }
            throw;
        }
    }
}
