package blogengine.repositories;

import blogengine.models.Comment;
import blogengine.models.Post;
import blogengine.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Integer> {

    List<Comment> findAllByPost(Post post);
}
