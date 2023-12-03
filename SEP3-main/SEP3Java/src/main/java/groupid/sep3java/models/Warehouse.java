package groupid.sep3java.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Warehouse {
	@Id @GeneratedValue(strategy = GenerationType.TABLE)
	private long id;
	private String name;
	private String address;

	public Warehouse() {
	}

	public Warehouse(String name,String address) {
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Warehouse))
			return false;
		Warehouse warehouse = (Warehouse) o;
		return id == warehouse.id && Objects.equals(name, warehouse.name)
				&& Objects.equals(address, warehouse.address);
	}

	@Override public int hashCode() {
		return Objects.hash(id, name, address);
	}

	@Override public String toString() {
		return "Warehouse{" + "id=" + id + ", name='" + name + '\'' + ", address='"
				+ address + '\'' + '}';
	}
}
