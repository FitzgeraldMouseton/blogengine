package blogengine.models.dto.blogdto;

import lombok.Data;

@Data
public class BlogStatisticsDto {

    private int postsCount;
    private int likesCount;
    private int dislikesCount;
    private int viewsCount;
    private String firstPublication;
}
