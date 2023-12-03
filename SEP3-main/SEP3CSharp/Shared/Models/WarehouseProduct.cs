namespace Shared.Models;
public class WarehouseProduct {
    public Product Product { get; set; } = null!;
    public Warehouse Warehouse { get; set; } = null!;
    public int Quantity { get; set; }
    public int MinimumQuantity { get; set; }
    public string WarehousePosition { get; set; } = null!;
}
