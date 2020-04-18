package blogengine.mappers;

import blogengine.models.Comment;
import blogengine.models.dto.CommentDTO;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Data
@Component
public class CommentDtoMapper {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
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
