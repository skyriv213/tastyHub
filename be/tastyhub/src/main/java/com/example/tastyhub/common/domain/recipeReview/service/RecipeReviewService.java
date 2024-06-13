package com.example.tastyhub.common.domain.recipeReview.service;

import java.util.List;

import com.example.tastyhub.common.domain.recipeReview.dtos.PagingMyRecipeReviewResponse;
import com.example.tastyhub.common.domain.recipeReview.dtos.PagingRecipeReviewResponse;
import com.example.tastyhub.common.domain.recipeReview.dtos.RecipeReviewCreateRequest;
import com.example.tastyhub.common.domain.recipeReview.dtos.RecipeReviewUpdateRequest;
import com.example.tastyhub.common.domain.user.entity.User;

public interface RecipeReviewService {

    void createRecipeReview(Long recipeId, RecipeReviewCreateRequest recipeReviewCreateRequest, User user);

    List<PagingRecipeReviewResponse> getRecipeReviews(Long reciped);

    void updateRecipeReview(Long recipeReviewId, RecipeReviewUpdateRequest recipeReviewUpdateRequest, User user);

    void deleteRecipeReview(Long recipeReviewId, User user);

    List<PagingMyRecipeReviewResponse> getMyRecipeReviews(User user);

}
