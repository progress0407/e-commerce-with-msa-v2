package io.philo.shop.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.philo.shop.entity.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    List<ItemEntity> findAllByIdIn(Collection<Long> itemIds);
}
