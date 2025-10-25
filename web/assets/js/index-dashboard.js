// Dashboard da página inicial com vendas totais, produtos mais vendidos e gráfico interativo
(function () {
  // Elementos do DOM
  const vendasTotaisEl = document.getElementById("vendasTotais");
  const itensVendidosEl = document.getElementById("itensVendidos");
  const produtosBodyEl = document.getElementById("produtosBody");
  const graficoCanvasEl = document.getElementById("vendasChart");
  
  // Indicadores de carregamento
  const loadingVendas = document.getElementById("loading-vendas");
  const loadingItens = document.getElementById("loading-itens");
  const loadingProdutos = document.getElementById("loading-produtos");
  const loadingGrafico = document.getElementById("loading-grafico");

  // Variáveis globais
  let vendasChart = null;
  let updateInterval = null;

  // Verificar se os elementos necessários existem
  if (!vendasTotaisEl || !itensVendidosEl || !produtosBodyEl || !graficoCanvasEl) {
    console.error("Elementos necessários não encontrados no DOM");
    return;
  }

  // Função para formatar valores em moeda brasileira
  function formatarMoeda(valor) {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(valor || 0);
  }

  // Função para formatar números
  function formatarNumero(numero) {
    return new Intl.NumberFormat('pt-BR').format(numero || 0);
  }

  // Função para mostrar/ocultar indicadores de carregamento
  function mostrarCarregamento(elemento, mostrar) {
    if (elemento) {
      elemento.style.display = mostrar ? 'flex' : 'none';
    }
  }

  // Função para carregar dados de vendas totais e itens vendidos
  async function carregarResumoVendas() {
    try {
      mostrarCarregamento(loadingVendas, true);
      mostrarCarregamento(loadingItens, true);

      const response = await fetch("/api/dashboard", {
        headers: { "Accept": "application/json" }
      });

      if (!response.ok) {
        throw new Error(`Erro HTTP: ${response.status}`);
      }

      const data = await response.json();
      
      // Atualizar vendas totais
      const totalVendas = Number(data.totalPeriodo || 0);
      vendasTotaisEl.innerHTML = `<strong>${formatarMoeda(totalVendas)}</strong>`;
      
      // Calcular total de itens vendidos
      const totalItens = data.dias ? data.dias.reduce((total, dia) => total + (dia.count || 0), 0) : 0;
      itensVendidosEl.innerHTML = `<strong>${formatarNumero(totalItens)}</strong>`;

      mostrarCarregamento(loadingVendas, false);
      mostrarCarregamento(loadingItens, false);

      return data;
    } catch (error) {
      console.error("Erro ao carregar resumo de vendas:", error);
      vendasTotaisEl.innerHTML = '<span style="color: var(--danger);">Erro ao carregar</span>';
      itensVendidosEl.innerHTML = '<span style="color: var(--danger);">Erro ao carregar</span>';
      
      mostrarCarregamento(loadingVendas, false);
      mostrarCarregamento(loadingItens, false);
      
      throw error;
    }
  }

  // Função para carregar produtos mais vendidos
  async function carregarProdutosMaisVendidos() {
    try {
      mostrarCarregamento(loadingProdutos, true);

      const response = await fetch("/api/dashboard/produtos-vendidos?limit=10&period=week", {
        headers: { "Accept": "application/json" }
      });

      if (!response.ok) {
        throw new Error(`Erro HTTP: ${response.status}`);
      }

      const data = await response.json();
      const produtos = data.produtos || [];

      // Limpar tabela
      produtosBodyEl.innerHTML = '';

      if (produtos.length === 0) {
        produtosBodyEl.innerHTML = '<tr><td colspan="3" class="text-center">Nenhum produto vendido ainda</td></tr>';
      } else {
        produtos.forEach(produto => {
          const row = document.createElement('tr');
          row.innerHTML = `
            <td>${produto.nome}</td>
            <td>${formatarNumero(produto.quantidadeVendida)}</td>
            <td>${formatarMoeda(produto.valorTotal)}</td>
          `;
          produtosBodyEl.appendChild(row);
        });
      }

      mostrarCarregamento(loadingProdutos, false);
      return produtos;
    } catch (error) {
      console.error("Erro ao carregar produtos mais vendidos:", error);
      produtosBodyEl.innerHTML = '<tr><td colspan="3" class="text-center" style="color: var(--danger);">Erro ao carregar produtos</td></tr>';
      mostrarCarregamento(loadingProdutos, false);
      throw error;
    }
  }

  // Função para criar/atualizar gráfico de vendas
  async function criarGraficoVendas() {
    try {
      mostrarCarregamento(loadingGrafico, true);

      const response = await fetch("/api/dashboard?period=week", {
        headers: { "Accept": "application/json" }
      });

      if (!response.ok) {
        throw new Error(`Erro HTTP: ${response.status}`);
      }

      const data = await response.json();
      const dias = data.dias || [];

      // Preparar dados para o gráfico
      const labels = dias.map(dia => {
        const date = new Date(dia.data);
        return date.toLocaleDateString('pt-BR', { weekday: 'short', day: '2-digit', month: '2-digit' });
      });
      
      const valores = dias.map(dia => dia.total || 0);
      const quantidades = dias.map(dia => dia.count || 0);

      // Destruir gráfico anterior se existir
      if (vendasChart) {
        vendasChart.destroy();
      }

      // Configuração do gráfico
      const ctx = graficoCanvasEl.getContext('2d');

      // Tooltip externo (div) para exibir valores ao passar o mouse
      function externalTooltipHandler(context) {
        const { chart, tooltip } = context;
        let tooltipEl = chart.canvas.parentNode.querySelector('div.chart-tooltip');

        // Cria o elemento se não existir
        if (!tooltipEl) {
          tooltipEl = document.createElement('div');
          tooltipEl.className = 'chart-tooltip';
          chart.canvas.parentNode.appendChild(tooltipEl);
        }

        // Oculta se não houver tooltip
        if (tooltip.opacity === 0) {
          tooltipEl.style.opacity = 0;
          return;
        }

        // Conteúdo do tooltip
        if (tooltip.dataPoints && tooltip.dataPoints.length > 0) {
          const idx = tooltip.dataPoints[0].dataIndex;
          const valor = valores[idx] || 0;
          const quantidade = quantidades[idx] || 0;
          tooltipEl.innerHTML = `<div><strong>${formatarMoeda(valor)}</strong><br/>Transações: ${formatarNumero(quantidade)}</div>`;
        }

        // Posição do tooltip
        const { offsetLeft: left, offsetTop: top } = chart.canvas;
        tooltipEl.style.opacity = 1;
        tooltipEl.style.left = left + tooltip.caretX + 'px';
        tooltipEl.style.top = top + tooltip.caretY + 'px';
      }

      vendasChart = new Chart(ctx, {
        type: 'line',
        data: {
          labels: labels,
          datasets: [{
            label: 'Vendas (R$)',
            data: valores,
            borderColor: 'rgb(13, 110, 253)',
            backgroundColor: 'rgba(13, 110, 253, 0.1)',
            borderWidth: 2,
            fill: true,
            tension: 0.4,
            pointBackgroundColor: 'rgb(13, 110, 253)',
            pointBorderColor: '#fff',
            pointBorderWidth: 2,
            pointRadius: 5,
            pointHoverRadius: 7
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          interaction: {
            intersect: false,
            mode: 'index'
          },
          plugins: {
            legend: {
              display: true,
              position: 'top'
            },
            tooltip: {
              enabled: false,
              external: externalTooltipHandler
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                callback: function(value) {
                  return formatarMoeda(value);
                }
              },
              grid: {
                color: 'rgba(0, 0, 0, 0.1)'
              }
            },
            x: {
              grid: {
                color: 'rgba(0, 0, 0, 0.1)'
              }
            }
          }
        }
      });

      mostrarCarregamento(loadingGrafico, false);
      document.getElementById("grafico").setAttribute("aria-hidden", "false");

    } catch (error) {
      console.error("Erro ao criar gráfico de vendas:", error);
      mostrarCarregamento(loadingGrafico, false);
      
      // Mostrar mensagem de erro no canvas
      const ctx = graficoCanvasEl.getContext('2d');
      ctx.clearRect(0, 0, graficoCanvasEl.width, graficoCanvasEl.height);
      ctx.fillStyle = '#dc3545';
      ctx.font = '16px system-ui';
      ctx.textAlign = 'center';
      ctx.fillText('Erro ao carregar gráfico', graficoCanvasEl.width / 2, graficoCanvasEl.height / 2);
      
      throw error;
    }
  }

  // Função principal para carregar todos os dados
  async function carregarDashboard() {
    try {
      // Carregar dados em paralelo para melhor performance
      await Promise.allSettled([
        carregarResumoVendas(),
        carregarProdutosMaisVendidos(),
        criarGraficoVendas()
      ]);
    } catch (error) {
      console.error("Erro ao carregar dashboard:", error);
    }
  }

  // Função para iniciar atualização automática
  function iniciarAtualizacaoAutomatica() {
    // Atualizar a cada 5 minutos (300000 ms)
    updateInterval = setInterval(() => {
      console.log("Atualizando dados do dashboard automaticamente...");
      carregarDashboard();
    }, 300000);
  }

  // Função para parar atualização automática
  function pararAtualizacaoAutomatica() {
    if (updateInterval) {
      clearInterval(updateInterval);
      updateInterval = null;
    }
  }

  // Função de limpeza ao sair da página
  function cleanup() {
    pararAtualizacaoAutomatica();
    if (vendasChart) {
      vendasChart.destroy();
      vendasChart = null;
    }
  }

  // Event listeners
  window.addEventListener('beforeunload', cleanup);
  window.addEventListener('unload', cleanup);

  // Detectar quando a página fica visível/invisível para pausar/retomar atualizações
  document.addEventListener('visibilitychange', function() {
    if (document.hidden) {
      pararAtualizacaoAutomatica();
    } else {
      iniciarAtualizacaoAutomatica();
      // Atualizar dados quando a página volta a ficar visível
      carregarDashboard();
    }
  });

  // Inicialização
  console.log("Inicializando dashboard da página inicial...");
  carregarDashboard().then(() => {
    iniciarAtualizacaoAutomatica();
    console.log("Dashboard inicializado com sucesso. Atualização automática ativada.");
  }).catch(error => {
    console.error("Erro na inicialização do dashboard:", error);
  });

})();