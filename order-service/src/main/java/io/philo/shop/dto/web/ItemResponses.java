package io.philo.shop.dto.web;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemResponses {

    private final List<ItemResponse> items;

    @JsonCreator
    public ItemResponses(@JsonProperty("items") List<ItemResponse> items) {
        this.items = items;
    }

    public List<ItemResponse> getItems() {
        return items;
    }
}
