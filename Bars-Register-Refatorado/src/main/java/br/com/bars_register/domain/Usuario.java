package br.com.bars_register.domain;

public class Usuario {
    private int id;
    private String nome;
    private String tipoUsuario;
    private String email;
    private boolean status;
    private String login;
    private String senha;

    public Usuario() {}

    public Usuario(int id, String nome, String tipoUsuario, String email, boolean status, String login, String senha) {
        this.id = id;
        this.nome = nome;
        this.tipoUsuario = tipoUsuario;
        this.email = email;
        this.status = status;
        this.login = login;
        this.senha = senha;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}