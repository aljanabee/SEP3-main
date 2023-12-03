using Application.DaoInterfaces;
using Application.LogicInterfaces;
using Shared.Models;

namespace Application.Logic; 

public class WarehousePositionLogic : IWarehousePositionLogic {
	private readonly IWarehousePositionDao _warehousePositionDao;

	public WarehousePositionLogic(IWarehousePositionDao warehousePositionDao) {
		_warehousePositionDao = warehousePositionDao;
	}

	public async Task<WarehousePosition> CreateAsync(string position) {
		WarehousePosition warehousePosition = await _warehousePositionDao.CreateAsync(position);
		return warehousePosition;
	}

	public async Task<WarehousePosition> GetByIdAsync(long id) {
		WarehousePosition warehousePosition = await _warehousePositionDao.GetByIdAsync(id);
		return warehousePosition;
	}

	public async Task<IEnumerable<WarehousePosition>> GetAsync() {
		IEnumerable<WarehousePosition> warehousePositions = await _warehousePositionDao.GetAsync();
		return warehousePositions;
	}
}