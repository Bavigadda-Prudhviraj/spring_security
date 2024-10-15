package com.prudhviraj.security.security.controller;


import com.prudhviraj.security.security.advices.ApiResponse;
import com.prudhviraj.security.security.dto.PostDto;
import com.prudhviraj.security.security.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/createNewPost")
    public PostDto createPost(@RequestBody PostDto postDto){
        return postService.createPost(postDto);
    }

    @GetMapping("/getAllPosts")
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_CREATOR"})
    public List<PostDto> getAllPost(){
        return postService.getAllPost();
    }

    @GetMapping("/getPostById/{postId}")
    @PreAuthorize("@postSecurity.isOwnerOfPostId(#postId)")
    public PostDto getPostById(@PathVariable Long postId){
        return  postService.getPostById(postId);
    }

    @PutMapping("/updatePostById/{postId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN) AND hasAuthority('POST_VIEW')")
    public PostDto updatePostById(@RequestBody PostDto postDto,@PathVariable Long postId){
        return  postService.updatePostById(postDto,postId);
    }

    @GetMapping("/test")
    @Secured("ROLE_GUEST_USER")
    public ApiResponse<String> test() {
        return new ApiResponse<>("hello Prudhviraj from security");
    }



}
