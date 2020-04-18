package blogengine.repositories;

import blogengine.models.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {

    List<Tag> findAllByNameStartingWith(String query);
    List<Tag> findAllBy();
}
