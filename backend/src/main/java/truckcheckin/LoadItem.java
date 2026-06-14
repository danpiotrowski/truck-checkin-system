package truckcheckin;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/*
 * Represents one row in the load_items table.
 *
 * One load can have many load items.
 */
@Entity
@Table(name = "load_items")
public class LoadItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * This links the item row to loads.id.
     */
    @Column(name = "load_id")
    private Long loadId;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "item_bin")
    private String itemBin;

    @Column(name = "item_description")
    private String itemDescription;

    private Integer pallets;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public LoadItem() {
    }

    public Long getId() {
        return id;
    }

    public Long getLoadId() {
        return loadId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getItemBin() {
        return itemBin;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public Integer getPallets() {
        return pallets;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setItemBin(String itemBin) {
        this.itemBin = itemBin;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setPallets(Integer pallets) {
        this.pallets = pallets;
    }
}