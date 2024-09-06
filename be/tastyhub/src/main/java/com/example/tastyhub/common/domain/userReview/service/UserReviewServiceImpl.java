package com.example.tastyhub.common.domain.userReview.service;

import com.example.tastyhub.common.utils.OrderSpecifierUtil;
import com.querydsl.core.types.OrderSpecifier;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.tastyhub.common.domain.user.entity.User;
import com.example.tastyhub.common.domain.user.repository.UserRepository;
import com.example.tastyhub.common.domain.userReview.dtos.PagingUserReviewResponse;
import com.example.tastyhub.common.domain.userReview.dtos.UserReviewCreateRequest;
import com.example.tastyhub.common.domain.userReview.dtos.UserReviewUpdateRequest;
import com.example.tastyhub.common.domain.userReview.entity.UserReview;
import com.example.tastyhub.common.domain.userReview.repository.UserReviewRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserReviewServiceImpl implements UserReviewService {

  private final UserReviewRepository userReviewRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public void createUserReview(Long userId, UserReviewCreateRequest userReviewCreateRequest,
      User user) {
    User reader = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
    UserReview userReview = UserReview.createUserReview(userReviewCreateRequest.getGrade(),
        userReviewCreateRequest.getContent(), reader, user);
    userReviewRepository.save(userReview);
  }

  @Override
  public Page<PagingUserReviewResponse> getUserReviews(Long userId, Pageable pageable) {
    OrderSpecifier<?>[] orderSpecifiers = OrderSpecifierUtil.getOrderSpecifiers(pageable, UserReview.class, "userReview");
    return userReviewRepository.findAllUserReviewResponse(userId,pageable,orderSpecifiers);
  }

  @Override
  @Transactional
  public void updateUserReviewByUserReviewUpdateRequest(Long userReviewId,
      UserReviewUpdateRequest userReviewUpdateRequest,
      User user) {
    UserReview userReview = userReviewRepository.findById(userReviewId).orElseThrow(
        () -> new IllegalArgumentException("User review not found with id: " + userReviewId));
    userReview.updateByUserReviewUpdateRequest(userReviewUpdateRequest);
  }

  @Override
  public void deleteUserReview(Long userReviewId, User user) {
    userReviewRepository.deleteById(userReviewId);
  }
}
