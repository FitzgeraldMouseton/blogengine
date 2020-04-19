package blogengine.services;

import blogengine.models.Post;
import blogengine.models.dto.blogdto.BlogStatisticsDto;
import blogengine.models.dto.blogdto.CalendarDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class GeneralService {

    private PostService postService;

    public BlogStatisticsDto getBlogStatistics(){
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<Post> posts = postService.getAllPots();
        BlogStatisticsDto statisticsDto = new BlogStatisticsDto();
        statisticsDto.setPostsCount(posts.size());
        statisticsDto.setFirstPublication(dateFormat.format(LocalDateTime.now()));
        posts.forEach(post -> {
            post.getVotes().forEach(vote -> {
                if(vote.getValue() == 1)
                    statisticsDto.setLikesCount(statisticsDto.getLikesCount() + 1);
                else if (vote.getValue() == -1)
                    statisticsDto.setDislikesCount(statisticsDto.getDislikesCount() + 1);
            });
            statisticsDto.setViewsCount(statisticsDto.getViewsCount() + post.getViewCount());
            LocalDateTime date = LocalDateTime.parse(statisticsDto.getFirstPublication(), dateFormat);
            if(date.isAfter(post.getTime()))
                statisticsDto.setFirstPublication(dateFormat.format(post.getTime()));
        });
        return statisticsDto;
    }

    public CalendarDto calendar(int year){
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        CalendarDto calendarDto = new CalendarDto();
        List<Post> posts = postService.getAllPots();

        Map<String, Integer> postsPerDate = posts.stream().filter(post -> post.getTime().getYear() == year)
                .collect(Collectors.groupingBy(p -> p.getTime().toLocalDate()))
                .entrySet().stream()
                .collect(Collectors.toMap(e -> dateFormat.format(e.getKey()), e -> e.getValue().size()))
                .entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        List<Integer> collect = posts.stream().map(post -> post.getTime().getYear()).distinct()
                .sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        Integer[] years = new Integer[collect.size()];
        years = collect.toArray(years);

        calendarDto.setPosts(postsPerDate);
        calendarDto.setYears(years);
        return calendarDto;
    }
}
