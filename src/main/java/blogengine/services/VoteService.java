package blogengine.services;

import blogengine.exceptions.authexceptions.NotEnoughPrivilegesException;
import blogengine.exceptions.authexceptions.UnauthenticatedUserException;
import blogengine.models.Post;
import blogengine.models.User;
import blogengine.models.Vote;
import blogengine.repositories.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserService userService;

    private static final byte LIKE = 1;
    private static final byte DISLIKE = -1;

    public void save(final Vote vote) {
        voteRepository.save(vote);
    }

    Long countLikesOfUserPosts(final User user) {
        return voteRepository.countVotesOfUserPosts(user, LIKE);
    }

    Long countDislikesOfUser(final User user) {
        return voteRepository.countVotesOfUserPosts(user, DISLIKE);
    }

    Long countLikes() {
        return voteRepository.countAllVotes(LIKE);
    }

    Long countDislikes() {
        return voteRepository.countAllVotes(DISLIKE);
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

    Vote getLike() {
        return createVote(LIKE);
    }

    Vote getDislike() {
        return createVote(DISLIKE);
    }

    void deleteLikeIfExists(final Post post, final User user) {
        Vote like = findLike(post, user);
        if (like != null) {
            post.removeVote(like);
        }
    }

    void deleteDislikeIfExists(final Post post, final User user) {
        Vote dislike = findDislike(post, user);
        if (dislike != null)
            post.removeVote(dislike);
    }

    public Vote createVote(final byte value) {
        Vote vote = new Vote();
        vote.setValue(value);
        vote.setTime(LocalDateTime.now());
        return vote;
    }
}
