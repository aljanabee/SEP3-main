using Application.LogicInterfaces;
using Microsoft.AspNetCore.Mvc;
using Shared.Models;

namespace RestAPI.Controllers; 

[ApiController]
[Route("[controller]")]

public class WarehousePositionController : ControllerBase {
	private readonly IWarehousePositionLogic _warehousePositionLogic;

	public WarehousePositionController(IWarehousePositionLogic warehousePositionLogic) {
		_warehousePositionLogic = warehousePositionLogic;
	}

	[HttpPost]
	public async Task<ActionResult<WarehousePosition>> CreateAsync(string position) {
		try {
			WarehousePosition warehousePosition = await _warehousePositionLogic.CreateAsync(position);
			return Created($"/warehousePosition/{warehousePosition.Id}",warehousePosition);
		}
		catch (Exception e) {
			Console.WriteLine(e);
			return StatusCode(500, e.Message);
		}
	}

	[HttpGet("{id}")]
	public async Task<ActionResult<WarehousePosition>> GetByIdAsync(long id) {
		try {
			WarehousePosition warehousePosition = await _warehousePositionLogic.GetByIdAsync(id);
			return Ok(warehousePosition);
		}
		catch (Exception e) {
			Console.WriteLine(e);
			return StatusCode(500, e.Message);
		}
	}

	[HttpGet]
	public async Task<ActionResult<IEnumerable<WarehousePosition>>> GetAsync() {
		try {
			IEnumerable<WarehousePosition> warehousePositions = await _warehousePositionLogic.GetAsync();
			return Ok(warehousePositions);
		}
		catch (Exception e) {
			Console.WriteLine(e);
			return StatusCode(500, e.Message);
		}
	}
}