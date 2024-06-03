package com.example.tastyhub.common.domain.userReview.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReviewCreateRequest {
    private Long grade;
    private String text;
}
