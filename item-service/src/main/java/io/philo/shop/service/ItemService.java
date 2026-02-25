package io.philo.shop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.philo.shop.dto.ItemInternalResponseDto;
import io.philo.shop.dto.ItemResponse;
import io.philo.shop.entity.ItemEntity;
import io.philo.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public Long registerItem(String name, String size, int price, int availableQuantity) {
        ItemEntity entity = new ItemEntity(name, size, price, availableQuantity);
        itemRepository.save(entity);
        return entity.getId();
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> findItems(List<Long> itemIds) {
        List<ItemEntity> entities = (itemIds == null || itemIds.isEmpty())
                ? itemRepository.findAll()
                : itemRepository.findAllByIdIn(itemIds);

        List<ItemResponse> dtos = new ArrayList<>();
        for (ItemEntity entity : entities) {
            dtos.add(new ItemResponse(entity));
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<ItemInternalResponseDto> findItemsForInternal(List<Long> itemIds) {
        List<ItemEntity> entities = itemRepository.findAllByIdIn(itemIds);
        List<ItemInternalResponseDto> dtos = new ArrayList<>();
        for (ItemEntity item : entities) {
            dtos.add(new ItemInternalResponseDto(item.getId(), item.getName(), item.getSize(), item.getPrice()));
        }
        return dtos;
    }
}
