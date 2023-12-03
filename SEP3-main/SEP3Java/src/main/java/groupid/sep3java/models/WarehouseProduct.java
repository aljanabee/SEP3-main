package groupid.sep3java.models;

import javax.persistence.*;
import java.util.Objects;
@Entity
@IdClass(WarehouseProductID.class)
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueWarehouseIDxPosition",columnNames = {"warehouse_id_id","warehousePosition"})})
public class WarehouseProduct {
	@Id @ManyToOne
	public Product productId;
	@Id @ManyToOne
	private Warehouse warehouseId;
	private int quantity;
	private int minimumQuantity;
	private String warehousePosition;

	public WarehouseProduct() {
	}

	public WarehouseProduct(Product productId, Warehouse warehouseId, int quantity,
			int minimumQuantity, String warehousePosition) {
		this.productId = productId;
		this.warehouseId = warehouseId;
		this.quantity = quantity;
		this.minimumQuantity = minimumQuantity;
		this.warehousePosition = warehousePosition;
	}

	public Product getProductId() {
		return productId;
	}

	public void setProductId(Product productId) {
		this.productId = productId;
	}

	public Warehouse getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Warehouse warehouseId) {
		this.warehouseId = warehouseId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getMinimumQuantity() {
		return minimumQuantity;
	}

	public void setMinimumQuantity(int minimumQuantity) {
		this.minimumQuantity = minimumQuantity;
	}

	public String getWarehousePosition() {
		return warehousePosition;
	}

	public void setWarehousePosition(String shelf) {
		this.warehousePosition = shelf;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		WarehouseProduct that = (WarehouseProduct) o;
		return quantity == that.quantity && minimumQuantity == that.minimumQuantity
				&& Objects.equals(productId, that.productId) && Objects.equals(
				warehouseId, that.warehouseId) && Objects.equals(warehousePosition,
				that.warehousePosition);
	}

	@Override public int hashCode() {
		return Objects.hash(productId, warehouseId, quantity, minimumQuantity,
				warehousePosition);
	}

	@Override public String toString() {
		return "WarehouseProduct{" + "productId=" + productId + ", warehouseId="
				+ warehouseId + ", quantity=" + quantity + ", minimumQuantity="
				+ minimumQuantity + ", warehousePosition='" + warehousePosition + '\''
				+ '}';
	}
}
