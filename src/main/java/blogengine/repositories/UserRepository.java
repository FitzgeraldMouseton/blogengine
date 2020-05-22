package blogengine.repositories;

import blogengine.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByCode(String code);

    Optional<User> findFirstByIsModeratorTrueOrderByPostsForModerationAsc();

    default Optional<User> getModerator(){
        return findFirstByIsModeratorTrueOrderByPostsForModerationAsc();
    }
}
