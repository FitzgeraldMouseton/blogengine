package blogengine.repositories;

import blogengine.models.ModerationStatus;
import blogengine.models.Post;
import blogengine.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {

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
    @Query("SELECT p FROM Post p WHERE p.active = 1 AND p.moderationStatus = :moderationStatus AND p.time <= :date "
            + "ORDER BY size(p.comments) DESC")
    List<Post> getPopularPosts(ModerationStatus moderationStatus, LocalDateTime date, Pageable pageable);

    // ========================= Best posts
    @Query("SELECT p FROM Post p LEFT JOIN p.votes v WHERE p.active = 1 AND p.moderationStatus = :moderationStatus AND p.time <= :date "
            + "GROUP BY p.id ORDER BY sum(v.value) DESC")
    List<Post> getBestPosts(ModerationStatus moderationStatus, LocalDateTime date, Pageable pageable);

    // ========================= Find posts by query
    @Query("SELECT p FROM Post p WHERE p.active = 1 AND p.moderationStatus = :moderationStatus AND p.time <= :date "
            + "AND (p.text LIKE %:query% or p.title LIKE %:query%)")
    List<Post> findPostsByQuery(ModerationStatus moderationStatus, LocalDateTime date, String query, Pageable pageable);

    // ========================= Find active accepted post by id
    Optional<Post> findByIdAndModerationStatusAndTimeBeforeAndActiveTrue(int id, ModerationStatus moderationStatus, LocalDateTime date);

    default Optional<Post> getValidPostById(int id, ModerationStatus moderationStatus, LocalDateTime date) {
        return findByIdAndModerationStatusAndTimeBeforeAndActiveTrue(id, moderationStatus, date);
    }

    // ========================= Find post by id for current user
    Optional<Post> findByIdAndUser(int id, User user);

    // ========================= Find posts by date
    @Query("SELECT p FROM Post p WHERE p.active = 1 AND p.moderationStatus = :moderationStatus AND p.time between :startOfDay and :endOfDay")
    List<Post> findPostsByDate(ModerationStatus moderationStatus, LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.active = 1 AND p.moderationStatus = :moderationStatus AND p.time between :startOfDay and :endOfDay")
    List<Post> findPostsByDate(ModerationStatus moderationStatus, LocalDateTime startOfDay, LocalDateTime endOfDay);
    // ========================= Find posts by tag
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE p.active = 1 "
            + "AND p.moderationStatus = :moderationStatus AND p.time <= :date and t.name = :tag")
    List<Post> findAllByTag(ModerationStatus moderationStatus, LocalDateTime date, String tag, Pageable pageable);

    // ========================= Find all active posts
    List<Post> findAllByModerationStatusAndTimeBeforeAndActiveTrue(ModerationStatus moderationStatus, LocalDateTime localDateTime);

    default List<Post> findActivePosts() {
        return findAllByModerationStatusAndTimeBeforeAndActiveTrue(ModerationStatus.ACCEPTED, LocalDateTime.now(ZoneOffset.UTC));
    }

    // ========================= Find all dates of active posts by year
    @Query("SELECT p.time FROM Post p WHERE p.active = 1 AND p.moderationStatus = 'ACCEPTED' AND year(p.time) = :year " +
            "order by p.time desc")
    List<LocalDateTime> findAllByYear(int year);

    // ========================= Find all years
    @Query("SELECT distinct year(p.time) FROM Post p WHERE p.active = 1 AND p.moderationStatus = 'ACCEPTED'")
    List<Integer> findAllYears();

    // ======================== Count user's inactive posts count

    Long countAllByUserAndActiveFalse(User user);

    default long countInactivePostsOfUser(User user) {
        return countAllByUserAndActiveFalse(user);
    }

    // ======================== Count user's active posts count

    Long countAllByUserAndModerationStatusAndActiveTrue(User user, ModerationStatus moderationStatus);

    default long countActivePostsOfUser(User user, ModerationStatus moderationStatus) {
        return countAllByUserAndModerationStatusAndActiveTrue(user, moderationStatus);
    }

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
    List<Post> findAllByActiveTrueAndModerationStatus(ModerationStatus moderationStatus, Pageable pageable);

    default List<Post> getPostsForModeration(ModerationStatus moderationStatus, Pageable pageable) {
        return findAllByActiveTrueAndModerationStatus(moderationStatus, pageable);
    }

    // ======================== Count posts related to current user if he is moderator
    Long countAllByModerationStatusAndActiveTrue(ModerationStatus moderationStatus);

    // ======================== Count posts for moderationCount value of current moderator

    @Query("select count(p) from Post p where p.moderationStatus = 'NEW' and p.active = true")
    int countPostsForModeration(User moderator);

    // ======================== Find first post
    Optional<Post> findFirstByModerationStatusAndActiveTrueOrderByTime(ModerationStatus moderationStatus);

    default Optional<Post> findFirstPost() {
        return findFirstByModerationStatusAndActiveTrueOrderByTime(ModerationStatus.ACCEPTED);
    }

    // ======================== Find first post of user
    Optional<Post> findFirstByUserAndModerationStatusAndActiveTrueOrderByTime(User user, ModerationStatus moderationStatus);

    default Optional<Post> findFirstPostOfUser(User user) {
        return findFirstByUserAndModerationStatusAndActiveTrueOrderByTime(user, ModerationStatus.ACCEPTED);
    }

    // ======================== Count active posts

    long countAllByModerationStatusAndTimeBeforeAndActiveTrue(ModerationStatus moderationStatus, LocalDateTime localDateTime);

    default long countActivePosts() {
        return countAllByModerationStatusAndTimeBeforeAndActiveTrue(ModerationStatus.ACCEPTED, LocalDateTime.now(ZoneOffset.UTC));
    }

    // ========================
    @Query("select sum(p.viewCount) from Post p where p.user = :user and p.moderationStatus = 'ACCEPTED' and p.active = true")
    Long countUserPostsViews(User user);

    @Query("select sum(p.viewCount) from Post p where p.moderationStatus = 'ACCEPTED' and p.active = true")
    long countAllPostsViews();
}
