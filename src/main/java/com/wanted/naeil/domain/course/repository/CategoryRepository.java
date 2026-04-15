package com.wanted.naeil.domain.course.repository;

import com.wanted.naeil.domain.course.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
