package com.example.final_project.service;

import com.example.final_project.dto.response.WishListResponse;
import com.example.final_project.entity.Product;
import com.example.final_project.entity.User;
import com.example.final_project.entity.WishList;
import com.example.final_project.enums.ProductStatus;
import com.example.final_project.exception.BadRequestException;
import com.example.final_project.exception.ResourceNotFoundException;
import com.example.final_project.repository.ProductRepository;
import com.example.final_project.repository.UserRepository;
import com.example.final_project.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishListServiceImpl implements WishListService {
    private final WishListRepository wishListRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public void addToWishList(Integer productId, int userId) {
        Product product = findProductById(productId);
        User user = findUserById(userId);

        WishList wishList = WishList.builder()
                .user(user)
                .product(product)
                .build();

        wishListRepository.save(wishList);
    }

    @Override
    public void removeFromWishList(Integer productId, int userId) {
        // Tìm wish list hiện tại có tồn tại
        Optional<WishList> wishList = wishListRepository.findByProductIdAndUserId(productId, userId);
        if (wishList.isEmpty()) {
            throw new ResourceNotFoundException("WishList Not Found");
        }

        // Xóa wish list của product hiện tại
        wishListRepository.delete(wishList.get());
    }

    @Override
    public void clearMyWishList(int userId) {
        User user = findUserById(userId);

        List<WishList> wishLists = user.getWishlists();

        wishListRepository.deleteAll(wishLists);
    }

    @Override
    public Page<WishListResponse> getWishLists(int userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("addedAt").descending());

        Page<WishList> wishLists = wishListRepository.findAllByUserId(userId, pageable);

        return wishLists
                .map(w -> WishListResponse.builder()
                        .wishListId(w.getWishListId())
                        .productId(w.getProduct().getId())
                        .productName(w.getProduct().getName())
                        .productPrice(w.getProduct().getPrice())
                        .productImage(w.getProduct().getImageUrl())
                        .addedAt(w.getAddedAt())
                        .build());
    }

    @Override
    public Boolean checkProductInWishList(Integer productId, int userId) {
        // Kiểm tra sản phẩm trong wish list
        Optional<WishList> wishList = wishListRepository.findByProductIdAndUserId(productId, userId);

        return wishList.isPresent();
    }

    private Product findProductById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStatus() == ProductStatus.ARCHIVED) {
            throw new BadRequestException("Product is archived");
        }

        return product;
    }

    private User findUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new BadRequestException("User is not active");
        }
        return user;
    }
}
