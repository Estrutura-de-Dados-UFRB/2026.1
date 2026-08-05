# SAMU - Mapeamento de Rotas de Ambulancias

Sistema academico em Java que simula o despacho de ambulancias do SAMU usando teoria de grafos. A cidade e representada como um grafo dirigido e ponderado: hospitais, bases do SAMU, bairros, cruzamentos e pacientes sao vertices; ruas e avenidas sao arestas com peso em minutos.

O projeto esta completo e possui backend de grafo, algoritmos de busca, regras de negocio, interface grafica em Swing e documentacao academica em `docs/`.

## Objetivo

O sistema busca apoiar uma simulacao de atendimento emergencial:

1. Carregar uma cidade ficticia com hospitais, bases, bairros, cruzamentos, vias e ambulancias.
2. Registrar uma ocorrencia em um ponto da cidade.
3. Encontrar a ambulancia disponivel mais proxima.
4. Calcular a rota ate o paciente.
5. Selecionar o hospital disponivel mais proximo.
6. Exibir a rota no mapa e animar o deslocamento da ambulancia.
7. Permitir alteracoes de trafego e capacidade hospitalar durante a simulacao.

## Tecnologias

- Java 11
- Swing para interface grafica
- Ant/NetBeans como estrutura de projeto
- Estrutura de pacotes separando dominio/algoritmos (`grafo`) e telas (`telas`)

## Como Executar

### Pelo terminal

No PowerShell, a partir da raiz do projeto:

```powershell
javac -encoding UTF-8 -d build\classes (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp build\classes grafo.Main
```

### Pelo NetBeans

Abra o projeto no NetBeans e execute a classe `grafo.Main`.

Observacao: o arquivo `nbproject/project.properties` pode estar apontando `main.class=ambulancia.Main`. Caso use o botao "Run Project", ajuste para:

```properties
main.class=grafo.Main
```

## Como Usar a Simulacao

Ao iniciar a aplicacao, a tela principal mostra um mapa vazio. Clique em `Iniciar` para carregar os dados demonstrativos.

Depois disso, e possivel:

- Criar uma nova ocorrencia pelo botao `Ocorrencia`.
- Selecionar o local da ocorrencia, nivel de urgencia e descricao.
- Visualizar a previsao de chegada da ambulancia antes do despacho.
- Despachar a ambulancia e acompanhar a rota destacada no mapa.
- Cadastrar novos hospitais.
- Cadastrar novas vias entre pontos existentes.
- Clicar em uma via para alterar seu status: livre, congestionada ou bloqueada.
- Clicar em um hospital para alterar capacidade, ocupacao ou ver detalhes.
- Usar zoom e arrastar o mapa para navegar pela malha viaria.

## Modelagem do Grafo

O nucleo da aplicacao fica no pacote `grafo`.

| Classe | Responsabilidade |
|---|---|
| `GrafoCidade` | Armazena vertices, arestas e listas de adjacencia. |
| `Vertice` | Representa um ponto da cidade com id, nome, tipo, latitude e longitude. |
| `Aresta` | Representa uma via entre dois vertices, com peso, status e nome. |
| `Hospital` | Vertice especializado com capacidade maxima e ocupacao atual. |
| `Paciente` | Vertice especializado com nivel de urgencia e descricao da ocorrencia. |
| `Ambulancia` | Guarda localizacao atual, status e posicao visual para animacao. |
| `SistemaEmergencia` | Orquestra cadastros, ocorrencias, despacho, rotas e cobertura. |

Tipos de vertices:

- `HOSPITAL`
- `BASE_SAMU`
- `BAIRRO`
- `CRUZAMENTO`
- `PACIENTE`

Status das vias:

- `LIVRE`: peso normal.
- `CONGESTIONADA`: peso efetivo multiplicado por 1.5.
- `BLOQUEADA`: peso efetivo infinito, impedindo passagem pelos algoritmos.

## Algoritmos Implementados

| Algoritmo | Classe | Uso no sistema |
|---|---|---|
| Dijkstra | `Dijkstra` | Calcula o menor caminho por tempo entre ambulancia, paciente e outros destinos. |
| A* | `AEstrela` | Seleciona o hospital disponivel mais proximo do paciente usando heuristica por distancia. |
| BFS | `BFS` | Encontra caminho com menor numero de arestas, util para analise estrutural. |
| Componentes Conexos | `ComponentesConexos` | Detecta regioes conectadas e componentes sem hospital disponivel. |

