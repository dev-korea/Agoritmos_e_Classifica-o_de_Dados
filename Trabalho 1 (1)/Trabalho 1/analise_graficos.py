# analise_graficos.py
# Lê results/raw/*.csv (formato: algo,ordem,n,run,time_ns)
# Gera boxplots e tabela-resumo (média/mediana/desvio) por (algo, ordem, n).
# MODIFICADO para agrupar por Categoria (Linear vs Ordenador)

import glob
import os
from pathlib import Path
import pandas as pd
import matplotlib.pyplot as plt

DIR_RAW = Path("results/raw")
DIR_SUM = Path("results/summary")
DIR_FIG = Path("results/figs")
DIR_SUM.mkdir(parents=True, exist_ok=True)
DIR_FIG.mkdir(parents=True, exist_ok=True)

# <<< MODIFICAÇÃO 1: Definir categorias >>>
# Substitua os nomes nos exemplos abaixo pelos nomes EXATOS 
# que aparecem na coluna 'algo' dos seus CSVs.

ALGOS_ORDENADORES = [
    "bubblesort", 
    "mergesort", 
    "quicksort", 
    "selection_sort" 
    # Adicione outros algoritmos de ordenação aqui
]

ALGOS_LINEARES = [
    "busca_sequencial", 
    "busca_binaria" 
    # Adicione outros algoritmos lineares/busca aqui
]


# ---------- 1) Carregar todos os CSVs brutos ----------
arquivos = sorted(glob.glob(str(DIR_RAW / "*.csv")))
if not arquivos:
    raise SystemExit(f"Nenhum CSV encontrado em {DIR_RAW}/. Rode o benchmark primeiro.")

dfs = []
for arq in arquivos:
    df = pd.read_csv(arq)
    # normaliza nomes esperados
    df.columns = [c.strip().lower() for c in df.columns]
    # exige colunas básicas
    exigidas = {"algo","ordem","n","run","time_ns"}
    if not exigidas.issubset(set(df.columns)):
        raise ValueError(f"Arquivo {arq} não contém colunas {exigidas}")
    dfs.append(df)

dados = pd.concat(dfs, ignore_index=True)

# <<< MODIFICAÇÃO 2: Criar coluna 'categoria' >>>
def categorizar_algo(nome_algo):
    if nome_algo in ALGOS_ORDENADORES:
        return "Ordenador"
    if nome_algo in ALGOS_LINEARES:
        return "Linear"
    return "Outro" # Categoria para o que não foi classificado

dados["categoria"] = dados["algo"].apply(categorizar_algo)

# Opcional: Avisar sobre algoritmos não classificados
nao_classificados = set(dados.query("categoria == 'Outro'")["algo"].unique())
if nao_classificados:
    print(f"[AVISO] Algoritmos não classificados (categorizados como 'Outro'): {nao_classificados}")
    print("        Edite ALGOS_ORDENADORES ou ALGOS_LINEARES para incluí-los.")

# tempos em ms e s (fica mais legível)
dados["time_ms"] = dados["time_ns"] / 1e6
dados["time_s"]  = dados["time_ns"] / 1e9

# Ordenações de rótulos
ordens_validas = ["aleatorio", "crescente", "decrescente"]
dados["ordem"] = pd.Categorical(dados["ordem"], categories=ordens_validas, ordered=True)

# ---------- 2) Tabela-resumo (média/mediana/desvio) ----------

# <<< MODIFICAÇÃO 3: Adicionar 'categoria' ao group by da tabela >>>
agr = (dados.groupby(["categoria", "algo","ordem","n"], as_index=False)["time_ms"]
           .agg(media_ms="mean", mediana_ms="median", desvio_ms="std", minimo_ms="min", maximo_ms="max", execucoes="count"))

agr = agr.sort_values(["categoria", "ordem","n","algo"]).reset_index(drop=True)

# salva CSV e XLSX
csv_out  = DIR_SUM / "tabela_resumo.csv"
xlsx_out = DIR_SUM / "tabela_resumo.xlsx"
agr.to_csv(csv_out, index=False)
with pd.ExcelWriter(xlsx_out, engine="openpyxl") as xlw:
    agr.to_excel(xlw, sheet_name="resumo", index=False)

print(f"[OK] Tabela-resumo -> {csv_out}  e  {xlsx_out}")


# ---------- 3) Boxplots ----------

