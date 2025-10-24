package br.com.bars_register.api;

import br.com.bars_register.application.services.ProdutoService;
import br.com.bars_register.domain.Produto;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService produtoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = new Produto(1, "Cerveja", 10.0, 100);
    }

    @Test
    void listar_DeveRetornarListaDeProdutos() throws Exception {
        // Arrange
        List<Produto> produtos = Arrays.asList(produto);
        when(produtoService.listarTodos()).thenReturn(produtos);

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Cerveja"))
                .andExpect(jsonPath("$[0].preco").value(10.0))
                .andExpect(jsonPath("$[0].estoque").value(100));

        verify(produtoService).listarTodos();
    }

    @Test
    void buscar_ProdutoExistente_DeveRetornarProduto() throws Exception {
        // Arrange
        when(produtoService.buscarPorId(1)).thenReturn(Optional.of(produto));

        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Cerveja"))
                .andExpect(jsonPath("$.preco").value(10.0))
                .andExpect(jsonPath("$.estoque").value(100));

        verify(produtoService).buscarPorId(1);
    }

    @Test
    void buscar_ProdutoNaoExistente_DeveRetornar404() throws Exception {
        // Arrange
        when(produtoService.buscarPorId(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(produtoService).buscarPorId(999);
    }

    @Test
    void criar_ComDadosValidos_DeveRetornar201() throws Exception {
        // Arrange
        Produto novoProduto = new Produto(0, "Refrigerante", 7.5, 50);
        Produto produtoSalvo = new Produto(2, "Refrigerante", 7.5, 50);
        
        when(produtoService.cadastrar(any(Produto.class))).thenReturn(produtoSalvo);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoProduto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/products/2"))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nome").value("Refrigerante"))
                .andExpect(jsonPath("$.preco").value(7.5))
                .andExpect(jsonPath("$.estoque").value(50));

        verify(produtoService).cadastrar(any(Produto.class));
    }

    @Test
    void criar_ComDadosInvalidos_DeveRetornar400() throws Exception {
        // Arrange
        Produto produtoInvalido = new Produto(0, "", -10.0, -5);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produtoInvalido)))
                .andExpect(status().isBadRequest());

        verify(produtoService).cadastrar(any(Produto.class));
    }

    @Test
    void atualizar_ProdutoExistente_DeveRetornar200() throws Exception {
        // Arrange
        Produto produtoAtualizado = new Produto(1, "Cerveja Premium", 15.0, 80);
        when(produtoService.buscarPorId(1)).thenReturn(Optional.of(produto));
        when(produtoService.atualizar(any(Produto.class))).thenReturn(produtoAtualizado);

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produtoAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Cerveja Premium"))
                .andExpect(jsonPath("$.preco").value(15.0))
                .andExpect(jsonPath("$.estoque").value(80));

        verify(produtoService).buscarPorId(1);
        verify(produtoService).atualizar(any(Produto.class));
    }

    @Test
    void atualizar_ProdutoNaoExistente_DeveRetornar404() throws Exception {
        // Arrange
        Produto produtoAtualizado = new Produto(999, "Produto", 10.0, 50);
        when(produtoService.buscarPorId(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produtoAtualizado)))
                .andExpect(status().isNotFound());

        verify(produtoService).buscarPorId(999);
        verify(produtoService, never()).atualizar(any(Produto.class));
    }

    @Test
    void remover_ProdutoExistente_DeveRetornar204() throws Exception {
        // Arrange
        when(produtoService.remover(1)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(produtoService).remover(1);
    }

    @Test
    void remover_ProdutoNaoExistente_DeveRetornar404() throws Exception {
        // Arrange
        when(produtoService.remover(999)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(produtoService).remover(999);
    }

    @Test
    void atualizarEstoque_ComDadosValidos_DeveRetornar200() throws Exception {
        // Arrange
        Produto produtoAtualizado = new Produto(1, "Cerveja", 10.0, 150);
        when(produtoService.buscarPorId(1)).thenReturn(Optional.of(produtoAtualizado));

        // Act & Assert
        mockMvc.perform(patch("/api/products/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"delta\": 50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estoque").value(150));

        verify(produtoService).atualizarEstoque(1, 50);
        verify(produtoService).buscarPorId(1);
    }

    @Test
    void atualizarEstoque_ProdutoNaoExistente_DeveRetornar404() throws Exception {
        // Arrange
        when(produtoService.buscarPorId(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(patch("/api/products/999/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"delta\": 50}"))
                .andExpect(status().isNotFound());

        verify(produtoService).atualizarEstoque(999, 50);
        verify(produtoService).buscarPorId(999);
    }
}
