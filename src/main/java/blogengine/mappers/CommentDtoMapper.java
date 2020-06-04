package blogengine.mappers;

import blogengine.models.Comment;
import blogengine.models.dto.blogdto.commentdto.CommentDTO;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Data
@Component
public class CommentDtoMapper {

    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private UserDtoMapper userDtoMapper;

    @Autowired
    public CommentDtoMapper(UserDtoMapper userDtoMapper) {
        this.userDtoMapper = userDtoMapper;
    }

    public CommentDTO commentToCommentDto(Comment comment){
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setTime(dateFormat.format(comment.getTime()));
        commentDTO.setUser(userDtoMapper.userToUserDto(comment.getUser()));
        commentDTO.setText(comment.getText());
        return commentDTO;
    }
}