# <<< MODIFICAÇÃO 4: Loop principal agora itera por CATEGORIA >>>
# Itera primeiro pela categoria (Linear, Ordenador, Outro)
for categoria in sorted(dados["categoria"].unique()):
    
    # Filtra os dados para esta categoria
    dados_cat = dados[dados["categoria"] == categoria]
    if dados_cat.empty:
        continue

    print(f"\nGerando gráficos para Categoria: {categoria}...")

    # a) Para ALEATÓRIO: 1 boxplot por tamanho n
    ns_aleatorio = sorted(dados_cat.query("ordem == 'aleatorio'")["n"].unique())
    for n in ns_aleatorio:
        sub = dados_cat.query("ordem == 'aleatorio' and n == @n").copy()
        if sub.empty:
            continue
        # ordenar barras por mediana (opcional, fica bonito)
        ordem_algos = (sub.groupby("algo")["time_ms"].median().sort_values().index.tolist())
        sub["algo"] = pd.Categorical(sub["algo"], categories=ordem_algos, ordered=True)

        plt.figure(figsize=(9, 5))
        sub.boxplot(column="time_ms", by="algo")
        # Título atualizado para incluir categoria
        plt.title(f"Boxplot - Categoria: {categoria} - Aleatório (n={n})")
        plt.suptitle("")  # remove subtítulo padrão
        plt.xlabel("Algoritmo")
        plt.ylabel("Tempo (ms)")
        plt.xticks(rotation=30, ha="right")
        plt.tight_layout()
        # Nome do arquivo atualizado para incluir categoria
        nome = DIR_FIG / f"boxplot_{categoria}_aleatorio_n{n}.png"
        plt.savefig(nome, dpi=150)
        plt.close()
        print(f"[OK] {nome}")

    # b) Para CRESCENTE e DECRESCENTE (normalmente só n=100000)
    for ordem in ["crescente","decrescente"]:
        sub = dados_cat.query("ordem == @ordem").copy()
        if sub.empty:
            continue
        for n in sorted(sub["n"].unique()):
            subn = sub.query("n == @n").copy()
            if subn.empty:
                continue
                
            ordem_algos = (subn.groupby("algo")["time_ms"].median().sort_values().index.tolist())
            subn["algo"] = pd.Categorical(subn["algo"], categories=ordem_algos, ordered=True)

            plt.figure(figsize=(9, 5))
            subn.boxplot(column="time_ms", by="algo")
            # Título atualizado
            plt.title(f"Boxplot - Categoria: {categoria} - {ordem.capitalize()} (n={n})")
            plt.suptitle("")
            plt.xlabel("Algoritmo")
            plt.ylabel("Tempo (ms)")
            plt.xticks(rotation=30, ha="right")
            plt.tight_layout()
            # Nome do arquivo atualizado
            nome = DIR_FIG / f"boxplot_{categoria}_{ordem}_n{n}.png"
            plt.savefig(nome, dpi=150)
            plt.close()
            print(f"[OK] {nome}")

# (Opcional) Dispersão: tempo vs n para aleatório, por algoritmo
try:
    # <<< MODIFICAÇÃO 5: Loop de dispersão também por CATEGORIA >>>
    print("\nGerando gráficos de dispersão (opcional)...")
    # Itera por categoria primeiro
    for categoria in sorted(dados["categoria"].unique()):
        sub_cat = dados.query("ordem == 'aleatorio' and categoria == @categoria").copy()
        if sub_cat.empty:
            continue
            
        # Depois por algoritmo dentro daquela categoria
        for algo, df_a in sub_cat.groupby("algo"):
            plt.figure(figsize=(7,4))
            medias_por_n = df_a.groupby("n")["time_ms"].mean().reset_index()
            if medias_por_n.empty:
                plt.close()
                continue
                
            plt.scatter(medias_por_n["n"], medias_por_n["time_ms"])
            # Título atualizado
            plt.title(f"Dispersão (média) - {categoria}: {algo} - Aleatório")
            plt.xlabel("n")
            plt.ylabel("Tempo (ms)")
            if len(medias_por_n["n"].unique()) > 1: # Só aplica escala log se houver mais de 1 ponto
                plt.xscale("log")
            plt.tight_layout()
            # Nome do arquivo atualizado
            out = DIR_FIG / f"dispersao_{categoria}_{algo}_aleatorio.png"
            plt.savefig(out, dpi=150); plt.close()
            print(f"[OK] {out}")
except Exception as e:
    print("[WARN] Falhou ao gerar dispersões opcionais:", e)

print("\nConcluído. Veja results/figs/ e results/summary/")