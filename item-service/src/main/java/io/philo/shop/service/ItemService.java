package io.philo.shop.service;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.philo.shop.OrderCreatedEvent;
import io.philo.shop.dto.ItemInternalResponseDto;
import io.philo.shop.entity.ItemEntity;
import io.philo.shop.exception.InvalidOrderQuantityException;
import io.philo.shop.exception.ItemNotFoundForOrderException;
import io.philo.shop.repository.ItemRepository;
import io.philo.shop.testsupport.ManualItemServiceExceptionTrigger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final ManualItemServiceExceptionTrigger manualItemServiceExceptionTrigger;

    @Transactional
    public Long addItem(String name, String size, int price, int availableQuantity) {
        ItemEntity entity = new ItemEntity(name, size, price, availableQuantity);
        itemRepository.save(entity);
        return entity.getId();
    }

    @Transactional(readOnly = true)
    public List<ItemEntity> findItems(List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return itemRepository.findAll();
        }
        return itemRepository.findAllByIdIn(itemIds);
    }

    @Transactional(readOnly = true)
    public List<ItemInternalResponseDto> findItemsForInternal(List<Long> itemIds) {
        List<ItemEntity> entities = itemRepository.findAllByIdIn(itemIds);

        return entities.stream()
            .map(ItemInternalResponseDto::new)
            .toList();

    }

    @Transactional
    public void deleteItem(Long itemId) {
        itemRepository.findById(itemId)
                .ifPresent(itemRepository::delete);
    }

    @Transactional
    public void decreaseStockByOrder(List<OrderCreatedEvent.OrderLine> orderLines) {
        manualItemServiceExceptionTrigger.throwIfConfigured();
        validateOrderLineEvent(orderLines);
        Map<Long, Integer> itemIdToDecreaseQuantity = convertItemIdToDecreaseQuantity(orderLines);
        List<ItemEntity> items = itemRepository.findAllByIdIn(itemIdToDecreaseQuantity.keySet());
        Map<Long, ItemEntity> itemIdToEntity = convertItemIdToEntity(items);
        validateAndDecreaseItemQuantity(itemIdToDecreaseQuantity, itemIdToEntity);
    }

    private static @NonNull Map<Long, ItemEntity> convertItemIdToEntity(List<ItemEntity> items) {
        return items.stream()
            .collect(toMap(ItemEntity::getId, item -> item));
    }

    private static void validateOrderLineEvent(List<OrderCreatedEvent.OrderLine> orderLines) {
        for (OrderCreatedEvent.OrderLine orderLine : orderLines) {
            if (orderLine.quantity() <= 0) {
                throw new InvalidOrderQuantityException(orderLine.itemId(), orderLine.quantity());
            }
        }
    }

    private static void validateAndDecreaseItemQuantity(Map<Long, Integer> requestItemIdToDecreaseQuantities, Map<Long, ItemEntity> itemIdToEntity) {
        for (Map.Entry<Long, Integer> itemIdToDecreaseQuantity : requestItemIdToDecreaseQuantities.entrySet()) {
            ItemEntity item = itemIdToEntity.get(itemIdToDecreaseQuantity.getKey());
            if (item == null) {
                throw new ItemNotFoundForOrderException(itemIdToDecreaseQuantity.getKey());
            }
            item.decreaseStockQuantity(itemIdToDecreaseQuantity.getValue());
        }
    }

    private static @NonNull Map<Long, Integer> convertItemIdToDecreaseQuantity(List<OrderCreatedEvent.OrderLine> orderLines) {
        return orderLines.stream()
            .collect(toMap(
                OrderCreatedEvent.OrderLine::itemId,
                OrderCreatedEvent.OrderLine::quantity,
                Integer::sum
            ));
    }
}
