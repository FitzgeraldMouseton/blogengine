package blogengine.repositories;

import blogengine.models.ModerationStatus;
import blogengine.models.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {

    // Recent posts
    List<Post> findAllByModerationStatusAndTimeBeforeAndActiveTrueOrderByTimeDesc(ModerationStatus moderationStatus, Date date, Pageable pageable);

    // Early posts
    List<Post> findAllByModerationStatusAndTimeBeforeAndActiveTrueOrderByTimeAsc(ModerationStatus moderationStatus, Date date, Pageable pageable);

    // Popular posts
    List<Post> findAllByModerationStatusAndTimeBeforeAndActiveTrueOrderByViewCountDesc(ModerationStatus moderationStatus, Date date, Pageable pageable);

    // Find posts ordered by likesCount
    @Query("SELECT p FROM Post p JOIN p.votes v GROUP BY p.id ORDER BY sum(case when v.value = 1 then v.value else 0 END) DESC")
    List<Post> findAllByOrderByLikes(Pageable pageable);

    // Find all valid posts by query
    List<Post> findAllByModerationStatusAndTimeBeforeAndActiveTrueAndTextContaining(ModerationStatus moderationStatus,
                                                                                    Date date, String query, Pageable pageable);

}
