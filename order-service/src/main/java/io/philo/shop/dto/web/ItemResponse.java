package io.philo.shop.dto.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemResponse {

    private final Long id;
    private final String name;
    private final String size;
    private final int price;
    private final int availableQuantity;

    @JsonCreator
    public ItemResponse(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("size") String size,
            @JsonProperty("price") int price,
            @JsonProperty("availableQuantity") int availableQuantity
    ) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public int getPrice() {
        return price;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
