//package blogengine.mappers;
//
//import blogengine.models.Comment;
//import blogengine.models.User;
//import blogengine.models.dto.blogdto.commentdto.CommentDTO;
//import blogengine.models.dto.userdto.UserDto;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ActiveProfiles("test")
//@SpringBootTest
//class CommentDtoMapperTest {
//
//    @Autowired
//    private CommentDtoMapper mapper;
//
//    @BeforeEach
//    void setUp() {
//    }
//
//    @Test
//    void commentToCommentDto() {
//
//        User user = new User();
//        Comment comment = new Comment();
//        comment.setText("123");
//
//
//        CommentDTO expectedDto = new CommentDTO();
//        expectedDto.setText("123");
//
//        CommentDTO actualDto = mapper.commentToCommentDto(comment);
//
//        Assertions.assertEquals(expectedDto, actualDto);
//    }
//}