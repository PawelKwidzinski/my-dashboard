package pl.kwidzinski.job4devs_ms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class JobUrlAlreadyExistsException extends RuntimeException {

    public JobUrlAlreadyExistsException(String message) {
        super(message);
    }
}
