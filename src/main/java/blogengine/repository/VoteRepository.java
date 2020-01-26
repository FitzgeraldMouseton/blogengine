package blogengine.repository;

import blogengine.model.Vote;
import org.springframework.data.repository.CrudRepository;

public interface VoteRepository extends CrudRepository<Vote, Integer> {
}
