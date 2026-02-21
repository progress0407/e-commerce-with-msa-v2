package io.philo.shop.presentation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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
import io.philo.shop.service.ItemService;

@RestController
@RequestMapping("/items")
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceCreateResponse add(@RequestBody ItemCreateRequest itemDto) {
        Long itemId = itemService.registerItem(
                itemDto.name(),
                itemDto.size(),
                itemDto.price(),
                itemDto.stockQuantity()
        );
        return new ResourceCreateResponse(itemId);
    }

    @GetMapping
    public ItemResponses list(@RequestParam(name = "ids", required = false) List<Long> itemIds) {
        List<ItemResponse> items = itemService.findItems(itemIds);
        return new ItemResponses(items);
    }
}
