package io.philo.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Entity
@Table(
        name = "item",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique__item__name_size", columnNames = {"name", "size"})
        }
)
@Getter
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false, length = 100)
    private String name = "";

    @Column(nullable = false)
    private String size = "-";

    @Column(nullable = false)
    private int price = 0;

    @Column(nullable = false)
    private int stockQuantity;

    protected ItemEntity() {
        this.id = null;
        this.name = "";
        this.size = "-";
        this.price = -1;
        this.stockQuantity = -1;
    }

    public ItemEntity(String name, int price, int stockQuantity) {
        this.id = null;
        this.name = name;
        this.size = "-";
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public ItemEntity(String name, String size, int price, int stockQuantity) {
        this.id = null;
        this.name = name;
        this.size = size;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public void decreaseStockQuantity(int orderQuantity) {
        validateCanDecrease(orderQuantity);
        this.stockQuantity -= orderQuantity;
    }

    public void increaseStockQuantity(int orderQuantity) {
        this.stockQuantity += orderQuantity;
    }

    private void validateCanDecrease(int orderQuantity) {
        if (stockQuantity - orderQuantity < 0) {
            throw new IllegalStateException("Not enough stock quantity for the requested order quantity.");
        }
    }

    @Override
    public String toString() {
        return "ItemEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                '}';
    }
}
