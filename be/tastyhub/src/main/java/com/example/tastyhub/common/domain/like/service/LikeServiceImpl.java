package com.example.tastyhub.common.domain.like.service;

import com.example.tastyhub.common.domain.like.dtos.LikeCheckDto;
import com.example.tastyhub.common.domain.recipe.service.RecipeService;
import org.springframework.stereotype.Service;

import com.example.tastyhub.common.domain.like.dtos.LikeCountRequest;
import com.example.tastyhub.common.domain.like.entity.Like;
import com.example.tastyhub.common.domain.like.repository.LikeRepository;
import com.example.tastyhub.common.domain.recipe.entity.Recipe;
import com.example.tastyhub.common.domain.user.entity.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final RecipeService recipeService;



    @Override
    @Transactional
    public LikeCheckDto like(Long recipeId, User user) {
        Recipe recipe = recipeService.findById(recipeId);
        Long userId = user.getId();
        if(checkLike(recipeId, userId)){
            likeRepository.deleteByRecipeIdAndUserId(recipeId, userId);
            return new LikeCheckDto().builder().check(true).build();
        }

        Like like = Like.createLike(user, recipe);
        likeRepository.save(like);
        return new LikeCheckDto().builder().check(false).build();
    }


    private boolean checkLike(Long recipeId, Long userId) {
        return likeRepository.existsByRecipeIdAndUserId(recipeId, userId);
    }


    @Override
    public LikeCountRequest count(Long recipeId) {
        LikeCountRequest likeCountRequest =  LikeCountRequest.builder().count(likeRepository.countByRecipeId(recipeId)).build();
        return likeCountRequest;
    }
}
