package blogengine.repositories;

import blogengine.models.ModerationStatus;
import blogengine.models.Post;
import blogengine.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {

    long countAllByModerationStatusAndTimeBeforeAndActiveTrue(ModerationStatus moderationStatus, LocalDateTime date);

    // ======================== Recent posts
    List<Post> findAllByModerationStatusAndTimeBeforeAndActiveTrueOrderByTimeDesc(ModerationStatus moderationStatus, LocalDateTime date, Pageable pageable);

    default List<Post> getRecentPosts(ModerationStatus moderationStatus, LocalDateTime date, Pageable pageable) {
        return findAllByModerationStatusAndTimeBeforeAndActiveTrueOrderByTimeDesc(moderationStatus, date, pageable);
    }

    // ========================= Early posts
    List<Post> findAllByModerationStatusAndTimeBeforeAndActiveTrueOrderByTimeAsc(ModerationStatus moderationStatus, LocalDateTime date, Pageable pageable);

    default List<Post> getEarlyPosts(ModerationStatus moderationStatus, LocalDateTime date, Pageable pageable) {
        return findAllByModerationStatusAndTimeBeforeAndActiveTrueOrderByTimeAsc(moderationStatus, date, pageable);
    }

    // ========================= Popular posts
    @Query("SELECT p FROM Post p WHERE p.active = 1 AND p.moderationStatus = :moderationStatus AND p.time <= :date " +
            "ORDER BY size(p.comments) DESC")
    List<Post> findPopularPosts(ModerationStatus moderationStatus, LocalDateTime date, Pageable pageable);

    // ========================= Best posts
    @Query("SELECT p FROM Post p LEFT JOIN p.votes v WHERE p.active = 1 AND p.moderationStatus = :moderationStatus AND p.time <= :date " +
            "GROUP BY p.id ORDER BY sum(v.value) DESC")
    List<Post> findBestPosts(ModerationStatus moderationStatus, LocalDateTime date, Pageable pageable);

    // ========================= Find posts by query
    @Query("SELECT p FROM Post p WHERE p.active = 1 AND p.moderationStatus = :moderationStatus AND p.time <= :date " +
            "AND (p.text LIKE %:query% or p.title LIKE %:query%)")
    List<Post> findPostsByQuery(ModerationStatus moderationStatus, LocalDateTime date, String query, Pageable pageable);

    // ========================= Find post by id
    Optional<Post> findByIdAndModerationStatusAndTimeBeforeAndActiveTrue(int id, ModerationStatus moderationStatus, LocalDateTime date);

    default Optional<Post> getValidPostById(int id, ModerationStatus moderationStatus, LocalDateTime date) {
        return findByIdAndModerationStatusAndTimeBeforeAndActiveTrue(id, moderationStatus, date);
    }

    // ========================= Find posts by date
    @Query("SELECT p FROM Post p WHERE p.active = 1 AND p.moderationStatus = :moderationStatus AND p.time between :startOfDay and :endOfDay")
    List<Post> findPostsByDate(ModerationStatus moderationStatus, LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);

    // ========================= Find posts by tag
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE p.active = 1 " +
            "AND p.moderationStatus = :moderationStatus AND p.time <= :date and t.name = :tag")
    List<Post> findAllByTag(ModerationStatus moderationStatus, LocalDateTime date, String tag, Pageable pageable);

    // ========================= Find all posts
    List<Post> findAllBy();

    // ======================== User's posts count

    Long countAllByUser(User user);

    // ======================== Inactive posts of user
    List<Post> findAllByUserAndActiveFalseOrderByTimeDesc(User user, Pageable pageable);

    default List<Post> getCurrentUserInactivePosts(User user, Pageable pageable) {
        return findAllByUserAndActiveFalseOrderByTimeDesc(user, pageable);
    }

    // ========================= Active posts of user
    List<Post> findAllByUserAndModerationStatusAndActiveTrue(User user, ModerationStatus moderationStatus, Pageable pageable);

    default List<Post> getCurrentUserActivePosts(User user, ModerationStatus moderationStatus, Pageable pageable) {
        return findAllByUserAndModerationStatusAndActiveTrue(user, moderationStatus, pageable);
    }

    // ======================== Posts for moderation
    List<Post> findAllByModeratorAndActiveTrueAndModerationStatus(User moderator, ModerationStatus moderationStatus, Pageable pageable);

    default List<Post> getPostsForModeration(User moderator, ModerationStatus moderationStatus, Pageable pageable) {
        return findAllByModeratorAndActiveTrueAndModerationStatus(moderator, moderationStatus, pageable);
    }

    // ======================== Count posts related to current user if he is moderator
    Long countAllByModeratorAndActiveTrue(User moderator);

    // ======================== Count posts for moderationCount value of current moderator

    @Query("select count(p) from Post p where p.moderator = :moderator and p.moderationStatus = 'NEW' and p.active = true")
    int countPostsForModeration(User moderator);

    // ======================== Find first post
    Optional<Post> findFirstByOrderByTime();

    // ========================
    @Query("select sum(p.viewCount) from Post p where p.user = :user")
    Long countUserPostsViews(User user);
}
