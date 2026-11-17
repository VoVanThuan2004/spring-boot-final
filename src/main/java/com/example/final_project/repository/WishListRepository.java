package com.example.final_project.repository;

import com.example.final_project.entity.WishList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Integer> {
    Optional<WishList> findByProductIdAndUserId(Integer productId, int userId);

    @Query("""
        SELECT wishList
        FROM WishList wishList
        WHERE wishList.user.id = :userId
        ORDER BY wishList.addedAt DESC
    """)
    Page<WishList> findAllByUserId(@Param("userId") int userId, Pageable pageable);
}
