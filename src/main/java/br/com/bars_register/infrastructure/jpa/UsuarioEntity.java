package br.com.bars_register.infrastructure.jpa;

import jakarta.persistence.*;
import br.com.bars_register.domain.Usuario;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(nullable = false, length = 50, unique = true)
    private String login;
    
    @Column(nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false)
    private Boolean status;
    
    @Column(nullable = false, length = 50)
    private String senha;
    
    // Construtores
    public UsuarioEntity() {}
    
    public UsuarioEntity(String nome, String login, String email, Boolean status, String senha) {
        this.nome = nome;
        this.login = login;
        this.email = email;
        this.status = status;
        this.senha = senha;
    }
    
    // Métodos de conversão
    public Usuario toDomain() {
        return new Usuario(id, nome, login, email, status, login, senha);
    }
    
    public static UsuarioEntity fromDomain(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.id = usuario.getId();
        entity.nome = usuario.getNome();
        entity.login = usuario.getLogin();
        entity.email = usuario.getEmail();
        entity.status = usuario.isStatus();
        entity.senha = usuario.getSenha();
        return entity;
    }
    
    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
