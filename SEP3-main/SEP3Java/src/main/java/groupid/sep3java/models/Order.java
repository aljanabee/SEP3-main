package groupid.sep3java.models;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Entity(name = "orderPlaced")
public class Order {
	@Id @GeneratedValue(strategy = GenerationType.TABLE)
	private long id;
	@ManyToOne
	private Customer customer;
	@ManyToOne
	private Warehouse warehouse;
	private LocalDate dateOrdered;
	private LocalTime timeOrdered;
	private boolean isPacked;
	private LocalDate dateSent;
	private LocalTime timeSent;
	@ManyToMany(fetch = FetchType.EAGER)
	private List<Product> orderedProducts;
	public Order() {
	}

	public Order(Customer customer, Warehouse warehouse, LocalDateTime dateTimeOrdered,
			boolean isPacked, LocalDateTime dateTimeSent, List<Product> orderedProducts) {
		this.customer = customer;
		this.warehouse = warehouse;
		if (dateTimeOrdered != null) {
			dateOrdered = dateTimeOrdered.toLocalDate();
			timeOrdered = dateTimeOrdered.toLocalTime();
		}
		this.isPacked = isPacked;
		if (dateTimeSent != null) {
			dateSent = dateTimeSent.toLocalDate();
			timeSent = dateTimeSent.toLocalTime();
		}
		this.orderedProducts = orderedProducts;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public LocalDate getDateOrdered() {
		return dateOrdered;
	}

	public LocalTime getTimeOrdered() {
		return timeOrdered;
	}

	public void setDateOrdered(LocalDate dateTimeOrdered) {
		this.dateOrdered = dateTimeOrdered;
	}

	public void setTimeOrdered(LocalTime timeOrdered) {
		this.timeOrdered = timeOrdered;
	}

	public boolean isPacked() {
		return isPacked;
	}

	public void setPacked(boolean packed) {
		isPacked = packed;
	}

	public LocalDate getDateSent() {
		return dateSent;
	}

	public LocalTime getTimeSent() {
		return timeSent;
	}

	public void setDateSent(LocalDate dateTimeSent) {
		this.dateSent = dateTimeSent;
	}

	public void setTimeSent(LocalTime timeSent) {
		this.timeSent = timeSent;
	}

	public List<Product> getOrderedProducts() {
		return orderedProducts;
	}

	public void setOrderedProducts(List<Product> orderedProducts) {
		this.orderedProducts = orderedProducts;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Order))
			return false;
		Order order = (Order) o;
		return id == order.id && isPacked == order.isPacked && Objects.equals(
				customer, order.customer) && Objects.equals(dateOrdered,
				order.dateOrdered) && Objects.equals(dateSent,
				order.dateSent);
	}

	@Override public int hashCode() {
		return Objects.hash(id, customer, dateOrdered, isPacked, dateSent);
	}

	@Override public String toString() {
		return "Order{" + "id=" + id + ", customer=" + customer
				+ ", dateTimeOrdered=" + dateOrdered + ", isPacked=" + isPacked
				+ ", dateTimeSent=" + dateSent + '}';
	}
}
