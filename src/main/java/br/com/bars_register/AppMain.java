package br.com.bars_register;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import br.com.bars_register.application.repositories.ProdutoRepository;
import br.com.bars_register.application.repositories.VendaRepository;
import br.com.bars_register.application.services.ProdutoService;
import br.com.bars_register.application.services.VendaService;
import br.com.bars_register.application.services.RelatorioService;
import br.com.bars_register.domain.ItemVenda;
import br.com.bars_register.domain.Produto;
import br.com.bars_register.domain.Venda;
import br.com.bars_register.infrastructure.repo.InMemoryProdutoRepository;
import br.com.bars_register.infrastructure.repo.InMemoryVendaRepository;

public class AppMain {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        // Permite especificar a porta via argumento: mvn -q exec:java -Dexec.args="8081"
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) { }
        }
        Path webRoot = Paths.get("web").toAbsolutePath().normalize();
        if (!Files.exists(webRoot) || !Files.isDirectory(webRoot)) {
            System.err.println("Diretório 'web' não encontrado em: " + webRoot);
            System.exit(1);
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Inicializa serviços com repositórios em memória
        ProdutoRepository produtoRepo = new InMemoryProdutoRepository();
        VendaRepository vendaRepo = new InMemoryVendaRepository();
        ProdutoService produtoService = new ProdutoService(produtoRepo);
        VendaService vendaService = new VendaService(vendaRepo, produtoRepo);
        RelatorioService relatorioService = new RelatorioService(vendaRepo);

        // Adiciona um produto inicial para demonstração
        produtoService.cadastrar(new Produto(0, "Café", 5.0, 100));

        // Endpoints de API
        server.createContext("/api/products", new ProductsApiHandler(produtoService));
        server.createContext("/api/vendas", new VendasApiHandler(vendaService, produtoService, vendaRepo));
        server.createContext("/api/dashboard", new DashboardApiHandler(relatorioService, vendaRepo));
        server.createContext("/api/dashboard/produtos-vendidos", new ProdutosVendidosApiHandler(vendaRepo, produtoRepo));

        // Servidor de arquivos estáticos
        server.createContext("/", new StaticFileHandler(webRoot));
        server.setExecutor(null);
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Encerrando servidor...");
            server.stop(0);
        }));

        System.out.println("Servidor iniciado em http://localhost:" + port + "/ servindo de " + webRoot);
        System.out.println("Pressione Ctrl+C para encerrar.");
        // Bloqueia a thread principal para manter o servidor ativo
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException ignored) {}
    }

    static class StaticFileHandler implements HttpHandler {
        private final Path root;
        private final Map<String, String> mimeTypes = new HashMap<>();

        StaticFileHandler(Path root) {
            this.root = root;
            // Tipos comuns
            mimeTypes.put(".html", "text/html; charset=utf-8");
            mimeTypes.put(".css", "text/css; charset=utf-8");
            mimeTypes.put(".js", "application/javascript; charset=utf-8");
            mimeTypes.put(".json", "application/json; charset=utf-8");
            mimeTypes.put(".png", "image/png");
            mimeTypes.put(".jpg", "image/jpeg");
            mimeTypes.put(".jpeg", "image/jpeg");
            mimeTypes.put(".svg", "image/svg+xml");
            mimeTypes.put(".ico", "image/x-icon");
            mimeTypes.put(".woff2", "font/woff2");
            mimeTypes.put(".woff", "font/woff");
            mimeTypes.put(".ttf", "font/ttf");
            mimeTypes.put(".map", "application/json; charset=utf-8");
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                URI uri = exchange.getRequestURI();
                String pathStr = uri.getPath();

                if (pathStr == null || pathStr.isEmpty() || "/".equals(pathStr)) {
                    pathStr = "/index.html";
                }

                // Evita path traversal
                Path requested = root.resolve(pathStr.substring(1)).normalize();
                if (!requested.startsWith(root)) {
                    sendString(exchange, 403, "Acesso negado");
                    return;
                }

                if (Files.isDirectory(requested)) {
                    requested = requested.resolve("index.html");
                }

                if (!Files.exists(requested) || !Files.isReadable(requested)) {
                    sendString(exchange, 404, "Arquivo não encontrado");
                    return;
                }

                byte[] bytes = Files.readAllBytes(requested);
                String contentType = guessContentType(requested);
                exchange.getResponseHeaders().add("Content-Type", contentType);
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendString(exchange, 500, "Erro interno do servidor");
            }
        }

        private String guessContentType(Path file) {
            String name = file.getFileName().toString().toLowerCase();
            int dotIdx = name.lastIndexOf('.');
            String ext = dotIdx >= 0 ? name.substring(dotIdx) : "";
            return mimeTypes.getOrDefault(ext, "application/octet-stream");
        }

        private void sendString(HttpExchange exchange, int status, String body) throws IOException {
            byte[] data = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(status, data.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(data);
            }
        }
    }

    // Handlers de API
    static class ProductsApiHandler implements HttpHandler {
        private final ProdutoService produtoService;
        ProductsApiHandler(ProdutoService produtoService) { this.produtoService = produtoService; }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String[] parts = path.split("/");
                Integer id = (parts.length > 3) ? parseIntSafe(parts[3]) : null;

                if ("GET".equalsIgnoreCase(method) && id == null) {
                    // Listar todos
                    var itens = produtoService.listarTodos();
                    StringBuilder sb = new StringBuilder();
                    sb.append("{\"items\":[");
                    for (int i = 0; i < itens.size(); i++) {
                        Produto p = itens.get(i);
                        sb.append(String.format("{\"id\":%d,\"nome\":\"%s\",\"preco\":%s,\"estoque\":%d}",
                                p.getId(), escape(p.getNome()), Double.toString(p.getPreco()), p.getEstoque()));
                        if (i < itens.size() - 1) sb.append(",");
                    }
                    sb.append("],\"count\":").append(itens.size()).append("}");
                    sendJson(exchange, 200, sb.toString());
                    return;
                }
                if ("POST".equalsIgnoreCase(method) && id == null) {
                    Map<String, String> form = readForm(exchange);
                    String nome = form.getOrDefault("nome", "");
                    double preco = Double.parseDouble(form.getOrDefault("preco", "0"));
                    int estoque = Integer.parseInt(form.getOrDefault("estoque", "0"));
                    Produto novo = new Produto(0, nome, preco, estoque);
                    Produto criado = produtoService.cadastrar(novo);
                    String json = String.format("{\"id\":%d,\"nome\":\"%s\",\"preco\":%s,\"estoque\":%d}",
                            criado.getId(), escape(criado.getNome()), Double.toString(criado.getPreco()), criado.getEstoque());
                    sendJson(exchange, 201, json);
                    return;
                }
                if (id != null && "PUT".equalsIgnoreCase(method)) {
                    Map<String, String> form = readForm(exchange);
                    String nome = form.getOrDefault("nome", "");
                    double preco = Double.parseDouble(form.getOrDefault("preco", "0"));
                    int estoque = Integer.parseInt(form.getOrDefault("estoque", "0"));
                    Produto p = new Produto(id, nome, preco, estoque);
                    Produto atualizado = produtoService.atualizar(p);
                    String json = String.format("{\"id\":%d,\"nome\":\"%s\",\"preco\":%s,\"estoque\":%d}",
                            atualizado.getId(), escape(atualizado.getNome()), Double.toString(atualizado.getPreco()), atualizado.getEstoque());
                    sendJson(exchange, 200, json);
                    return;
                }
                if (id != null && "DELETE".equalsIgnoreCase(method)) {
                    boolean ok = produtoService.remover(id);
                    sendJson(exchange, ok ? 200 : 404, "{\"ok\":" + ok + "}");
                    return;
                }
                sendJson(exchange, 405, "{\"error\":\"Método não suportado\"}");
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, "{\"error\":\"Erro interno\"}");
            }
        }

        private static Map<String, String> readForm(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> form = new HashMap<>();
            for (String pair : body.split("&")) {
                int eq = pair.indexOf('=');
                if (eq > 0) {
                    String k = urlDecode(pair.substring(0, eq));
                    String v = urlDecode(pair.substring(eq + 1));
                    form.put(k, v);
                }
            }
            return form;
        }
        private static String urlDecode(String s) { return java.net.URLDecoder.decode(s, StandardCharsets.UTF_8); }
        private static Integer parseIntSafe(String s) { try { return Integer.parseInt(s); } catch (Exception e) { return null; } }
        private static String escape(String s) { return s.replace("\\", "\\\\").replace("\"", "\\\""); }
        private static void sendJson(HttpExchange ex, int status, String json) throws IOException {
            byte[] data = json.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            ex.sendResponseHeaders(status, data.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(data); }
        }
    }

    // VendasApiHandler atualizado abaixo com suporte ao repositório e GET
    static class VendasApiHandler implements HttpHandler {
        private final VendaService vendaService;
        private final ProdutoService produtoService;
        private final VendaRepository vendaRepository;
        VendasApiHandler(VendaService vendaService, ProdutoService produtoService, VendaRepository vendaRepository) {
            this.vendaService = vendaService;
            this.produtoService = produtoService;
            this.vendaRepository = vendaRepository;
        }
         @Override
         public void handle(HttpExchange exchange) throws IOException {
             try {
                String method = exchange.getRequestMethod();
                if ("GET".equalsIgnoreCase(method)) {
                    // Lista de vendas recentes com filtros
                    String q = exchange.getRequestURI().getQuery();
                    int limit = 10;
                    String sort = "date"; // or "total"
                    String order = "desc";
                    String period = "week"; // day|week|month
                    if (q != null && !q.isBlank()) {
                        for (String part : q.split("&")) {
                            String[] kv = part.split("=");
                            if (kv.length == 2) {
                                String key = kv[0]; String val = kv[1];
                                if ("limit".equals(key)) { try { limit = Math.max(1, Integer.parseInt(val)); } catch (Exception ignored) {} }
                                else if ("sort".equals(key)) { if ("total".equalsIgnoreCase(val)) sort = "total"; }
                                else if ("order".equals(key)) { if ("asc".equalsIgnoreCase(val)) order = "asc"; }
                                else if ("period".equals(key)) { if (val.equalsIgnoreCase("day")||val.equalsIgnoreCase("week")||val.equalsIgnoreCase("month")) period = val.toLowerCase(); }
                            }
                        }
                    }
                    java.time.LocalDateTime inicio;
                    java.time.LocalDateTime fim = java.time.LocalDateTime.now();
                    java.time.LocalDate today = java.time.LocalDate.now();
                    if ("day".equals(period)) inicio = today.atStartOfDay();
                    else if ("month".equals(period)) inicio = today.withDayOfMonth(1).atStartOfDay();
                    else /* week */ inicio = today.minusDays(6).atStartOfDay();
                    var vendas = vendaRepository.findByPeriodo(inicio, fim);
                    java.util.Comparator<Venda> cmp = "total".equals(sort)
                            ? java.util.Comparator.comparingDouble(Venda::getTotal)
                            : java.util.Comparator.comparing(Venda::getDataVenda);
                    vendas.sort("asc".equals(order) ? cmp : cmp.reversed());
                    if (vendas.size() > limit) vendas = vendas.subList(0, limit);
                    StringBuilder sb = new StringBuilder();
                    sb.append("{\"items\":[");
                    for (int i = 0; i < vendas.size(); i++) {
                        Venda v = vendas.get(i);
                        sb.append("{")
                          .append("\"id\":").append(v.getId()).append(",")
                          .append("\"data\":\"").append(v.getDataVenda().toString()).append("\",")
                          .append("\"total\":").append(Double.toString(v.getTotal())).append(",")
                          .append("\"tipoPagamento\":\"").append(escape(v.getTipoPagamento())).append("\",")
                          .append("\"itens\":[");
                        java.util.List<ItemVenda> itens = v.getItens();
                        for (int j = 0; j < itens.size(); j++) {
                            ItemVenda iv = itens.get(j);
                            sb.append(String.format("{\"produto\":\"%s\",\"qtd\":%d,\"subtotal\":%s}",
                                    escape(iv.getProduto().getNome()), iv.getQuantidade(), Double.toString(iv.getSubtotal())));
                            if (j < itens.size() - 1) sb.append(",");
                        }
                        sb.append("]}");
                        if (i < vendas.size() - 1) sb.append(",");
                    }
                    sb.append("],\"count\":").append(vendas.size()).append("}");
                    sendJson(exchange, 200, sb.toString());
                    return;
                }
                if (!"POST".equalsIgnoreCase(method)) {
                    sendJson(exchange, 405, "{\"error\":\"Método não suportado\"}");
                    return;
                }
                 Map<String, String> form = ProductsApiHandler.readForm(exchange);
                 String itemsStr = form.getOrDefault("items", ""); // formato: "id:qty,id:qty"
                 String tipoPagamento = form.getOrDefault("tipoPagamento", "INDEFINIDO");
                 java.util.List<ItemVenda> itens = new java.util.ArrayList<>();
                 if (!itemsStr.isBlank()) {
                     for (String token : itemsStr.split(",")) {
                         String[] kv = token.trim().split(":");
                         if (kv.length == 2) {
                             int pid = Integer.parseInt(kv[0]);
                             int qty = Integer.parseInt(kv[1]);
                             Produto p = produtoService.buscarPorId(pid).orElse(null);
                             if (p != null) {
                                 itens.add(new ItemVenda(p, qty));
                             }
                         }
                     }
                 }
                 Venda venda = vendaService.registrarVenda(itens, tipoPagamento);
                 String json = String.format("{\"id\":%d,\"total\":%s,\"tipoPagamento\":\"%s\"}",
                         venda.getId(), Double.toString(venda.getTotal()), venda.getTipoPagamento());
                 sendJson(exchange, 201, json);
             } catch (Exception e) {
                 e.printStackTrace();
                 sendJson(exchange, 400, "{\"error\":\"" + escape(e.getMessage()) + "\"}");
             }
         }
        private static void sendJson(HttpExchange ex, int status, String json) throws IOException {
            byte[] data = json.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            ex.sendResponseHeaders(status, data.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(data); }
        }
        private static String escape(String s) { return s.replace("\\", "\\\\").replace("\"", "\\\""); }
    }

    static class DashboardApiHandler implements HttpHandler {
        private final RelatorioService relatorioService;
        private final VendaRepository vendaRepository;
        DashboardApiHandler(RelatorioService relatorioService, VendaRepository vendaRepository) {
            this.relatorioService = relatorioService;
            this.vendaRepository = vendaRepository;
        }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendJson(exchange, 405, "{\"error\":\"Método não suportado\"}");
                    return;
                }
                // Métricas semanais e por período com série diária
                String q = exchange.getRequestURI().getQuery();
                String period = "week"; // day|week|month
                if (q != null && !q.isBlank()) {
                    for (String part : q.split("&")) {
                        String[] kv = part.split("=");
                        if (kv.length == 2 && "period".equals(kv[0])) {
                            String val = kv[1].toLowerCase();
                            if (val.equals("day") || val.equals("week") || val.equals("month")) period = val;
                        }
                    }
                }
                java.time.LocalDate hoje = java.time.LocalDate.now();
                java.time.LocalDateTime fim = java.time.LocalDateTime.now();
                java.time.LocalDateTime inicio;
                if ("day".equals(period)) inicio = hoje.atStartOfDay();
                else if ("month".equals(period)) inicio = hoje.withDayOfMonth(1).atStartOfDay();
                else /* week */ inicio = hoje.minusDays(6).atStartOfDay();

                var vendasPeriodo = vendaRepository.findByPeriodo(inicio, fim);
                double totalPeriodo = 0.0;
                int vendasCount = vendasPeriodo.size();
                // Série diária: últimos N dias
                java.util.Map<java.time.LocalDate, double[]> serie = new java.util.LinkedHashMap<>();
                int dias = "day".equals(period) ? 1 : ("month".equals(period) ? hoje.lengthOfMonth() : 7);
                for (int i = dias - 1; i >= 0; i--) {
                    java.time.LocalDate d = hoje.minusDays(i);
                    serie.put(d, new double[]{0.0, 0.0}); // [total, count]
                }
                for (Venda v : vendasPeriodo) {
                    totalPeriodo += v.getTotal();
                    java.time.LocalDate d = v.getDataVenda().toLocalDate();
                    double[] agg = serie.get(d);
                    if (agg != null) {
                        agg[0] += v.getTotal();
                        agg[1] += 1;
                    }
                }
                double ticketMedio = vendasCount > 0 ? totalPeriodo / vendasCount : 0.0;
                // Comparação com período anterior
                java.time.LocalDateTime inicioPrev;
                java.time.LocalDateTime fimPrev;
                if ("day".equals(period)) { inicioPrev = hoje.minusDays(1).atStartOfDay(); fimPrev = hoje.atStartOfDay(); }
                else if ("month".equals(period)) { inicioPrev = hoje.minusMonths(1).withDayOfMonth(1).atStartOfDay(); fimPrev = hoje.withDayOfMonth(1).atStartOfDay(); }
                else { inicioPrev = inicio.minusDays(7); fimPrev = inicio; }
                double totalPrev = 0.0;
                for (Venda v : vendaRepository.findByPeriodo(inicioPrev, fimPrev)) totalPrev += v.getTotal();
                double comparacaoPercent = (totalPrev > 0) ? ((totalPeriodo - totalPrev) / totalPrev) * 100.0 : (totalPeriodo > 0 ? 100.0 : 0.0);
                // Dia de maior movimento (por total)
                java.time.LocalDate diaMax = null; double maxTotal = -1; int maxCount = 0;
                for (var entry : serie.entrySet()) {
                    double tot = entry.getValue()[0];
                    int cnt = (int) entry.getValue()[1];
                    if (tot > maxTotal) { maxTotal = tot; maxCount = cnt; diaMax = entry.getKey(); }
                }
                StringBuilder sb = new StringBuilder();
                sb.append("{")
                  .append("\"period\":\"").append(period).append("\",")
                  .append("\"totalPeriodo\":").append(Double.toString(totalPeriodo)).append(",")
                  .append("\"ticketMedio\":").append(Double.toString(ticketMedio)).append(",")
                  .append("\"comparacaoPercent\":").append(Double.toString(comparacaoPercent)).append(",")
                  .append("\"dias\":[");
                int i = 0; int size = serie.size();
                for (var entry : serie.entrySet()) {
                    java.time.LocalDate d = entry.getKey();
                    double[] agg = entry.getValue();
                    sb.append("{")
                      .append("\"data\":\"").append(d.toString()).append("\",")
                      .append("\"total\":").append(Double.toString(agg[0])).append(",")
                      .append("\"count\":").append((int) agg[1])
                      .append("}");
                    if (i < size - 1) sb.append(",");
                    i++;
                }
                sb.append("],\"diaMaisMovimento\":{")
                  .append("\"data\":\"").append(diaMax != null ? diaMax.toString() : hoje.toString()).append("\",")
                  .append("\"total\":").append(Double.toString(maxTotal >= 0 ? maxTotal : 0.0)).append(",")
                  .append("\"count\":").append(maxCount)
                  .append("}}");
                sendJson(exchange, 200, sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, "{\"error\":\"Erro interno\"}");
            }
        }
        private static void sendJson(HttpExchange ex, int status, String json) throws IOException {
            byte[] data = json.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            ex.sendResponseHeaders(status, data.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(data); }
        }
    }

    static class ProdutosVendidosApiHandler implements HttpHandler {
        private final VendaRepository vendaRepository;
        private final ProdutoRepository produtoRepository;
        
        ProdutosVendidosApiHandler(VendaRepository vendaRepository, ProdutoRepository produtoRepository) {
            this.vendaRepository = vendaRepository;
            this.produtoRepository = produtoRepository;
        }
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendJson(exchange, 405, "{\"error\":\"Método não suportado\"}");
                    return;
                }
                
                // Parâmetros de consulta
                String q = exchange.getRequestURI().getQuery();
                int limit = 10;
                String period = "week"; // day|week|month|all
                
                if (q != null && !q.isBlank()) {
                    for (String part : q.split("&")) {
                        String[] kv = part.split("=");
                        if (kv.length == 2) {
                            String key = kv[0]; 
                            String val = kv[1];
                            if ("limit".equals(key)) { 
                                try { 
                                    limit = Math.max(1, Math.min(50, Integer.parseInt(val))); 
                                } catch (Exception ignored) {} 
                            }
                            else if ("period".equals(key)) { 
                                if (val.equalsIgnoreCase("day") || val.equalsIgnoreCase("week") || 
                                    val.equalsIgnoreCase("month") || val.equalsIgnoreCase("all")) {
                                    period = val.toLowerCase(); 
                                }
                            }
                        }
                    }
                }
                
                // Definir período de consulta
                java.time.LocalDateTime fim = java.time.LocalDateTime.now();
                java.time.LocalDateTime inicio;
                java.time.LocalDate hoje = java.time.LocalDate.now();
                
                switch (period) {
                    case "day":
                        inicio = hoje.atStartOfDay();
                        break;
                    case "month":
                        inicio = hoje.withDayOfMonth(1).atStartOfDay();
                        break;
                    case "all":
                        inicio = java.time.LocalDateTime.of(2020, 1, 1, 0, 0);
                        break;
                    default: // week
                        inicio = hoje.minusDays(6).atStartOfDay();
                        break;
                }
                
                // Buscar vendas do período
                var vendasPeriodo = vendaRepository.findByPeriodo(inicio, fim);
                
                // Agrupar por produto e calcular totais
                java.util.Map<Integer, ProdutoVendido> produtosVendidos = new java.util.HashMap<>();
                
                for (Venda venda : vendasPeriodo) {
                    for (ItemVenda item : venda.getItens()) {
                        Produto produto = item.getProduto();
                        int produtoId = produto.getId();
                        ProdutoVendido pv = produtosVendidos.get(produtoId);
                        if (pv == null) {
                            pv = new ProdutoVendido(produtoId, produto.getNome(), produto.getPreco());
                            produtosVendidos.put(produtoId, pv);
                        }
                        pv.adicionarVenda(item.getQuantidade(), produto.getPreco());
                    }
                }
                
                // Ordenar por quantidade vendida (decrescente)
                var produtosOrdenados = produtosVendidos.values().stream()
                    .sorted((a, b) -> Integer.compare(b.quantidadeTotal, a.quantidadeTotal))
                    .limit(limit)
                    .collect(java.util.stream.Collectors.toList());
                
                // Construir JSON de resposta
                StringBuilder sb = new StringBuilder();
                sb.append("{\"produtos\":[");
                
                for (int i = 0; i < produtosOrdenados.size(); i++) {
                    ProdutoVendido pv = produtosOrdenados.get(i);
                    sb.append("{")
                      .append("\"id\":").append(pv.id).append(",")
                      .append("\"nome\":\"").append(escape(pv.nome)).append("\",")
                      .append("\"precoUnitario\":").append(Double.toString(pv.precoUnitario)).append(",")
                      .append("\"quantidadeVendida\":").append(pv.quantidadeTotal).append(",")
                      .append("\"valorTotal\":").append(Double.toString(pv.valorTotal))
                      .append("}");
                    if (i < produtosOrdenados.size() - 1) sb.append(",");
                }
                
                sb.append("],\"period\":\"").append(period).append("\",")
                  .append("\"count\":").append(produtosOrdenados.size()).append("}");
                
                sendJson(exchange, 200, sb.toString());
                
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, "{\"error\":\"Erro interno\"}");
            }
        }
        
        private static String escape(String str) {
            if (str == null) return "";
            return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
        }
        
        private static void sendJson(HttpExchange ex, int status, String json) throws IOException {
            byte[] data = json.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            ex.sendResponseHeaders(status, data.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(data); }
        }
        
        // Classe auxiliar para agrupar dados de produtos vendidos
        private static class ProdutoVendido {
            final int id;
            final String nome;
            final double precoUnitario;
            int quantidadeTotal = 0;
            double valorTotal = 0.0;
            
            ProdutoVendido(int id, String nome, double precoUnitario) {
                this.id = id;
                this.nome = nome;
                this.precoUnitario = precoUnitario;
            }
            
            void adicionarVenda(int quantidade, double precoVenda) {
                this.quantidadeTotal += quantidade;
                this.valorTotal += quantidade * precoVenda;
            }
        }
    }
}