namespace Shared.Dtos;
public class ProductCreationDto {
    public string Name { get; }
    public string Description { get; }
    public double Price { get; }

    public ProductCreationDto(string name, string description, double price) {
        if (name == null) throw new ArgumentNullException("name");
        if (description == null) throw new ArgumentNullException("description");
        Name = name;
        Description = description;
        Price = price;
    }
}
