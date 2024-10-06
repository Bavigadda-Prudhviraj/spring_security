package com.prudhviraj.security.security.service;



import com.prudhviraj.security.security.dto.PostDto;

import java.util.List;


public interface PostService {
    List<PostDto> getAllPost();
    PostDto createPost(PostDto postDto);
    PostDto getPostById(Long postId);
    PostDto updatePostById(PostDto postDto, Long postId);
}
