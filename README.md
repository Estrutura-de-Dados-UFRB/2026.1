# Planejamento de Rotas para Transporte Estudantil — Cruz das Almas/BA

Aplicação desktop em Java Swing que modela pontos de Cruz das Almas/BA como um grafo, calcula rotas com algoritmos clássicos de Estrutura de Dados e simula o deslocamento de um veículo no mapa.

O projeto foi desenvolvido como trabalho final da disciplina de Estrutura de Dados. Não utiliza Maven, Gradle ou Node.js: basta o JDK e as bibliotecas JAR já incluídas em [`lib/`](lib/).

![Tela principal do Planejamento de Rotas para Transporte Estudantil](docs/prints/tela-principal-mapa.png)

## Sumário

- [Visão geral](#visão-geral)
- [Início rápido](#início-rápido)
- [Como usar](#como-usar)
- [Algoritmos](#algoritmos)
- [Mapas, rotas e modo offline](#mapas-rotas-e-modo-offline)
- [Base de dados](#base-de-dados)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Testes](#testes)
- [Documentação](#documentação)
- [Solução de problemas](#solução-de-problemas)
- [Autores](#autores)

## Visão geral

O sistema reúne em uma única interface:

- mapa interativo do OpenStreetMap, com zoom, movimentação e filtros por tipo de ponto;
- cadastro, pesquisa, edição e remoção de escolas, universidades, bairros e pontos de embarque;
- cálculo com Dijkstra, Prim, Kruskal, BFS, DFS, Guloso e TSP simplificado;
- seleção de origem, destino, ponto inicial e subconjunto de paradas;
- desenho da rota pelas ruas, com enquadramento automático do percurso;
- estatísticas de distância, duração, alunos, paradas e combustível;
- simulação animada do veículo, com velocidade configurável, pausa e reinício;
- abertura e salvamento de projetos;
- exportação dos resultados em CSV, TXT e PDF;
- fallback local quando o serviço de rotas viárias estiver indisponível.

### Rota calculada

![Rota calculada com o TSP simplificado](docs/prints/tela-rota-tsp.png)

O painel lateral redimensionável apresenta as estatísticas e a ordem de visita. A rota desenhada e a animação usam a mesma sequência de coordenadas para manter o veículo alinhado ao percurso.

## Início rápido

### Requisitos

- JDK 21 ou superior;
- ambiente gráfico para executar a interface Swing;
- internet para carregar o mapa e obter trajetos viários do OSRM.

Confira a instalação do Java:

```bash
java -version
javac -version
```

### Linux

Na raiz do projeto, execute:

```bash
mkdir -p bin
javac --release 21 -encoding UTF-8 -cp "lib/*" -d bin $(find src -name "*.java")
java -cp "bin:lib/*" Main
```

### Windows PowerShell

```powershell
$sources = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object FullName
New-Item -ItemType Directory -Force -Path bin | Out-Null
javac --release 21 -encoding UTF-8 -cp "lib/*" -d bin $sources
java -cp "bin;lib/*" Main
```

### Visual Studio Code

1. Instale o **Extension Pack for Java**.
2. Abra a pasta raiz do projeto.
3. Pressione `Ctrl+Shift+B` para compilar em `bin/`.
4. Abra `src/Main.java`, pressione `F5` e escolha **Executar Main**.

As configurações de compilação e execução estão em [`.vscode/tasks.json`](.vscode/tasks.json) e [`.vscode/launch.json`](.vscode/launch.json).

## Como usar

1. Inicie a aplicação.
2. Escolha um algoritmo na barra superior.
3. Clique em **Calcular**.
4. Selecione os pontos e, quando solicitado, a origem, o destino ou o ponto inicial.
5. Consulte o percurso, as estatísticas e o resumo no painel lateral.
6. Ajuste a velocidade e use **Iniciar**, **Pausar**, **Parar** ou **Limpar** para controlar a simulação.

### Marcadores do mapa

| Marcador | Significado |
|:---:|---|
| E | Escola |
| U | Universidade |
| B | Bairro |
| P | Ponto de embarque |
| S | Início da rota |
| D | Destino |

## Algoritmos

| Algoritmo | Resultado | Uso no sistema |
|---|---|---|
| Dijkstra | Menor caminho entre dois pontos | Rota entre origem e destino |
| Prim | Árvore geradora mínima a partir de uma origem | Conexão de todos os pontos selecionados |
| Kruskal | Árvore geradora mínima pela ordenação das arestas | Conexão global de menor peso |
| BFS | Percurso em largura | Visita por níveis de proximidade no grafo |
| DFS | Percurso em profundidade | Exploração completa de cada ramo |
| Guloso | Rota aproximada | Priorização de distância, demanda e prioridade |
| TSP simplificado | Ordem heurística de visitas | Percurso curto por várias paradas |

A classe `VRP` também está implementada na camada de algoritmos para distribuir pontos entre múltiplos veículos, mas ainda não está disponível na barra principal da interface.

Quando o resultado de um algoritmo contém mudanças entre vértices não adjacentes, o sistema expande esses trechos em caminhos existentes no grafo. Isso mantém o desenho e a animação contínuos.

Para uma explicação mais detalhada, resultados medidos e capturas de cada algoritmo, consulte o [comparativo completo](docs/telas-e-comparativo-algoritmos.md).

## Mapas, rotas e modo offline

O mapa é exibido com JXMapViewer2 e tiles do OpenStreetMap. Depois do cálculo do algoritmo, a aplicação consulta o OSRM para obter:

- geometria do percurso pelas ruas;
- distância de cada trecho e distância total;
- duração estimada da viagem.

Se o OSRM não responder, o sistema usa uma geometria local e calcula as distâncias com a fórmula de Haversine. Os algoritmos, o desenho simplificado e a simulação continuam funcionando; apenas o fundo cartográfico depende dos tiles do OpenStreetMap.

A animação usa um `javax.swing.Timer` atualizado aproximadamente a cada 16 ms. A posição considera o tempo transcorrido e a velocidade selecionada, e a orientação acompanha a direção do segmento atual.

## Base de dados

O cadastro inicial é montado pelo `DadosIniciaisService`, que usa [`dados/vertices.csv`](dados/vertices.csv) para atualizar os registros correspondentes. As conexões do grafo são geradas em tempo de execução a partir da distância geográfica e de ligações-base; [`dados/edges.csv`](dados/edges.csv) permanece no repositório como conjunto de referência. As coordenadas usam o sistema WGS84, e o mapa inicia centralizado aproximadamente em `-12.6723, -39.1054`.

| Informação | Quantidade |
|---|---:|
| Pontos cadastrados | 36 |
| Escolas | 18 |
| Universidades | 1 |
| Bairros | 14 |
| Pontos de embarque | 3 |
| Arestas | 349 |
| Alunos | 1.500 |

## Tecnologias

- Java 21 e Java Swing;
- JXMapViewer2 2.8;
- OpenStreetMap e OSRM;
- arquivos CSV para os dados iniciais;
- Nimbus Look and Feel e componentes visuais próprios.

As dependências são carregadas localmente:

| Biblioteca | Finalidade |
|---|---|
| `jxmapviewer2-2.8.jar` | Exibição e navegação do mapa |
| `commons-logging-1.3.0.jar` | Logging usado pelo visualizador |

## Estrutura do projeto

```text
.
├── src/
│   ├── Main.java
│   ├── algoritmos/          # Dijkstra, Prim, Kruskal, BFS, DFS, Guloso, TSP e VRP
│   ├── gui/                 # Janelas, controladores, mapa e animação
│   │   └── components/      # Componentes Swing reutilizáveis
│   ├── model/               # Grafo, pontos, arestas, rotas e veículos
│   ├── services/            # Dados, projetos, simulação e integração OSRM
│   └── util/                # Geometria, distâncias e exportadores
├── dados/                   # Base inicial em CSV
├── lib/                     # Dependências JAR locais
├── tests/                   # Testes executáveis sem framework externo
├── docs/                    # Relatório visual e capturas da interface
├── .vscode/                 # Configuração de build e execução
└── README.md
```

## Testes

Compile primeiro a aplicação conforme a seção [Início rápido](#início-rápido). Em seguida, compile e execute os testes.

### Linux

```bash
javac --release 21 -encoding UTF-8 -cp "bin:lib/*" -d bin $(find tests -name "*.java")
java -ea -cp "bin:lib/*" RouteSmokeTest
```

Teste opcional com acesso à internet:

```bash
java -ea -cp "bin:lib/*" OsrmIntegrationTest
```

### Windows PowerShell

```powershell
$tests = Get-ChildItem -Path tests -Filter *.java | ForEach-Object FullName
javac --release 21 -encoding UTF-8 -cp "bin;lib/*" -d bin $tests
java -ea -cp "bin;lib/*" RouteSmokeTest
java -ea -cp "bin;lib/*" OsrmIntegrationTest
```

- `RouteSmokeTest` valida a base padrão, as coordenadas, os algoritmos e a continuidade das rotas. Não requer internet.
- `OsrmIntegrationTest` consulta uma resposta real do OSRM e, portanto, requer internet.

## Documentação

- [Telas do sistema e comparativo dos algoritmos](docs/telas-e-comparativo-algoritmos.md)
- [Capturas da interface](docs/prints/)

A documentação complementar apresenta os fluxos de cadastro, seleção, exportação e simulação, além dos resultados visuais e do quadro comparativo dos sete algoritmos disponíveis na interface.

## Solução de problemas

### `package org.jxmapviewer... does not exist`

Confirme que estes arquivos existem:

```text
lib/jxmapviewer2-2.8.jar
lib/commons-logging-1.3.0.jar
```

No VS Code, execute `Java: Clean Java Language Server Workspace`, escolha **Restart and delete** e aguarde a reimportação do projeto.

### O mapa não aparece

Verifique a conexão com a internet. Os cálculos continuam disponíveis, mas os tiles do OpenStreetMap não são exibidos offline.

### A rota usa uma aproximação local

O OSRM não respondeu dentro do tempo configurado. Verifique a conexão e calcule novamente. O fallback local continua disponível para visualização e simulação.

### A aplicação informa que precisa de um ambiente gráfico

O Java foi iniciado em um terminal sem servidor gráfico, como uma sessão SSH ou um contêiner headless. Execute o programa em um ambiente desktop com suporte a janelas Swing.

### Os caracteres acentuados aparecem incorretamente

Compile com `-encoding UTF-8`, como nos comandos deste README.

## Autores

- Benjamin da Conceição Neves Cardoso
- Caio Conceição dos Santos
- Daniel Bezerra Medeiros de Souza
- Daniel José Cerqueira Brito
- Jessé dos Santos Nery
- João Pedro Carneiro da Silva
