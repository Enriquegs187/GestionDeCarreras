package es.uji.ei1027.project.model;

public class UserDetails {
    String username;
    String password;
    String tipoUsuario;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipoUsuario() {return tipoUsuario;}

    public void setTipoUsuario(String tipoUsuario) {this.tipoUsuario = tipoUsuario;}

    @Override
    public String toString() {
        return "UserDetails{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", tipoUsuario='" + tipoUsuario + '\'' +
                '}';
    }
}
