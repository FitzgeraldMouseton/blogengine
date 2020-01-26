package blogengine.repository;

import blogengine.model.Tag;
import org.springframework.data.repository.CrudRepository;

public interface TagRepository extends CrudRepository<Tag, Integer> {
}
