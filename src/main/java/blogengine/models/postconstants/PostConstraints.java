package blogengine.models.postconstants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostConstraints {

    public static final int MIN_TITLE_SIZE = 5;
    public static final int MIN_TEXT_SIZE = 100;
    public static final int MIN_COMMENT_SIZE = 5;
    public static final int ANNOUNCE_LENGTH = 200;
}
