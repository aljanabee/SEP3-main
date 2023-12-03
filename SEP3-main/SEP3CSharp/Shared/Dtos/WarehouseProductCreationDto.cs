namespace Shared.Dtos;

public class WarehouseProductCreationDto
{
    public long ProductId { get; set; }
    public long WarehouseId { get; set; }
    public int Quantity { get; set; }
    public int MinimumQuantity { get; set; }
    public string WarehousePosition { get; set; } = null!;
}