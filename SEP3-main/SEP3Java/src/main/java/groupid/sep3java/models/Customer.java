package groupid.sep3java.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Customer {
	@Id @GeneratedValue(strategy = GenerationType.TABLE)
	private long id;
	private String fullName;
	private String phoneNo;
	private String address;
	private String mail;

	public Customer() {
	}

	public Customer(String fullName, String phoneNo, String address,
			String mail) {
		this.fullName = fullName;
		this.phoneNo = phoneNo;
		this.address = address;
		this.mail = mail;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Customer))
			return false;
		Customer customer = (Customer) o;
		return id == customer.id && Objects.equals(fullName, customer.fullName)
				&& Objects.equals(phoneNo, customer.phoneNo) && Objects.equals(address,
				customer.address) && Objects.equals(mail, customer.mail);
	}

	@Override public int hashCode() {
		return Objects.hash(id, fullName, phoneNo, address, mail);
	}

	@Override public String toString() {
		return "Customer{" + "id=" + id + ", fullName='" + fullName + '\''
				+ ", phoneNo='" + phoneNo + '\'' + ", address='" + address + '\''
				+ ", mail='" + mail + '\'' + '}';
	}
}
