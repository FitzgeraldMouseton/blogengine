package blogengine.services;

import blogengine.models.Post;
import blogengine.models.User;
import blogengine.models.Vote;
import blogengine.repositories.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;

    private static final byte LIKE = 1;
    private static final byte DISLIKE = -1;

    public void save(final Vote vote) {
        voteRepository.save(vote);
    }

    Long countLikesOfUser(final User user) {
        return voteRepository.countVotesOfUserPosts(user, LIKE);
    }

    Long countDislikesOfUser(final User user) {
        return voteRepository.countVotesOfUserPosts(user, DISLIKE);
    }

    Vote findLike(final Post post, final User user) {
        return voteRepository.findByPostAndUserAndValue(post, user, LIKE).orElse(null);
    }

    Vote findDislike(final Post post, final User user) {
        return voteRepository.findByPostAndUserAndValue(post, user, DISLIKE).orElse(null);
    }

    public void delete(final Vote vote) {
        voteRepository.delete(vote);
    }

    void setLike(final Post post, final User user) {
        Vote like =  createVote(post, user, LIKE);
        log.info(like.toString());
        voteRepository.save(like);
    }

    void setDislike(final Post post, final User user) {
        Vote dislike = createVote(post, user, DISLIKE);
        log.info(dislike.toString());
        voteRepository.save(dislike);
    }

    void deleteLikeIfExists(final Post post, final User user) {
        Vote like = findLike(post, user);
        if (like != null)
            voteRepository.delete(like);
    }

    void deleteDislikeIfExists(final Post post, final User user) {
        Vote dislike = findDislike(post, user);
        if (dislike != null)
            voteRepository.delete(dislike);
    }

    private Vote createVote(final Post post, final User user, final byte value) {
        Vote vote = new Vote();
        vote.setPost(post);
        vote.setUser(user);
        vote.setValue(value);
        vote.setTime(LocalDateTime.now());
        return vote;
    }
}
