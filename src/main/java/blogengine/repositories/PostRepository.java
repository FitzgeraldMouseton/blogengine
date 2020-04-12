package blogengine.repositories;

import blogengine.models.ModerationStatus;
import blogengine.models.Post;
import org.springframework.core.annotation.AliasFor;
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
    List<Post> findAllByModerationStatusAndTimeBeforeAndActiveTrueOrderByViewCountAsc(ModerationStatus moderationStatus, Date date, Pageable pageable);

    default List<Post> findPopularPosts(ModerationStatus moderationStatus, Date date, Pageable pageable){
        return findAllByModerationStatusAndTimeBeforeAndActiveTrueOrderByViewCountAsc(moderationStatus, date, pageable);
    }
    // ========================= Best posts
    @Query("SELECT p FROM Post p JOIN p.votes v WHERE p.active = 1 AND p.moderationStatus = :moderationStatus AND p.time <= :time " +
            "GROUP BY p.id ORDER BY sum(case when v.value = 1 then v.value else 0 END) DESC")
    List<Post> findBestPosts(ModerationStatus moderationStatus, Date time, Pageable pageable);

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

    // ========================= Find posts by date
    List<Post> findAllByModerationStatusAndTimeBeforeAndActiveTrueAndTimeBetween(ModerationStatus moderationStatus,
                                                                                Date now, Date query, Date limit, Pageable pageable);

    default List<Post> findPostsByDate(ModerationStatus moderationStatus, Date now, Date query, Date limit, Pageable pageable){
        return findAllByModerationStatusAndTimeBeforeAndActiveTrueAndTimeBetween(moderationStatus, now, query, limit, pageable);
    }

    // ========================= Find posts by tag
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE p.active = 1 " +
            "AND p.moderationStatus = :moderationStatus AND p.time <= :time and t.name = :tag")
    List<Post> findAllByTag(ModerationStatus moderationStatus, Date time, String tag, Pageable pageable);
}
