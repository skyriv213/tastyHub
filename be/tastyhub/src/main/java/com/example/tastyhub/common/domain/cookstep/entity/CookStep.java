package com.example.tastyhub.common.domain.cookstep.entity;

import com.example.tastyhub.common.domain.cookstep.dtos.CookStepCreateRequest;
import com.example.tastyhub.common.domain.cookstep.dtos.CookStepDto;
import com.example.tastyhub.common.utils.TimeStamped;
import com.example.tastyhub.common.domain.recipe.entity.Recipe;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "cook_steps")
public class CookStep extends TimeStamped {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasty_hub_sequence")
  @SequenceGenerator(name = "tasty_hub_sequence", sequenceName = "thesq", allocationSize = 10)
  @Column(name = "cookstep_id")
  private Long id;

  // 조리과정 숫자
  private Long stepNumber;

  @Size(max = 1024)
  // 조리 시 필요한 내용
  private String content;
  @Column(name = "step_img_url", length = 2000) // 길이를 2000으로 설정
  private String stepImgUrl;

  private String timeline;

  //연관 관계
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, targetEntity = Recipe.class)
  @JoinColumn(name = "recipe_id", nullable = false)
  private Recipe recipe;

  public static CookStep makeCookStep(CookStepCreateRequest cookStepCreateRequest, String imageUrl) {
    return CookStep.builder()
        .stepNumber(cookStepCreateRequest.getStepNumber())
        .timeline(cookStepCreateRequest.getTimeLine())
        .content(cookStepCreateRequest.getContent())
        .stepImgUrl(imageUrl)
        .build();
  }

//    public static CookStep updaCookStep(CookStepCreateDto cookStepCreateDto) {
//        return CookStep.builder()
//            .stepNumber(cookStepCreateDto.getStepNumber())
//            .text(cookStepCreateDto.getText())
//            .build();
//    }

  public void updateByUpdateDto(CookStepDto cookStepUpdateDto) {
    this.content = cookStepUpdateDto.getContent();
    this.stepNumber = cookStepUpdateDto.getStepNumber();
//        this.stepImgUrl = cookStepUpdateDto.getStepImg();
  }

  public void updateFromDto(CookStepDto dto) {

  }

  public void setRecipe(Recipe recipe) {
    this.recipe = recipe;
  }

  public void updateRelation(Recipe recipe) {
    this.recipe = recipe;
  }
}
