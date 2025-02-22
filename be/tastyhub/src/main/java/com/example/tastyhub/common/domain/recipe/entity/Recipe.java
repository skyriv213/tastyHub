package com.example.tastyhub.common.domain.recipe.entity;

import com.example.tastyhub.common.domain.recipe.dtos.RecipeCreateDto;
import com.example.tastyhub.common.utils.TimeStamped;
import com.example.tastyhub.common.domain.cookstep.entity.CookStep;
import com.example.tastyhub.common.domain.foodInformation.entity.FoodInformation;
import com.example.tastyhub.common.domain.ingredient.entity.Ingredient;
import com.example.tastyhub.common.domain.like.entity.Like;
import com.example.tastyhub.common.domain.recipeReview.entity.RecipeReview;
import com.example.tastyhub.common.domain.scrap.entity.Scrap;
import com.example.tastyhub.common.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "recipes")
@Entity
@DynamicUpdate
public class Recipe extends TimeStamped {


  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasty_hub_sequence")
  @SequenceGenerator(name = "tasty_hub_sequence", sequenceName = "thesq", allocationSize = 10)
  @Column(name = "recipe_id")
  private Long id;

  private String foodName;


  @Column(name = "food_img", length = 1024)
  private String recipeImgUrl;

  @Column(name = "food_video", length = 1024)
  private String recipeVideoUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RecipeType recipeType;

//    private Long numLike;

  //연관관계
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToOne(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private FoodInformation foodInformation;

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Ingredient> ingredients = new ArrayList<>();

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<CookStep> cookSteps = new ArrayList<>();

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<RecipeReview> recipeReviews = new ArrayList<>();

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Like> likes = new ArrayList<>();

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Scrap> scraps = new ArrayList<>();

  public void update(FoodInformation updateFoodInformation, List<Ingredient> ingredients,
      List<CookStep> cookSteps, String foodName, String imgUrl) {
    this.foodInformation = updateFoodInformation;
    this.ingredients = ingredients;
    this.cookSteps = cookSteps;
    this.foodName = foodName;
    this.recipeImgUrl = imgUrl;
  }

  public static Recipe createRecipe(RecipeCreateDto recipeCreateDto, User user, String imgUrl,
      FoodInformation foodInformation, List<Ingredient> ingredients, List<CookStep> cookSteps) {
    return Recipe.builder()
        .foodName(recipeCreateDto.getFoodName())
        .recipeImgUrl(imgUrl)
        .recipeType(recipeCreateDto.getRecipeType())
        .recipeVideoUrl(recipeCreateDto.getFoodVideoUrl())
        .user(user)
        .foodInformation(foodInformation)
        .ingredients(ingredients)
        .cookSteps(cookSteps)
        .build();
  }


}
