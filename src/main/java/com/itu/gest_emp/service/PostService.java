package com.itu.gest_emp.service;

import com.itu.gest_emp.model.Post;
import com.itu.gest_emp.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    // Récupérer tous les posts
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // Récupérer un post par son ID
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    // Créer un nouveau post
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    // Mettre à jour un post existant
    public Post updatePost(Long id, Post postDetails) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setName(postDetails.getName());
            post.setDescription(postDetails.getDescription());
            post.setMissions(postDetails.getMissions());
            return postRepository.save(post);
        } else {
            throw new RuntimeException("Post not found with id: " + id);
        }
    }

    // Supprimer un post
    public void deletePost(Long id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
        } else {
            throw new RuntimeException("Post not found with id: " + id);
        }
    }

    // Rechercher des posts par nom
    public List<Post> searchPostsByName(String name) {
        return postRepository.findByNameContainingIgnoreCase(name);
    }

    // Rechercher des posts par mot-clé dans la description
    public List<Post> searchPostsByDescription(String keyword) {
        return postRepository.findByDescriptionContainingIgnoreCase(keyword);
    }
}