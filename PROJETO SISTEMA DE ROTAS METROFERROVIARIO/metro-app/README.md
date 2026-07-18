# Sistema de Rotas Inteligentes para Estações de Metrô

## Visão Geral
Sistema em Java que modela a rede de metrô de São Paulo como grafo e responde a
consultas de rota usando algoritmos clássicos. Estruturas e algoritmos são
implementados **do zero**, sem bibliotecas externas de grafos.

### Objetivo Acadêmico
- Implementar algoritmos de grafos (Dijkstra, BFS, DFS, Floyd-Warshall,
  aproximação de centralidade de intermediação) manualmente
- Modelar a rede de metrô/CPTM de São Paulo com dados de referência (14 linhas)
- Oferecer uma interface gráfica (Swing) para demonstração interativa

---

## Estrutura do Projeto
```
src/
├── model/               # Modelos de dados
│   ├── Estacao.java     # Representação das estações
│   └── Conexao.java     # Conexões entre estações
├── grafo/               # Estrutura do grafo
│   ├── RedeMetro.java    # Estrutura principal do grafo (lista de adjacência)
│   └── ResultadoRota.java # Resultado das consultas de rota
├── algoritmos/          # Implementações dos algoritmos
│   ├── Dijkstra.java
│   ├── BFS.java
│   ├── DFS.java
│   ├── FloydWarshall.java
│   └── Betweenness.java  # Aproximação de centralidade de intermediação
├── dados/                # Dados da rede
│   └── DadosMetroSP.java
├── enums/
│   └── Linha.java
├── Main.java             # Execução simples via linha de comando
└── MapaMetroSPApp.java   # Interface gráfica Swing
```

---

## Modelagem do Domínio

### Entidades
- **Estação**: `id`, `nome`, `linha`, status `ativa`. O `id` segue o formato
  `LINHA:Nome_Da_Estação` (ex.: `L1_AZUL:Tucuruvi`). Estações de baldeação
  existem como **vértices distintos por linha** — o mesmo local físico
  aparece mais de uma vez no grafo, conectado por arestas de integração.
- **Conexão**: liga duas estações com `peso` (tempo em minutos) e flag
  `bidirecional` (não usada atualmente para direcionar a lista de
  adjacência — ver limitação abaixo).
- **RedeMetro**: grafo com lista de adjacência (`Map<String, List<Conexao>>`),
  suporte a interdição/reativação de estações.

### Escolhas Técnicas
- **Lista de adjacência** em vez de matriz, por eficiência em grafo esparso.
- **Vértice por par (linha, estação)** para representar baldeações sem
  perder a informação de qual linha está sendo percorrida.
- **Swing** para a interface gráfica, por não exigir dependências externas
  (ao contrário do JavaFX, que desde o JDK 11 precisa de SDK separado).

---

## Algoritmos Implementados

| Algoritmo        | Função                                              | Complexidade    |
|-------------------|------------------------------------------------------|-----------------|
| Dijkstra          | Caminho de menor custo (tempo) entre duas estações    | O((V+E) log V)  |
| BFS               | Menor número de estações (arestas) entre A e B        | O(V + E)        |
| DFS               | Verificação de conectividade e detecção de ciclos     | O(V + E)        |
| Floyd-Warshall    | Menor caminho entre todos os pares de estações        | O(V³)           |
| Betweenness       | Aproximação de centralidade de intermediação, usada para "estações mais importantes" | O(V³) (via Floyd-Warshall) |

### Limitações conhecidas
- **`Dijkstra.calcularComBalcao(...)`** recebe um parâmetro `penalidadeBalcao`
  mas atualmente **ignora esse parâmetro** e apenas delega para
  `calcularCaminhoMinimo(...)`. Ainda não está decidido se será implementado
  de fato (penalizando trocas de linha no relaxamento) ou removido.
- **`Betweenness`** conta apenas **um** caminho mínimo por par de estações
  (o que `Floyd-Warshall` reconstrói via `prox[i][j]`), não distribuindo
  crédito fracionário entre caminhos empatados. Não é betweenness centrality
  formal — é uma aproximação de caminho único, documentada como tal no
  próprio código.
- Nenhuma métrica puramente estrutural (grau, betweenness) corresponde a
  ranking real de fluxo de passageiros, já que os pesos das arestas
  representam tempo de viagem, não volume de embarque.
- `RedeMetro.adicionarConexao` é direcionado por padrão; conexões
  bidirecionais são criadas chamando o método duas vezes (uma em cada
  sentido) em `DadosMetroSP`.

---

## Dados da Rede

Carregados em `dados/DadosMetroSP.java`:
- **14 linhas** (enum `Linha`: L1, L2, L3, L4, L5, L7, L8, L9, L10, L11, L12,
  L13, L15, L17)
- **214 vértices** estação-linha (contando cada estação de baldeação uma vez
  por linha em que opera)
- Pesos de trecho de 2 a 4 minutos, conforme a linha
- Baldeações com peso de 3 min (integração "gratuita") ou 5 min
  (integração "horária", ex.: Tatuapé/Corinthians-Itaquera entre L3 e L11)

---

## Interface Gráfica (`MapaMetroSPApp.java`)

Aplicação Swing com:
- Layout orientado a forças (Fruchterman-Reingold simplificado) para
  posicionar as estações — **não é geograficamente fiel** ao mapa real,
  é derivado apenas da topologia do grafo
- Clique esquerdo: seleciona origem e depois destino, calculando a rota via
  Dijkstra automaticamente
- Clique direito: interdita/reativa uma estação, recalculando a rota se
  havia uma seleção ativa
- Diálogo "Estações mais importantes": top 10 por betweenness aproximado
- Legenda de linhas e tooltip ao passar o mouse sobre uma estação

**Risco conhecido de uso**: com 214 vértices desenhados em um espaço
relativamente pequeno, a precisão de clique é o principal risco de usabilidade
em uma demonstração ao vivo — não a corretude dos algoritmos.

---

## Como Usar

### Requisitos
- **Java 15+** (o projeto usa `String.formatted()`, disponível a partir do
  Java 15)
- IDE com suporte a Java, ou `javac`/`java` diretamente

### Exemplo Básico
```java
// Carregar rede com dados reais
RedeMetro rede = DadosMetroSP.carregar();

// Consultar rota mais rápida (note o formato do id: LINHA:Nome_Da_Estação)
ResultadoRota rota = Dijkstra.calcularCaminhoMinimo(
    rede,
    "L1_AZUL:Tucuruvi",
    "L3_VERMELHA:Corinthians-Itaquera"
);
System.out.println(rota);

// Verificar conectividade da rede
boolean conectada = DFS.redeConectada(rede);
```

### Executando via linha de comando (`Main.java`)
```java
RedeMetro rede = DadosMetroSP.carregar();
System.out.println(rede);
System.out.println("Rede conectada? " + DFS.redeConectada(rede));

List<String> ida = BFS.caminhoMenorSaltos(rede, "L1_AZUL:Tucuruvi", "L1_AZUL:Jabaquara");
List<String> volta = BFS.caminhoMenorSaltos(rede, "L1_AZUL:Jabaquara", "L1_AZUL:Tucuruvi");
```

### Executando a interface gráfica
```
java MapaMetroSPApp
```

---

## Referências

- Estruturas de dados em grafos (Cormen et al. — CLRS)

> Nota: links para o site oficial do Metrô e um "mapa 2026" foram removidos
> desta versão do README por não terem sido verificados como fontes reais —
> evitar referências não confirmadas.

---