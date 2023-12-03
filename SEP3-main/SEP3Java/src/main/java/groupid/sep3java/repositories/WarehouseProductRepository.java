package groupid.sep3java.repositories;

import groupid.sep3java.models.WarehouseProduct;
import groupid.sep3java.models.WarehouseProductID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WarehouseProductRepository extends JpaRepository<WarehouseProduct, WarehouseProductID> {
	@Query(value = "select * from warehouse_product where product_id_id = :productId",
			nativeQuery = true)
	List<WarehouseProduct> findWarehouseProductsByProductId(long productId);
	@Query(value = "select * from warehouse_product where warehouse_id_id = :warehouseId",
			nativeQuery = true)
	List<WarehouseProduct> findWarehouseProductsByWarehouseId(long warehouseId);
}
