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

    long countAllByModerationStatusAndTimeBeforeAndActiveTrue(ModerationStatus moderationStatus, Date date);

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
    @Query("SELECT p FROM Post p LEFT JOIN p.votes v WHERE p.active = 1 AND p.moderationStatus = :moderationStatus AND p.time <= :date " +
            "GROUP BY p.id ORDER BY sum(v.value) DESC")
    List<Post> findBestPosts(ModerationStatus moderationStatus, Date date, Pageable pageable);

    // ========================= Find posts by query
    @Query("SELECT p FROM Post p WHERE p.active = 1 AND p.moderationStatus = :moderationStatus AND p.time <= :date " +
            "AND (p.text LIKE %:query% or p.title LIKE %:query%)")
    List<Post> findPostsByQuery(ModerationStatus moderationStatus, Date date, String query, Pageable pageable);

    // ========================= Find post by id
    Optional<Post> findByIdAndModerationStatusAndTimeBeforeAndActiveTrue(int id, ModerationStatus moderationStatus, Date date);

    default Optional<Post> findValidPostById(int id, ModerationStatus moderationStatus, Date date){
        return findByIdAndModerationStatusAndTimeBeforeAndActiveTrue(id, moderationStatus, date);
    }

    // ========================= Find posts by date
    List<Post> findAllByModerationStatusAndTimeBeforeAndActiveTrueAndTimeBetween(ModerationStatus moderationStatus,
                                                                                Date now, Date query, Date limit, Pageable pageable);

    default List<Post> findPostsByDate(ModerationStatus moderationStatus, Date now, Date query, Date limit, Pageable pageable){
        return findAllByModerationStatusAndTimeBeforeAndActiveTrueAndTimeBetween(moderationStatus, now, query, limit, pageable);
    }

    // ========================= Find posts by tag
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE p.active = 1 " +
            "AND p.moderationStatus = :moderationStatus AND p.time <= :date and t.name = :tag")
    List<Post> findAllByTag(ModerationStatus moderationStatus, Date date, String tag, Pageable pageable);

}
