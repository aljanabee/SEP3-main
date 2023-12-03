using Application.DaoInterfaces;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.ChangeTracking;
using Shared.Models;

namespace EfcEmployeeDataAccess.DAOs; 

public class WarehousePositionEfcDao : IWarehousePositionDao {
	private readonly DataContext _context;

	public WarehousePositionEfcDao(DataContext context) {
		_context = context;
	}

	public async Task<WarehousePosition> CreateAsync(string position) {
		WarehousePosition warehousePosition = new WarehousePosition { Position = position };

		EntityEntry<WarehousePosition> newWarehouse = await _context.WarehousePositions.AddAsync(warehousePosition);
		await _context.SaveChangesAsync();
		return newWarehouse.Entity;
	}

	public async Task<WarehousePosition> GetByIdAsync(long id) {
		WarehousePosition warehousePosition = await _context.WarehousePositions
			.FirstAsync(w => w.Id == id);
		return warehousePosition;
	}

	public async Task<IEnumerable<WarehousePosition>> GetAsync() {
		IEnumerable<WarehousePosition> warehousePositions = await _context.WarehousePositions.ToListAsync();
		return warehousePositions;
	}
}