Os algoritmos usam a lista de adjacencia do `GrafoCidade`, evitando percorrer todas as arestas a cada passo. Isso deixa as buscas mais adequadas para redes urbanas maiores.

## Fluxo de Atendimento

O fluxo principal fica em `SistemaEmergencia.registrarOcorrencia`.

```text
registrarOcorrencia(paciente)
  adiciona o paciente ao grafo
  conecta o paciente ao vertice mais proximo
  localiza a ambulancia disponivel mais proxima com Dijkstra
  calcula a rota da ambulancia ate o paciente
  seleciona o hospital disponivel mais proximo com A*
  retorna AtendimentoResultado
```

Na interface, `TelaAtendimento` usa esse resultado para:

- mostrar mensagens de erro quando nao ha rota, ambulancia ou hospital;
- destacar a rota completa no mapa;
- animar a ambulancia ate o paciente e depois ate o hospital;
- atualizar a ocupacao do hospital;
- retornar a ambulancia para a base ao fim do atendimento.

## Interface Grafica

O pacote `telas` contem a interface Swing.

| Classe | Funcao |
|---|---|
| `TelaPrincipal` | Janela principal, botoes de acao, log da simulacao e integracao com o mapa. |
| `PainelMapa` | Desenha a malha viaria, hospitais, bases, ambulancias, pacientes, legenda, zoom e pan. |
| `TelaAtendimento` | Dialogo para registrar ocorrencias e despachar ambulancias. |
| `TelaCadastroHospital` | Cadastro de novos hospitais. |
| `TelaCadastroVia` | Cadastro de novas ruas ou avenidas entre vertices. |

O mapa representa visualmente:

- vias livres, congestionadas e bloqueadas;
- hospitais e bases SAMU;
- ambulancias disponiveis ou em atendimento;
- paciente no local da ocorrencia;
- rota atual em destaque.

## Dados de Demonstracao

A classe `SeedDados` popula automaticamente a simulacao quando o usuario clica em `Iniciar`.

Ela cria:

- 3 hospitais;
- 4 bases SAMU;
- 8 bairros;
- 6 cruzamentos;
- vias bidirecionais entre os pontos;
- 4 ambulancias posicionadas nas bases.

Esses dados permitem testar o sistema sem cadastro manual inicial.

## Regras de Negocio

- Uma ambulancia so atende uma ocorrencia por vez.
- Hospitais lotados sao ignorados na escolha do destino.
- Vias bloqueadas nao podem ser usadas nas rotas.
- Vias congestionadas aumentam o tempo de deslocamento.
- Toda nova ocorrencia dispara uma analise completa de rota.
- Pacientes sao conectados automaticamente ao ponto mais proximo da malha.

## Estrutura do Projeto

```text
.
├── src/
│   ├── grafo/      # Dominio, grafo, algoritmos e regras de negocio
│   └── telas/      # Interface grafica Swing
├── docs/
│   ├── artigo/     # Artigo em LaTeX/PDF
│   ├── slides/     # Material de apresentacao
│   └── *.pdf       # Documentos de apoio
├── nbproject/      # Configuracao do NetBeans/Ant
├── build.xml       # Build Ant
└── README.md
```

## Principais Arquivos

- `src/grafo/Main.java`: ponto de entrada da aplicacao.
- `src/grafo/SistemaEmergencia.java`: servico central da simulacao.
- `src/grafo/GrafoCidade.java`: estrutura do grafo.
- `src/grafo/Dijkstra.java`: menor caminho por custo.
- `src/grafo/AEstrela.java`: busca A* para hospital mais proximo.
- `src/grafo/SeedDados.java`: dados iniciais da cidade.
- `src/telas/TelaPrincipal.java`: janela principal.
- `src/telas/PainelMapa.java`: renderizacao e interacao com o mapa.

## Documentacao Academica

A pasta `docs/` contem materiais complementares do trabalho:

- artigo em LaTeX e PDF em `docs/artigo/`;
- arquivos de slides em `docs/slides/`;
- documentos de requisitos e apoio.

## Observacoes

O projeto usa textos em portugues e caracteres acentuados. Ao compilar pelo terminal, use `-encoding UTF-8` para evitar problemas de exibicao.

Em Windows, se os acentos aparecerem corrompidos no console, execute antes:

```powershell
chcp 65001
```
