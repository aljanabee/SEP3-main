package groupid.sep3java.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;
@Entity
public class Product {
	@Id @GeneratedValue(strategy = GenerationType.TABLE)
	private long Id;

	private String name;
	private String description;
	private double price;

	public Product() {
	}

	public Product(String name, String description, double price) {
		this.name = name;
		this.description = description;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Product))
			return false;
		Product product = (Product) o;
		return Id == product.Id && Double.compare(product.price, price) == 0
				&& Objects.equals(name, product.name) && Objects.equals(description,
				product.description);
	}

	@Override public int hashCode() {
		return Objects.hash(Id, name, description, price);
	}

	@Override public String toString() {
		return "Product{" + "Id=" + Id + ", name='" + name + '\''
				+ ", Description='" + description + '\'' + ", Price=" + price + '}';
	}
}
