package lt.milkusteam.cloud.core.model;

/**
 * Created by gediminas on 5/16/16.
 */
public class DbxNotLinkedException extends RuntimeException {
    public DbxNotLinkedException() {
        super();
    }
    public DbxNotLinkedException(String message) {
        super(message);
    }
}
