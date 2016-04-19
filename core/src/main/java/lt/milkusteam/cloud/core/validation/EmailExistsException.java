package lt.milkusteam.cloud.core.validation;
/**
 * Created by Arnoldas on 4/18/16.
 */
@SuppressWarnings("serial")
public class EmailExistsException extends Throwable {

    public EmailExistsException(final String message) {
        super(message);
    }

}
