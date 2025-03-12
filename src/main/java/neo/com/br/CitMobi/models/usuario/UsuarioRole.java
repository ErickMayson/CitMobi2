package neo.com.br.CitMobi.models.usuario;

public enum UsuarioRole {

    ADMIN("ADMIN"),
    USER("USER");

    private final String role;

    UsuarioRole(String role) {
        this.role = role;
    }

    public String getRole(){
        return role;
    }

}
