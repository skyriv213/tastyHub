package com.example.tastyhub.common.domain.post.repository;

import com.example.tastyhub.common.domain.post.dtos.PagingPostResponse;
import com.example.tastyhub.common.domain.post.dtos.PostResponse;
import com.example.tastyhub.common.domain.village.entity.Village;
import com.example.tastyhub.common.utils.page.RestPage;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryQuery {

    RestPage<PagingPostResponse> findAllPostResponse(Village village, Pageable pageable);
    RestPage<PagingPostResponse> findAllRecentPostResponse(Village village, Pageable pageable);

    Optional<PostResponse> findByIdQuery(Long postId);
}
