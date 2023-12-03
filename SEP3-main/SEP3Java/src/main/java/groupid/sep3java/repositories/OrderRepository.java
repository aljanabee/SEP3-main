package groupid.sep3java.repositories;

import groupid.sep3java.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByWarehouse_Id(long warehouseId);
}
