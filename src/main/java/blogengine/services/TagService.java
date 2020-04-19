package blogengine.services;

import blogengine.models.Tag;
import blogengine.models.dto.blogdto.SingleTagDto;
import blogengine.models.dto.blogdto.TagsResponse;
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

    public TagsResponse findTagsByName(String query) {
        List<Tag> tagList = (query == null) ? tagRepository.findAllByEmptyQuery()
                : tagRepository.findAllByNameStartingWith(query);

        Map<String, List<Tag>> collect = tagList.stream().collect(Collectors.groupingBy(Tag::getName));
        Optional<List<Tag>> maxOptional = collect.values().stream().max(Comparator.comparing(List::size));
        if(maxOptional.isEmpty()){
            throw new NoSuchElementException("Лист тэгов пуст");
        }
        int maxWeight = maxOptional.get().size();
        List<SingleTagDto> collect1 = collect.entrySet().stream()
                .map(entry -> new SingleTagDto(entry.getKey(), Precision.round((double) entry.getValue().size() / maxWeight, 3)))
                .sorted(Comparator.comparing(SingleTagDto::getWeight).reversed())
                .collect(Collectors.toList());
        SingleTagDto[] tags = new SingleTagDto[collect1.size()];
        tags = collect1.toArray(tags);
        return new TagsResponse(tags);
    }
}
