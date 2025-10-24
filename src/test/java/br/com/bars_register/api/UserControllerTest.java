package br.com.bars_register.api;

import br.com.bars_register.application.services.UsuarioService;
import br.com.bars_register.domain.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1, "João Silva", "joao", "joao@email.com", true, "joao", "123456");
    }

    @Test
    void listar_DeveRetornarListaDeUsuarios() throws Exception {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioService.listarUsuarios()).thenReturn(usuarios);

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("João Silva"))
                .andExpect(jsonPath("$[0].login").value("joao"))
                .andExpect(jsonPath("$[0].email").value("joao@email.com"))
                .andExpect(jsonPath("$[0].status").value(true));

        verify(usuarioService).listarUsuarios();
    }

    @Test
    void criar_ComDadosValidos_DeveRetornar201() throws Exception {
        // Arrange
        Usuario novoUsuario = new Usuario(0, "Maria Santos", "maria", "maria@email.com", true, "maria", "654321");
        Usuario usuarioSalvo = new Usuario(2, "Maria Santos", "maria", "maria@email.com", true, "maria", "654321");
        
        when(usuarioService.criarUsuario(any(Usuario.class))).thenReturn(usuarioSalvo);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoUsuario)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/2"))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nome").value("Maria Santos"))
                .andExpect(jsonPath("$.login").value("maria"))
                .andExpect(jsonPath("$.email").value("maria@email.com"))
                .andExpect(jsonPath("$.status").value(true));

        verify(usuarioService).criarUsuario(any(Usuario.class));
    }

    @Test
    void criar_ComDadosInvalidos_DeveRetornar400() throws Exception {
        // Arrange
        Usuario usuarioInvalido = new Usuario(0, "", "", "", true, "", "");

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioInvalido)))
                .andExpect(status().isBadRequest());

        verify(usuarioService).criarUsuario(any(Usuario.class));
    }

    @Test
    void login_ComCredenciaisValidas_DeveRetornar200() throws Exception {
        // Arrange
        when(usuarioService.autenticar("joao", "123456")).thenReturn(Optional.of(usuario));

        // Act & Assert
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"joao\", \"senha\": \"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.login").value("joao"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.status").value(true));

        verify(usuarioService).autenticar("joao", "123456");
    }

    @Test
    void login_ComCredenciaisInvalidas_DeveRetornar401() throws Exception {
        // Arrange
        when(usuarioService.autenticar("joao", "senha_errada")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"joao\", \"senha\": \"senha_errada\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciais inválidas ou usuário inativo"));

        verify(usuarioService).autenticar("joao", "senha_errada");
    }

    @Test
    void login_ComLoginVazio_DeveRetornar400() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"\", \"senha\": \"123456\"}"))
                .andExpect(status().isBadRequest());

        verify(usuarioService).autenticar("", "123456");
    }

    @Test
    void login_ComSenhaVazia_DeveRetornar400() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"joao\", \"senha\": \"\"}"))
                .andExpect(status().isBadRequest());

        verify(usuarioService).autenticar("joao", "");
    }

    @Test
    void login_ComDadosNulos_DeveRetornar400() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": null, \"senha\": null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ComJsonInvalido_DeveRetornar400() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"joao\"}"))
                .andExpect(status().isBadRequest());
    }
}
