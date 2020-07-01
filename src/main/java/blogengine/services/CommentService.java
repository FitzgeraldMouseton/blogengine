package blogengine.services;

import blogengine.models.Comment;
import blogengine.repositories.CommentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    Comment findById(final int id) {
        return commentRepository.findById(id).orElse(null);
    }

    void save(final Comment comment) {
        commentRepository.save(comment);
    }
}
