package com.example.tastyhub.common.domain.recipe.service;

import com.example.tastyhub.common.domain.recipe.dtos.RecipeCreateDto;
import com.example.tastyhub.common.domain.recipe.dtos.RecipeDto;
import com.example.tastyhub.common.domain.recipe.dtos.RecipeUpdateDto;
import com.example.tastyhub.common.domain.recipe.entity.Recipe;
import com.example.tastyhub.common.domain.user.entity.User;

import com.example.tastyhub.common.utils.page.RestPage;
import java.io.IOException;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.example.tastyhub.common.domain.recipe.dtos.PagingRecipeResponse;

public interface RecipeService {

    void createRecipe(RecipeCreateDto recipeCreateDto, MultipartFile recipeImg, List<MultipartFile> images, User user) throws Exception;

    RecipeDto getRecipe(Long recipeId, User user);

    void updateRecipe(Long recipeId, MultipartFile img,User user, RecipeUpdateDto recipeUpdateDto) throws IOException;
    
    Page<PagingRecipeResponse> getAllRecipes(Pageable pageable);

    Page<PagingRecipeResponse> getMyRecipes(Pageable pageable, User user);


    RestPage<PagingRecipeResponse> getPopularRecipes(Pageable pageable);

    Page<PagingRecipeResponse> getSearchedRecipes(String foodName, Pageable pageable);


    void deleteRecipe(Long recipeId, User user) throws IOException;

  Recipe findById(Long recipeId);
}
