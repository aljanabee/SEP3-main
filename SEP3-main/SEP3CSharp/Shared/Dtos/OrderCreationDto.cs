using Shared.Models;

namespace Shared.Dtos;
public class OrderCreationDto {
    public long CustomerId { get; set; }
    public long WarehouseId { get; set; }
    public DateTime? DateTimeOrdered { get; set; }
    public bool IsPacked { get; set; }
    public DateTime? DateTimeSent { get; set; }
    public IEnumerable<long> ProductIds { get; set; } = null!;
}
