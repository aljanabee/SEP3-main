namespace Shared.Models;
public class Customer {
    public long Id { get; set; }
    public string FullName { get; set; } = null!;
    public string PhoneNo { get; set; } = null!;
    public string Address { get; set; } = null!;
    public string Mail { get; set; } = null!;
}
