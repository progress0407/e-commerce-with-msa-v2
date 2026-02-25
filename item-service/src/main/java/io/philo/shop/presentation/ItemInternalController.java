package io.philo.shop.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.philo.shop.dto.ItemInternalResponseDto;
import io.philo.shop.service.ItemService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/items/internal")
@RequiredArgsConstructor
public class ItemInternalController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemInternalResponseDto> list(@RequestParam(value = "ids") List<Long> itemIds) {
        return itemService.findItemsForInternal(itemIds);
    }
}
