package io.philo.shop.presentation;

import java.util.List;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.philo.shop.dto.ItemCreateRequest;
import io.philo.shop.dto.ItemResponse;
import io.philo.shop.dto.ItemResponses;
import io.philo.shop.dto.ResourceCreateResponse;
import io.philo.shop.entity.ItemEntity;
import io.philo.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceCreateResponse add(@RequestBody ItemCreateRequest itemDto) {
        Long itemId = itemService.addItem(
                itemDto.name(),
                itemDto.size(),
                itemDto.price(),
                itemDto.stockQuantity()
        );
        return new ResourceCreateResponse(itemId);
    }

    @GetMapping
    public ItemResponses list(@RequestParam(name = "ids", required = false) List<Long> itemIds) {
        List<ItemEntity> items = itemService.findItems(itemIds);
        List<ItemResponse> itemResponses = items.stream()
            .map(ItemResponse::new)
            .toList();
        return new ItemResponses(itemResponses);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long itemId) {
        itemService.deleteItem(itemId);
    }
}
