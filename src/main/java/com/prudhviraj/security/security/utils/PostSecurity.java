package com.prudhviraj.security.security.utils;

import com.prudhviraj.security.security.dto.PostDto;
import com.prudhviraj.security.security.entities.User;
import com.prudhviraj.security.security.service.impl.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service for handling security-related operations for posts.
 * This service provides methods to check if a user has ownership of a specific post.
 */
@Service
@RequiredArgsConstructor
public class PostSecurity {

    // Reference to the PostServiceImpl to interact with post-related data
    private final PostServiceImpl postService;

    /**
     * Checks if the currently authenticated user is the owner of the specified post.
     *
     * @param postId the ID of the post to check ownership for
     * @return true if the authenticated user is the author of the post; false otherwise
     */
    public boolean isOwnerOfPostId(Long postId) {
        // Retrieve the currently authenticated user from the security context
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Fetch the post using the provided postId
        PostDto post = postService.getPostById(postId);

        // Compare the author's ID of the post with the authenticated user's ID
        return post.getAuthor().getId().equals(user.getId());
    }
}
