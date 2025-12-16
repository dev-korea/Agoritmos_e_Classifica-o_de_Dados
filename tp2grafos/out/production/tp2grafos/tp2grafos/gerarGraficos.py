import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

# 1. Carregar os dados
df = pd.read_csv('resultados_tp2.csv')

# 2. Converter unidades para facilitar leitura
df['tempo_ms'] = df['tempo_ns'] / 1e6  # Converte nanosegundos para milissegundos
df['memoria_MB'] = df['memoria_bytes'] / (1024 * 1024) # Converte bytes para MB

# Configuração visual
sns.set(style="whitegrid")

# Função auxiliar para gerar e salvar o gráfico
def gerar_boxplot(algoritmo, nome_arquivo):
    plt.figure(figsize=(12, 6))
    
    # Filtrar dados do algoritmo
    dados = df[df['algoritmo'] == algoritmo]
    
    # Plotar
    sns.boxplot(x='tipo', y='tempo_ms', hue='representacao', data=dados, palette="Set2")
    
    plt.title(f'Desempenho: {algoritmo} (Lista vs Matriz)')
    plt.ylabel('Tempo (ms)')
    plt.xlabel('Tipo de Grafo (Densidade)')
    plt.savefig(nome_arquivo)
    plt.show()

# Gerar os gráficos solicitados
gerar_boxplot('Dijkstra', 'boxplot_dijkstra.png')
gerar_boxplot('Bellman-Ford', 'boxplot_bellman.png')

# Gráfico de Memória
plt.figure(figsize=(10, 6))
sns.boxplot(x='tipo', y='memoria_MB', hue='representacao', data=df, palette="Pastel1")
plt.title('Consumo de Memória (Lista vs Matriz)')
plt.ylabel('Memória (MB)')
plt.xlabel('Tipo de Grafo')
plt.savefig('boxplot_memoria.png')
plt.show()