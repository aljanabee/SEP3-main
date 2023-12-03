using Application.LogicInterfaces;
using gRPC.ServiceInterfaces;
using Shared.Dtos;
using Shared.Models;

namespace Application.Logic;

public class WarehouseProductLogic : IWarehouseProductLogic
{
    private readonly IWarehouseProductGrpcService _warehouseProductService;

    public WarehouseProductLogic(IWarehouseProductGrpcService warehouseProductService)
    {
        _warehouseProductService = warehouseProductService;
    }

    public async Task<WarehouseProduct> CreateWarehouseProduct(WarehouseProductCreationDto dto) {
        WarehouseProduct warehouseProduct = await _warehouseProductService.CreateWarehouseProductAsync(dto);
        return warehouseProduct;
    }

    public async Task<WarehouseProduct> AlterWarehouseProduct(WarehouseProductCreationDto dto) {
        WarehouseProduct warehouseProduct = await _warehouseProductService.AlterWarehouseProductAsync(dto);
        return warehouseProduct;
    }

    public async Task<WarehouseProduct> GetWarehouseProductByIdAsync(long productId, long warehouseId) {
        WarehouseProduct warehouseProduct = await _warehouseProductService.GetWarehouseProductByIdAsync(productId, warehouseId);
        return warehouseProduct;
    }

    public async Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsAsync() {
        IEnumerable<WarehouseProduct> warehouseProducts = await _warehouseProductService.GetWarehouseProductsAsync();
        return warehouseProducts;
    }

    public async Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsByProductIdAsync(long id) {
        IEnumerable<WarehouseProduct> warehouseProducts =
            await _warehouseProductService.GetWarehouseProductsByProductIdAsync(id);
        return warehouseProducts;
    }

    public async Task<IEnumerable<WarehouseProduct>> GetWarehouseProductsByWarehouseIdAsync(long id) {
        IEnumerable<WarehouseProduct> warehouseProducts =
            await _warehouseProductService.GetWarehouseProductsByWarehouseIdAsync(id);
        return warehouseProducts;
    }
}