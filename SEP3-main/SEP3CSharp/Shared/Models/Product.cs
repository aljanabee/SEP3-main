namespace Shared.Models;

public class Product {
    public long Id { get; set; }
    public string Name { get; set; } = null!;
    public string Description { get; set; } = null!;
    public double Price { get; set; }
}
