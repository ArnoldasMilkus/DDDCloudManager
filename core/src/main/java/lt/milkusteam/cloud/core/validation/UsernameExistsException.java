package lt.milkusteam.cloud.core.validation;

/**
 * Created by Arnoldas on 2016-04-21.
 */
@SuppressWarnings("serial")
public class UsernameExistsException extends Throwable {

    public UsernameExistsException(final String message) {
        super(message);
    }

}