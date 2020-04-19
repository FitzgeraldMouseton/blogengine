package blogengine.services;

import blogengine.models.ModerationStatus;
import blogengine.models.Tag;
import blogengine.models.dto.TagDto;
import blogengine.repositories.TagRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TagService {

    private TagRepository tagRepository;

    //TODO round
    public List<TagDto> findTagsByName(String query) {
        List<Tag> tags = (query == null) ? tagRepository.findAllByEmptyQuery()
                : tagRepository.findAllByNameStartingWith(query);

        Map<String, List<Tag>> collect = tags.stream().collect(Collectors.groupingBy(Tag::getName));
        Optional<List<Tag>> maxOptional = collect.values().stream().max(Comparator.comparing(List::size));
        if(maxOptional.isEmpty()){
            throw new NoSuchElementException("Лист тэгов пуст");
        }
        int max = maxOptional.get().size();
        return collect.entrySet().stream()
                .map(entry -> new TagDto(entry.getKey(), Precision.round((double) entry.getValue().size() / max, 3)))
                .sorted(Comparator.comparing(TagDto::getWeight).reversed())
                .collect(Collectors.toList());
    }
}
