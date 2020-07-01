package blogengine.services;

import blogengine.models.Tag;
import blogengine.models.dto.blogdto.tagdto.SingleTagDto;
import blogengine.models.dto.blogdto.tagdto.TagsResponse;
import blogengine.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final float MIN_TAG_WEIGHT = 0.1f;

    public void save(final Tag tag) {
        tagRepository.save(tag);
    }

    public TagsResponse findTagsByName(final String query) {
        List<Tag> tagList = (query == null) ? tagRepository.findAllByEmptyQuery()
                : tagRepository.findAllByNameStartingWith(query);

        Map<String, Long> collect = tagList.stream().collect(Collectors.groupingBy(Tag::getName, Collectors.counting()));
        Long maxWeight = Collections.max(collect.values());

        List<SingleTagDto> tagDtoList = collect.entrySet().stream()
                .map(entry -> {
                    String tagName = entry.getKey();
                    float tagWeight = Precision.round((float) entry.getValue() / maxWeight, 3);
                    if (tagWeight < MIN_TAG_WEIGHT)
                        tagWeight = MIN_TAG_WEIGHT;
                    return new SingleTagDto(tagName, tagWeight);
                })
                .sorted(Comparator.comparing(SingleTagDto::getWeight).reversed())
                .collect(Collectors.toList());

        SingleTagDto[] tags = new SingleTagDto[tagDtoList.size()];
        tags = tagDtoList.toArray(tags);
        return new TagsResponse(tags);
    }
}
