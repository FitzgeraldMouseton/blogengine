package blogengine.repositories;

import blogengine.models.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {

    @Query("Select t from Tag t join Post p where p.active = 1 and p.moderationStatus = 'ACCEPTED' and p.time <= current_timestamp " +
            "and t.name like :query%")
    List<Tag> findAllByNameStartingWith(String query);

    @Query("Select t from Tag t join t.posts p where p.active = 1 and p.moderationStatus = 'ACCEPTED' and p.time <= current_timestamp ")
    List<Tag> findAllByEmptyQuery();
}
