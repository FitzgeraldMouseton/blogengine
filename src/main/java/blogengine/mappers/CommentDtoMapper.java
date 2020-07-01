package blogengine.mappers;

import blogengine.models.Comment;
import blogengine.models.dto.blogdto.commentdto.CommentDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Data
@Component
@RequiredArgsConstructor
public class CommentDtoMapper {

    private final UserDtoMapper userDtoMapper;
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public CommentDTO commentToCommentDto(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setTime(dateFormat.format(comment.getTime()));
        commentDTO.setUser(userDtoMapper.userToUserDto(comment.getUser()));
        commentDTO.setText(comment.getText());
        return commentDTO;
    }
}
