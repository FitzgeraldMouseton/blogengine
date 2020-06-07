package blogengine.util.validation.validators;

import blogengine.util.validation.constraints.CodeNotExpiredConstraint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.util.Base64;

@Slf4j
public class RestoreCodeValidator implements ConstraintValidator<CodeNotExpiredConstraint, String> {

   @Value("${restorecode.expiration.time}")
   private Integer restoreCodeExistenceTime;

   public void initialize(CodeNotExpiredConstraint constraint) {
   }

   public boolean isValid(String code, ConstraintValidatorContext context) {
      if (code == null || code.isEmpty()){
         return false;
      }
      String encodedTime = code.substring(40);
      String decodedTime = new String(Base64.getDecoder().decode(encodedTime));
      long time = Long.parseLong(decodedTime);
      Instant currentTime = Instant.now();
      Instant codeTime = Instant.ofEpochMilli(time);
      return currentTime.isBefore(codeTime.plusSeconds(restoreCodeExistenceTime));
   }
}
