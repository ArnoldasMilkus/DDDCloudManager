package lt.milkusteam.cloud.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by gediminas on 4/17/16.
 */
@Entity
@Table(name = "dbx_tokens")
public class DbxToken {

    private String username;
    private String token;

    public DbxToken() {
    }

    public DbxToken(String username, String token) {
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

    @Column(name = "token", nullable = false, length = 64)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "DbxToken{" +
                "username='" + username + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbxToken dbxToken = (DbxToken) o;

        if (!username.equals(dbxToken.username)) return false;
        return token.equals(dbxToken.token);
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + token.hashCode();
        return result;
    }
}
