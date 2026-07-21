# Sistema de Coleta de Lixo Urbano

Sistema de apoio à coleta urbana de lixo que modela a cidade como um **grafo dirigido e ponderado** e resolve, sobre esse modelo, os principais problemas de roteamento: menor caminho, conectividade, malha econômica de vias e cobertura de todas as ruas. Conta com uma interface gráfica (Java Swing) que integra todos os módulos e permite simular interdições de vias em tempo real.

Trabalho acadêmico da disciplina de Estrutura de Dados (2026.1), desenvolvido por Anna Clara Muniz Braga, Eloiza Santos da Silva, Luis Gustavo Morais Pereira e Matheus Barreto Suzart Vieira.

## Sumário

- [Visão geral](#visão-geral)
- [Modelagem do grafo](#modelagem-do-grafo)
- [Funcionalidades](#funcionalidades)
- [Arquitetura e módulos](#arquitetura-e-módulos)
- [Como executar](#como-executar)
- [Cenários e formato de arquivo](#cenários-e-formato-de-arquivo)
- [Decisões de projeto](#decisões-de-projeto)
- [Limitações conhecidas](#limitações-conhecidas)
- [Trabalhos futuros](#trabalhos-futuros)

## Visão geral

O caminhão de coleta parte de um depósito, percorre os pontos da cidade e leva a carga a um aterro. Cada rua tem uma distância e um volume de lixo associado; o peso de cada via é calculado como `distância / volume de lixo`, de modo que o sistema pondere tanto o percurso quanto a demanda de coleta ao escolher rotas.

O sistema foi escrito em **Java**, com uma estrutura de grafo própria (lista de adjacência) e interface em **Java Swing**, sem bibliotecas externas.

## Modelagem do grafo

A cidade é representada por um **grafo dirigido**: cada aresta existe em um sentido. Ruas de mão dupla são modeladas como um par de arestas opostas; ruas de mão única, como uma única aresta. Isso permite representar trânsito assimétrico — ir e voltar entre dois pontos pode ter custos diferentes.

- **Vértice:** ponto de coleta (Depósito, Centro, Praça, Aterro, etc.).
- **Aresta:** rua, com nome, ponto de início, ponto de fim, distância (km) e volume de lixo (t).
- **Peso:** `distância / volumeLixo` — priorizar ruas com mais lixo a coletar por quilômetro percorrido.

## Funcionalidades

- **Menor rota entre dois pontos** com cálculo de custo (Dijkstra).
- **Comparação entre rota atual e rota otimizada**, com o percentual de economia.
- **Verificação de alcance** — quais pontos o caminhão consegue atender a partir do depósito (BFS). Pontos inalcançáveis são destacados no mapa.
- **Malha econômica de vias prioritárias** — árvore geradora mínima (Kruskal), com custo e quilometragem.
- **Rota que cobre todas as ruas** e retorna ao ponto de partida (Carteiro Chinês), com o circuito desenhado no mapa em ordem de percurso.
- **Simulação em tempo real** — interditar, liberar e alterar (peso, distância ou volume) vias, com recálculo imediato das rotas.

## Arquitetura e módulos

O trabalho foi dividido entre cinco integrantes, cada um responsável por um conjunto de classes.

| Módulo | Classes principais | Responsabilidade |
|---|---|---|
| Grafo e carga de dados | `Grafo`, `Vertice`, `Aresta`, `CarregadorCenario`, `Main` | Estrutura do grafo, cenários e ponto de entrada |
| Menor caminho | `Dijkstra`, `ResultadoDijkstra` | Rota de menor custo entre dois pontos |
| Conectividade e malha | `ConectividadeService`, `MalhaEconomica` | Alcance (BFS) e árvore geradora mínima (Kruskal) |
| Cobertura de ruas | `CarteiroChines`, `MontarCaminho`, `Pilha` | Circuito que passa por todas as ruas |
| Simulação e interface | `SimuladorVias`, `JanelaPrincipal`, `PainelGrafo`, `Menu`, `Estilo` | Interdição de vias e interface gráfica integradora |

Estruturas de apoio: `ListaSimplesmenteEncadeada`, `Elemento`, `Pilha`.

### Interface

A `JanelaPrincipal` desenha o mapa (`PainelGrafo`) e reúne, num painel lateral, as ações de todos os módulos, além de um diário de operação que registra cada ação executada. A rota calculada aparece destacada; vias interditadas são marcadas com listras de barreira; pontos sem coleta ficam sinalizados em vermelho. Há também uma interface de console alternativa (`Menu`).

## Como executar

Requer **JDK 21** (ou compatível).

Os arquivos `.java` declaram o pacote `com.mycompany.sistemacoletaurbana`. Compile e execute a partir da raiz do código-fonte:

```bash
javac -encoding UTF-8 -d build com/mycompany/sistemacoletaurbana/*.java
java -cp build com.mycompany.sistemacoletaurbana.Main
```

### Modos de execução

| Comando | Efeito |
|---|---|
| `Main` | Abre a interface gráfica com o cenário padrão |
| `Main arquivo.csv` | Abre a interface com um cenário carregado de arquivo |
| `Main --console` | Usa a interface de console (menu de texto) |
| `Main --exemplo saida.csv` | Gera um arquivo CSV de exemplo e encerra |

### Abrindo no NetBeans

O projeto usa a raiz do código como pasta de fontes. Para executar corretamente:

1. Em **Properties → Run → Main Class**, defina `com.mycompany.sistemacoletaurbana.Main`.
2. Use **Clean & Build** (F11) seguido de **Run Project** (F6).

> Evite "Run File" (Shift+F6) nas classes: como os arquivos ficam na raiz mas declaram um pacote, esse comando tenta executá-los sem o pacote e falha. Use sempre Run Project.

## Cenários e formato de arquivo

O sistema traz um cenário embutido para demonstração e também lê cenários de um arquivo CSV. Gere um arquivo de exemplo com `Main --exemplo exemplo.csv` para ver o formato, que é:

```
# Comentários começam com #
V;NomeDoPonto
A;NomeDaRua;Inicio;Fim;distancia;volumeLixo;maoDupla(s/n)
```

- Linhas `V` declaram pontos; devem vir antes das ruas que os utilizam.
- Linhas `A` declaram ruas. O último campo indica mão dupla (`s`) ou mão única (`n`).
- O carregador valida cada linha e informa o número da linha em caso de erro (ponto duplicado, ponto não declarado, número inválido, etc.).

## Decisões de projeto

**Grafo dirigido.** Optou-se por um grafo dirigido para representar mão única de verdade. Isso torna o modelo mais realista (rotas de ida e volta com custos diferentes) e serviu de base para as decisões abaixo.

**Interdição por peso infinito.** Ao interditar uma via, o sistema não a remove do grafo: atribui a ela peso infinito e guarda o valor original. Assim, os algoritmos de rota deixam de escolhê-la automaticamente, sem qualquer alteração no código dos demais módulos, e a interdição é reversível.

**Cenário próprio para o Carteiro Chinês.** O Carteiro Chinês implementado é o clássico (grafo não-dirigido). Para que ele feche um circuito que passa por todas as ruas e volta ao ponto de partida, o mapa precisa estar *balanceado*. Por isso o módulo roda sobre uma variante do cenário com um **anel viário** de mão única (Centro → Praça → Escola → BairroNorte → Centro), em que cada ponto do anel mantém entrada e saída equilibradas. Os demais algoritmos rodam sobre o cenário de mão dupla.

**Verificação de conectividade antes do Carteiro.** Antes de executar o Carteiro Chinês, o sistema checa se o grafo é conexo (via `ConectividadeService`). Se algum ponto for inalcançável, ele não roda o algoritmo e avisa, já que não existe circuito que cubra todas as ruas em um grafo desconexo.

## Limitações conhecidas

- **Malha econômica em grafo com mão única.** O Kruskal é um algoritmo de grafo não-dirigido: sobre um mapa com mão única, a árvore que ele gera funciona como uma **seleção de vias prioritárias**, não como um trajeto percorrível (o caminhão pode não conseguir retornar usando apenas essas vias). Por isso a malha é apresentada como "vias a manter", não como rota.
- **BFS e interdições.** A verificação de alcance (BFS) percorre as arestas sem consultar o peso, então não enxerga interdições. A interface sinaliza essa divergência no diário quando ela ocorre.

## Trabalhos futuros

- Filtrar interdições no BFS e no Kruskal, para que respeitem vias bloqueadas.
- Adotar **arborescência mínima** (algoritmo de Chu-Liu/Edmonds) para gerar uma malha percorrível também em grafo dirigido.
- Evoluções na simulação: interdição com prazo (obra que reabre sozinha), agrupamento de várias interdições num único evento, desfazer/refazer e salvamento do estado da simulação.
