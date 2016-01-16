package org.books.ejb.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import org.books.ejb.exception.ValidationException;

public class ValidationInterceptor {

    private static final Logger logger = Logger.getLogger(ValidationInterceptor.class.getName());
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    @AroundInvoke
    public Object validate(InvocationContext ic) throws Exception {
        if (ic.getParameters() != null) {
            ExecutableValidator validator = factory.getValidator().forExecutables();
            Set<ConstraintViolation<Object>> violations = validator.validateParameters(ic.getTarget(), ic.getMethod(), ic.getParameters());
            if (!violations.isEmpty()) {
                List<String> messages = new ArrayList<>(violations.size());
                for (ConstraintViolation<Object> violation : violations) {
                    messages.add(violation.getRootBeanClass().getSimpleName() + "."
                            + violation.getPropertyPath() + ": " + violation.getMessage());
                }
                logger.warning(violations.toString());
                throw new ValidationException(messages);
            }
        }
        return ic.proceed();
    }
}
