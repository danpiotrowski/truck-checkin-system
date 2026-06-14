package truckcheckin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
 * Repository for the load_items table.
 */
public interface LoadItemRepository extends JpaRepository<LoadItem, Long> {

    /*
     * Later we will use this to show all item rows
     * that belong to one load.
     */
    List<LoadItem> findByLoadId(Long loadId);
/*
 * Find a load item by load, order number, and item/bin.
 *
 * This prevents duplicate item rows if the same CSV is uploaded twice.
 */
Optional<LoadItem> findByLoadIdAndOrderNumberAndItemBin(
        Long loadId,
        String orderNumber,
        String itemBin);
}