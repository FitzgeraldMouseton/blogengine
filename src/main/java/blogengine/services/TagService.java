package blogengine.services;

import blogengine.models.Tag;
import blogengine.models.dto.TagDto;
import blogengine.repositories.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TagService {

    private TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    //TODO разобраться с весами
    public List<TagDto> findTagsByName(String query) {

        List<Tag> tags;
        if (query != null){
            tags = tagRepository.findAllByNameStartingWith(query);
        } else {
            tags = tagRepository.findAllBy();
        }

        List<TagDto> list = new ArrayList<>();
        for (Tag tag : tags) {
            TagDto tagDto = new TagDto(tag.getName(), 1.0);
            list.add(tagDto);
        }
        return list;
    }
}
