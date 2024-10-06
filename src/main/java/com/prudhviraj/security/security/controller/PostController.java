package com.prudhviraj.security.security.controller;


import com.prudhviraj.security.security.dto.PostDto;
import com.prudhviraj.security.security.service.PostService;
import lombok.RequiredArgsConstructor;
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
    public List<PostDto> getAllPost(){
        return postService.getAllPost();
    }

    @GetMapping("/getPostById/{postId}")
    public PostDto getPostById(@PathVariable Long postId){
        return  postService.getPostById(postId);
    }

    @PutMapping("/updatePostById/{postId}")
    public PostDto updatePostById(@RequestBody PostDto postDto,@PathVariable Long postId){
        return  postService.updatePostById(postDto,postId);
    }

    @GetMapping("/test")
    public String test(){
        return "hello Prudhviraj from security";
    }


}
