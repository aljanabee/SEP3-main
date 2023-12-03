package groupid.sep3java.models;

import java.io.Serializable;
import java.util.Objects;

public class WarehouseProductID implements Serializable {
	private long productId;
	private long warehouseId;

	public WarehouseProductID() {
	}

	public WarehouseProductID(long productId, long warehouseId) {
		this.productId = productId;
		this.warehouseId = warehouseId;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof WarehouseProductID))
			return false;
		WarehouseProductID that = (WarehouseProductID) o;
		return productId == that.productId &&
				warehouseId == that.warehouseId;
	}

	@Override public int hashCode() {
		return Objects.hash(productId, warehouseId);
	}

	@Override public String toString() {
		return "WarehouseProductID{" + "productID=" + productId + ", warehouseID="
				+ warehouseId + '}';
	}
}
