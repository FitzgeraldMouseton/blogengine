package blogengine.services;

import blogengine.models.Tag;
import blogengine.models.dto.blogdto.tagdto.SingleTagDto;
import blogengine.models.dto.blogdto.tagdto.TagsResponse;
import blogengine.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Value("${tag.min_weight}")
    private float tagMinWeight;

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
                    if (tagWeight < tagMinWeight)
                        tagWeight = tagMinWeight;
                    return new SingleTagDto(tagName, tagWeight);
                })
                .sorted(Comparator.comparing(SingleTagDto::getWeight).reversed())
                .collect(Collectors.toList());

        SingleTagDto[] tags = new SingleTagDto[tagDtoList.size()];
        tags = tagDtoList.toArray(tags);
        return new TagsResponse(tags);
    }
}
