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

    private final static byte LIKE = 1;
    private final static byte DISLIKE = -1;

    public void save(Vote vote){
        voteRepository.save(vote);
    }

    Long countLikesOfUser(User user){
        return voteRepository.countVotesOfUserPosts(user, LIKE);
    }

    Long countDislikesOfUser(User user){
        return voteRepository.countVotesOfUserPosts(user, DISLIKE);
    }

    Vote findLike(Post post, User user){
        return voteRepository.findByPostAndUserAndValue(post, user, LIKE).orElse(null);
    }

    Vote findDislike(Post post, User user){
        return voteRepository.findByPostAndUserAndValue(post, user, DISLIKE).orElse(null);
    }

    public void delete(Vote vote){
        voteRepository.delete(vote);
    }

    void setLike(Post post, User user){
        Vote like =  createVote(post, user, LIKE);
        voteRepository.save(like);
    }

    void setDislike(Post post, User user){
        Vote dislike = createVote(post, user, DISLIKE);
        voteRepository.save(dislike);
    }

    void deleteLikeIfExists(Post post, User user){
        Vote like = findLike(post, user);
        if (like != null)
            voteRepository.delete(like);
    }

    void deleteDislikeIfExists(Post post, User user){
        Vote dislike = findLike(post, user);
        if (dislike != null)
            voteRepository.delete(dislike);
    }

    private Vote createVote(Post post, User user, byte value){
        Vote vote = new Vote();
        vote.setPost(post);
        vote.setUser(user);
        vote.setValue(value);
        vote.setTime(LocalDateTime.now());
        return vote;
    }
}
