package blogengine.repositories;

import blogengine.models.ModerationStatus;
import blogengine.models.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {

    // ======================== Recent posts
    List<Post> findAllByModerationStatusAndTimeBeforeAndActiveTrueOrderByTimeDesc(ModerationStatus moderationStatus, Date date, Pageable pageable);

    default List<Post> findRecentPosts(ModerationStatus moderationStatus, Date date, Pageable pageable){
        return findAllByModerationStatusAndTimeBeforeAndActiveTrueOrderByTimeDesc(moderationStatus, date, pageable);
    }

    // ========================= Early posts
    List<Post> findAllByModerationStatusAndTimeBeforeAndActiveTrueOrderByTimeAsc(ModerationStatus moderationStatus, Date date, Pageable pageable);

    default List<Post> findEarlyPosts(ModerationStatus moderationStatus, Date date, Pageable pageable){
        return findAllByModerationStatusAndTimeBeforeAndActiveTrueOrderByTimeAsc(moderationStatus, date, pageable);
    }

    // ========================= Popular posts
    @Query("SELECT p FROM Post p WHERE p.active = 1 AND p.moderationStatus = :moderationStatus AND p.time <= :date " +
            "ORDER BY size(p.comments) DESC")
    List<Post> findPopularPosts(ModerationStatus moderationStatus, Date date, Pageable pageable);

    // ========================= Best posts
    @Query("SELECT p FROM Post p JOIN p.votes v GROUP BY p.id ORDER BY sum(case when v.value = 1 then v.value else 0 END) DESC")
    List<Post> findBestPosts(Pageable pageable);

    // ========================= Find posts by query
    List<Post> findAllByModerationStatusAndTimeBeforeAndActiveTrueAndTextContaining(ModerationStatus moderationStatus,
                                                                                    Date date, String query, Pageable pageable);

    default List<Post> findPostsByQuery(ModerationStatus moderationStatus, Date date, String query, Pageable pageable){
        return findAllByModerationStatusAndTimeBeforeAndActiveTrueAndTextContaining(moderationStatus, date, query, pageable);
    }

    // ========================= Find post by id
    Optional<Post> findByIdAndModerationStatusAndTimeBeforeAndActiveTrue(int id, ModerationStatus moderationStatus, Date date);

    default Optional<Post> findValidPostById(int id, ModerationStatus moderationStatus, Date date){
        return findByIdAndModerationStatusAndTimeBeforeAndActiveTrue(id, moderationStatus, date);
    }
}
