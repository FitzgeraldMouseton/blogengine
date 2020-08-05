package blogengine.repositories;

import blogengine.models.Post;
import blogengine.models.User;
import blogengine.models.Vote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends CrudRepository<Vote, Integer> {

    Optional<Vote> findByPostAndUserAndValue(Post post, User user, Byte value);

    @Query("select count(v) from Vote v join v.post p where p.active = true and p.user = :user " +
            "and p.moderationStatus = 'ACCEPTED' and v.value = :value")
    long countVotesOfUserPosts(User user, byte value);

    @Query("select count(v) from Vote v join v.post p where p.active = true " +
            "and p.moderationStatus = 'ACCEPTED' and v.value = :value")
    long countAllVotes(byte value);
}
