package lt.milkusteam.cloud.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Vilintas on 2016-05-13.
 */
@Entity
@Table(name = "GDrive_tokens")
public class GDriveToken {

    private String username;
    private String token;

    public GDriveToken() {
    }

    public GDriveToken(String username, String token) {
        this.username = username;
        this.token = token;
    }

    @Id
    @Column(name = "username", nullable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "token", nullable = false, length = 73)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "GDriveToken{" +
                "username='" + username + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GDriveToken gDriveToken = (GDriveToken) o;

        if (!username.equals(gDriveToken.username)) return false;
        return token.equals(gDriveToken.token);
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + token.hashCode();
        return result;
    }
}
