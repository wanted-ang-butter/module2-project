package com.wanted.naeil.domain.payment.repository;

import com.wanted.naeil.domain.payment.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<CartItem> findByUserIdAndCourseId(Long userId, Long courseId);

    List<CartItem> findAllByUserId(Long userId);

    Optional<CartItem> findByIdAndUserId(Long cartItemId, Long userId);

    List<CartItem> findAllByIdInAndUserId(List<Long> cartItemIds, Long userId);

    void deleteAllByIdIn(List<Long> cartItemIds);
}