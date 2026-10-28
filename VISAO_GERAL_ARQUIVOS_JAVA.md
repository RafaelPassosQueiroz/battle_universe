# Visão geral dos arquivos Java — MarvelUniverse

Este guia explica o papel dos 33 arquivos do package `com.example.projetomarvel` em linguagem de apresentação: o que cada arquivo representa, em qual parte do aplicativo participa e como se relaciona com o conjunto. A ordem é a mesma das seções 9.1 a 9.33 da documentação técnica completa.

As explicações descrevem a implementação existente. Não são uma proposta de reorganização. Componentes que permanecem no projeto sem utilização ativa identificada estão indicados dessa forma.

## Antes da lista: como enxergar o conjunto

O aplicativo tem três grandes pontos de execução: a tela principal, a arena de luta e a exploração do mundo. Cada um possui uma Activity que organiza sua experiência. As demais classes ajudam essas Activities a buscar dados, montar interfaces, controlar regras ou desenhar elementos.

Como muitas telas são construídas em Java, uma classe pode participar tanto da apresentação quanto do comportamento. Estar dentro do package não significa que o arquivo contenha somente regras: alguns arquivos são responsáveis principalmente pelo que aparece na tela.

```text
MainActivity: entrada, navegação e experiências das abas
    ├── Home: apresentação + API + cache + tradução
    ├── Jarvis e Quiz: interface + conteúdo local ou integração remota
    ├── História: campanha linear e criação do protagonista
    ├── Lutas → FightActivity: arena + regras + desenhos
    └── Visitar Mundo → VisitWorldActivity: exploração + narrativa + mapa

Desenhos, efeitos e fontes apoiam essas experiências.
```

## 1. MainActivity.java — o centro de coordenação do aplicativo

É a Activity principal e o ponto de entrada do usuário. Organiza a passagem pela abertura e a navegação entre Home, Jarvis, Quiz, História e Lutas. Também coordena detalhes de personagens, seleção de combatentes e pré-luta.

No projeto atual, ela reúne bastante responsabilidade: mantém dados de algumas experiências e constrói grande parte de suas interfaces em Java. Outras classes trabalham com ela para apresentar o catálogo, consultar serviços e organizar a história. Por isso, é o principal lugar para entender como as partes do aplicativo se conectam.

**Para explicar:** “A MainActivity é a central do aplicativo: organiza a navegação, coordena as abas e conecta a interface aos dados e às experiências disponíveis.”

## 2. HomeReference.java — a apresentação da página inicial

É responsável por montar a parte visual da Home: título, apresentação do catálogo, acesso à campanha, campo de busca e lista de personagens. Ela cria componentes Android em Java e os coloca na área de conteúdo preparada pela MainActivity.

Seu papel é apresentar e permitir a interação com o catálogo. A obtenção e o armazenamento dos personagens ficam com outros componentes. A HomeReference liga as ações visuais, como digitar uma busca ou abrir a história, aos controles da Activity principal.

**Para explicar:** “A HomeReference monta a página inicial e organiza a forma como o usuário encontra e acessa os personagens do catálogo.”

## 3. ComicVineClient.java — a ligação com o catálogo externo

É o componente que se comunica com a Comic Vine para obter personagens e informações adicionais. Ele transforma as respostas do serviço em dados que o aplicativo consegue utilizar, incluindo o tratamento da editora Marvel.

No conjunto, permite que a Home seja alimentada por um catálogo real. Ele não desenha cards nem decide a aparência das telas: entrega informações para que a MainActivity possa apresentá-las e armazená-las.

**Para explicar:** “O ComicVineClient busca e interpreta as informações dos personagens na Comic Vine para alimentar o catálogo e os detalhes.”

## 4. CatalogStore.java — a memória local do catálogo

Guarda os personagens recebidos em um banco SQLite no aparelho e permite recuperá-los posteriormente. Também armazena os textos de detalhes que foram obtidos para os personagens.

Sua função no aplicativo é reaproveitar dados já recebidos, reduzindo a necessidade de começar o catálogo do zero a cada abertura. Esse armazenamento é específico do catálogo; não representa um banco geral de progresso do Quiz ou da história.

**Para explicar:** “O CatalogStore mantém uma cópia local do catálogo para que o aplicativo possa reaproveitar os personagens já carregados.”

## 5. CharacterTranslator.java — o apoio à leitura em português

Cuida da tradução dos textos em inglês para português utilizando o ML Kit. A interface solicita a tradução e recebe o resultado sem precisar conhecer os detalhes desse serviço.

Ele ajuda a tornar os conteúdos da Comic Vine mais acessíveis. Quando a tradução não pode ser realizada, conserva o texto original disponível. Dessa forma, o acesso à informação não depende exclusivamente do sucesso da tradução.

**Para explicar:** “O CharacterTranslator traduz os textos do catálogo e preserva o conteúdo original quando a tradução falha.”

## 6. GeminiClient.java — a conexão opcional com geração de texto

Centraliza a comunicação remota com o Gemini utilizada pelos recursos de conversa e geração de perguntas. Ele recebe as instruções e o contexto fornecidos pelo aplicativo e devolve o texto retornado pelo serviço.

Representa a infraestrutura da integração, e não uma tela ou o controlador do Quiz. A decisão de quando utilizá-lo e como apresentar a resposta pertence aos componentes que o chamam. A existência dessa integração no código não comprova a disponibilidade atual do serviço externo.

**Para explicar:** “O GeminiClient é a ponte de comunicação com o Gemini, usada quando o aplicativo precisa solicitar conteúdo remoto para o Jarvis ou o Quiz.”

## 7. QuizGenerator.java — a preparação de novas perguntas

Define o pedido de geração de perguntas e confere se a resposta recebida tem o formato esperado para o Quiz: quantidade, organização por etapas, alternativas e indicação de resposta correta.

Seu papel é preparar conteúdo utilizável pelo desafio. O controle de pontuação, erros, Joias e conclusão permanece na MainActivity. A conferência realizada por essa classe verifica a estrutura das perguntas; não comprova a verdade de cada informação gerada.

**Para explicar:** “O QuizGenerator prepara e organiza perguntas geradas remotamente para que possam ser utilizadas pelo Quiz.”

## 8. ScreenFragment.java — o espaço reutilizável das telas principais

É o Fragment utilizado como superfície para apresentar os destinos da navegação principal. Ele carrega um XML básico com um espaço onde os componentes da tela serão inseridos.

No modelo atual, as abas não possuem cada uma seu próprio Fragment com toda a interface e lógica. O ScreenFragment oferece a estrutura comum, enquanto a MainActivity e seus auxiliares montam o conteúdo correspondente à tela selecionada.

**Para explicar:** “O ScreenFragment fornece o espaço visual reutilizado pelas telas principais; o conteúdo de cada uma é montado pelos controladores do aplicativo.”

## 9. StoryMode.java — o organizador da campanha linear

Conduz a experiência de história apresentada na aba História. Organiza a escolha ou criação do protagonista, as etapas de personalização, os diálogos, as decisões e a preparação para as três batalhas da campanha.

Ele mantém a sequência narrativa e define o que o usuário deve ver em cada momento. Quando chega a hora de lutar, a campanha utiliza a arena compartilhada do aplicativo. É uma experiência distinta da exploração livre apresentada em Visitar Mundo.

**Para explicar:** “O StoryMode organiza a campanha linear, desde a definição do protagonista até as escolhas e os confrontos que fazem a história avançar.”

## 10. BattleHubReference.java — a apresentação do menu de lutas

Monta a interface inicial da área de Lutas, incluindo a escolha de dificuldade e a apresentação das opções e orientações dessa experiência. Trabalha com a MainActivity para encaminhar o usuário à seleção dos combatentes.

No conjunto, representa a preparação e a entrada para o combate livre. A escolha detalhada de jogador e adversário continua sendo coordenada pela MainActivity; a batalha efetiva acontece em outra Activity.

**Para explicar:** “A BattleHubReference organiza o menu de entrada das lutas, onde o usuário se prepara e escolhe a dificuldade antes de selecionar os personagens.”

## 11. FightActivity.java — a tela que coloca a luta em funcionamento

É a Activity da arena de combate em orientação horizontal. Recebe os participantes, organiza cenário, barras de vida e controles, acompanha a atualização da luta e apresenta o resultado.

Ela conecta a ação do usuário às regras do BattleEngine e transforma o estado da batalha em apresentação visual. A mesma arena é aproveitada por lutas livres e por confrontos iniciados pelas experiências narrativas.

**Para explicar:** “A FightActivity coordena a tela de combate: recebe os comandos, apresenta a arena e acompanha a luta até o resultado.”

## 12. BattleEngine.java — as regras e a simulação da batalha

É o núcleo lógico do combate. Representa os lutadores e controla vida, movimento, ataques, defesa, projéteis, habilidades, efeitos temporários e decisões do adversário controlado pelo aplicativo.

Seu papel é determinar o que acontece na luta. A FightActivity apresenta o resultado dessas decisões, mas o motor não precisa conhecer botões ou layouts XML. Essa separação permite estudar as regras de combate independentemente do desenho da tela.

**Para explicar:** “O BattleEngine é o motor da luta: calcula o que acontece com os personagens e aplica as regras de movimento, ataques, defesa e vida.”

## 13. FighterArtView.java — o desenho e as poses dos lutadores

Desenha os personagens por meio de formas gráficas, com diferenças de aparência, roupas, acessórios e poses. É utilizado em apresentações, seleção, pré-luta e combate, além de outros pontos que exibem os lutadores.

Seu papel é dar uma representação visual aos personagens e aos movimentos definidos pelo restante do sistema. Ele não determina o dano de um golpe. Na implementação analisada, é o caminho ativo de desenho dos lutadores, mesmo existindo imagens PNG no projeto.

**Para explicar:** “A FighterArtView desenha os lutadores e suas poses, mantendo a identidade visual dos personagens nas diferentes telas.”

## 14. FighterSprites.java — o suporte existente para imagens dos lutadores

Contém uma alternativa de apresentação baseada nas imagens PNG dos personagens. Prepara essas imagens, trata o fundo e permite encaixá-las em retratos ou representações de combate.

Entretanto, não foram identificadas chamadas ativas dessa classe no fluxo atual de produção. Ela permanece como uma implementação disponível no projeto, enquanto a apresentação atual dos lutadores utiliza a FighterArtView. Essa distinção é importante para não atribuir aos PNG o visual que o usuário vê hoje.

**Para explicar:** “O FighterSprites reúne o tratamento das imagens PNG dos personagens, mas atualmente não é o componente utilizado para desenhar os lutadores nas telas.”

## 15. CharacterPortraitView.java — os retratos compactos dos personagens

Apresenta um retrato circular com a parte superior do personagem. É utilizado nos locais em que uma figura completa ocuparia espaço demais, como cards de escolha.

Ele reaproveita o desenho da FighterArtView para manter o personagem reconhecível em uma apresentação menor. Sua responsabilidade é visual: não controla seleção, desbloqueio ou atributos de combate.

**Para explicar:** “A CharacterPortraitView apresenta pequenos retratos dos personagens para facilitar sua identificação nos seletores.”

## 16. AttackEffects.java — a aparência dos poderes comuns

Desenha os efeitos visuais associados aos ataques comuns dos personagens, como teias, escudo, martelo, garras e energia. Também considera os poderes do protagonista personalizado.

Ajuda o jogador a reconhecer de quem é um ataque e qual é sua característica visual. O contato e o dano são calculados pelo motor de batalha; essa classe apresenta graficamente o golpe que está acontecendo.

**Para explicar:** “O AttackEffects dá aos poderes comuns a aparência característica de cada personagem, como teias para o Homem-Aranha e martelo para Thor.”

## 17. CombatEffects.java — os sinais visuais das habilidades e dos estados

Complementa a apresentação do combate com efeitos de habilidades especiais, avisos, escudos, congelamento, feixes, aliados e outras condições temporárias.

Seu papel é tornar compreensível o estado da batalha. Por exemplo, uma condição definida pelo motor precisa de algum sinal na tela para que o usuário possa percebê-la e reagir. Essa classe fornece parte dessa linguagem visual, em conjunto com os personagens e a arena.

**Para explicar:** “O CombatEffects mostra os efeitos e sinais das habilidades especiais e das condições temporárias, ajudando o jogador a entender o que está acontecendo na luta.”

## 18. SplashCinematicView.java — a cena animada de abertura

Produz a apresentação visual exibida na abertura do aplicativo, com Capitão América e Homem de Ferro em uma cena de confronto. Desenha os personagens, a composição e os efeitos da animação.

É o componente visual da Splash atual. O momento de sair da abertura e entrar na Home é coordenado pela MainActivity; essa View cuida da cena que acompanha essa passagem.

**Para explicar:** “A SplashCinematicView desenha a animação de abertura que apresenta o aplicativo antes da Home.”

## 19. ArcReactorView.java — um componente visual de reator disponível

Representa um reator tecnológico com círculos, brilho e animação. É um elemento de identidade visual relacionado ao universo do Homem de Ferro.

Não foi identificada utilização ativa desse componente na abertura ou nas telas atuais. A Splash em uso utiliza a SplashCinematicView. Portanto, ele deve ser descrito como um recurso visual existente, e não como o responsável pela abertura atual.

**Para explicar:** “A ArcReactorView é um componente animado de reator que permanece no projeto, mas não participa da interface atual identificada.”

## 20. GauntletView.java — a representação visual do progresso do Quiz

Desenha a Manopla do Infinito e apresenta as Joias conforme a quantidade informada pelo Quiz. É utilizada nas conquistas e na conclusão do desafio.

Transforma uma informação de progresso em uma imagem fácil de compreender. A decisão de conceder uma Joia pertence ao controle do Quiz; a GauntletView recebe essa situação e a representa visualmente.

**Para explicar:** “A GauntletView mostra a Manopla e suas Joias, permitindo visualizar o avanço do jogador no Quiz.”

## 21. GemArtView.java — o desenho individual de uma Joia

Desenha uma Joia com faces, contorno e brilho na cor correspondente. É um componente visual reutilizado nos indicadores de conquista e progresso do Quiz.

Enquanto a GauntletView representa a coleção de Joias na Manopla, a GemArtView permite destacar uma Joia individualmente. Ela não controla perguntas ou desbloqueios.

**Para explicar:** “A GemArtView desenha cada Joia do Infinito usada nos indicadores e nas conquistas do Quiz.”

## 22. StoryArtView.java — a ambientação visual das apresentações da história

Desenha elementos de portal e energia utilizados como fundo nas apresentações da campanha linear. A aparência pode variar para acompanhar a identidade associada ao Dr. Doom.

Seu papel é criar ambientação para a narrativa e para a apresentação dos personagens. Não decide o rumo da história nem o resultado de uma escolha: fornece o cenário gráfico sobre o qual a campanha apresenta seu conteúdo.

**Para explicar:** “A StoryArtView cria os fundos de portal e energia que ajudam a ambientar as apresentações do modo história.”

## 23. HeroEmblemView.java — uma biblioteca visual de símbolos

Desenha emblemas associados a personagens, como teia, reator, garras e escudo. É uma forma compacta de representar identidades por símbolos.

Não foram encontrados consumidores ativos dessa classe nas telas atuais. Nos cards de seleção, o caminho identificado utiliza retratos da CharacterPortraitView. Ela permanece no projeto como um componente visual disponível.

**Para explicar:** “A HeroEmblemView reúne desenhos de símbolos de personagens, mas esses emblemas não são o recurso utilizado atualmente nos cards de seleção.”

## 24. Typography.java — a padronização das fontes

Centraliza o acesso às fontes incluídas no aplicativo e permite que títulos, textos e elementos de interface compartilhem as mesmas famílias tipográficas. Também reaproveita fontes já carregadas.

Sua contribuição para o conjunto é a consistência visual: componentes diferentes podem seguir o mesmo padrão de letra sem cada tela precisar organizar o carregamento dos arquivos de fonte por conta própria.

**Para explicar:** “A Typography organiza as fontes do aplicativo para manter a identidade dos títulos e dos textos nas diferentes telas.”

## 25. VisitWorldActivity.java — a experiência de exploração

É a Activity que conduz Visitar Mundo, a exploração em orientação horizontal. Organiza a entrada do protagonista na ilha, os controles, a movimentação, a interação com personagens e objetos, os diálogos, o diário, o mapa e a passagem para encontros de combate.

Funciona como coordenadora dessa experiência: consulta as regras, mantém a sessão e solicita o desenho do mundo. Assim como a FightActivity organiza a arena, a VisitWorldActivity organiza a exploração. Ela possui um fluxo próprio, separado da campanha linear da aba História.

**Para explicar:** “A VisitWorldActivity coordena a exploração da ilha, conectando movimento, diálogos, objetivos e encontros dentro do mundo.”

## 26. WorldRules.java — a narrativa e as regras da exploração

Concentra as regras da campanha Fraturas: identidade dos personagens, diálogos, escolhas, pistas, confiança, missões, condições de acesso a encontros e critérios dos finais.

É o componente que dá significado à exploração. A Activity recebe uma ação do usuário e consulta essas regras para saber o que deve acontecer na história. O arquivo também contém definições gráficas utilizadas no desenho do mundo e de seus habitantes; sua responsabilidade atual é mais ampla do que apenas narrativa.

**Para explicar:** “O WorldRules define como a campanha da ilha funciona: o que cada personagem diz, como as escolhas afetam o progresso e quais condições permitem avançar e chegar aos finais.”

## 27. WorldValues.java — a infraestrutura das regras do mundo

Fornece estruturas de valores, funções e operações utilizadas pelo WorldRules e pelas classes de exploração. Permite que as regras compiladas em Java manipulem dad os e comportamentos em um formato compartilhado.

É uma peça de suporte interno. O jogador não vê esse arquivo diretamente, mas as regras dependem dele para representar e executar suas operações. Apesar da forma particular de organizar valores e funções, a execução é Java nativo, sem depender de uma WebView.

**Para explicar:** “O WorldValues oferece a base interna de dados e operações que permite executar as regras da exploração.”

## 28. WorldJson.java — a leitura e a escrita dos dados da exploração

Transforma os dados JSON do mundo em estruturas que as classes Java conseguem utilizar e também permite transformar o estado da exploração novamente em texto JSON.

Participa tanto do carregamento dos dados locais da ilha quanto da representação da sessão para sua restauração. Ele não decide a história e não desenha o mapa: cuida da passagem entre o formato de arquivo e os dados utilizados pelo módulo.

**Para explicar:** “O WorldJson permite carregar os dados da ilha e converter a sessão de exploração para o formato utilizado na restauração do estado.”

## 29. WorldMap.java — a organização física do mundo

Representa a geometria da ilha: dimensões, tipos de terreno, regiões, obstáculos e portões. Controla onde o personagem pode caminhar e como a câmera acompanha seu deslocamento.

É o responsável pelo espaço físico da exploração. O WorldRules pode determinar que um caminho foi liberado pela história, mas o WorldMap utiliza esse estado para permitir a passagem na movimentação. O desenho correspondente é feito por outros componentes.

**Para explicar:** “O WorldMap organiza o espaço da ilha e controla terreno, colisões, passagens e acompanhamento da câmera.”

## 30. WorldArt.java — a apresentação gráfica da ilha

Compõe visualmente o mundo, reunindo terreno, objetos, personagens e elementos de orientação. Também participa do desenho do minimapa e da apresentação do mapa maior.

Trabalha com a posição da câmera e reaproveita imagens e desenhos para evitar reconstruir tudo sem necessidade. Sua função no conjunto é transformar os dados físicos e narrativos em uma cena visível e organizada, com elementos posicionados em camadas.

**Para explicar:** “O WorldArt desenha a ilha e seus elementos, transformando os dados do mapa e da sessão na cena de exploração que aparece na tela.”

## 31. WorldCanvas.java — a ligação entre os comandos gráficos e o Android

Adapta os comandos de desenho utilizados pelas regras do mundo para o Canvas do Android. Faz a ligação entre operações como pintar formas, posicionar elementos e escrever textos e a superfície gráfica nativa.

É uma camada de apoio ao desenho. Não organiza a narrativa nem a localização das regiões por conta própria; permite que as instruções gráficas do módulo sejam executadas na interface Android.

**Para explicar:** “O WorldCanvas traduz as instruções gráficas do sistema do mundo para o mecanismo de desenho nativo do Android.”

## 32. WorldBattleBridge.java — a passagem da exploração para a arena

Prepara a abertura de uma luta a partir de um encontro da ilha. Identifica protagonista e oponente, verifica se aquele personagem pode participar do combate e reúne as informações que a FightActivity precisa receber.

Permite que a exploração utilize a arena já existente, em vez de manter um segundo sistema de luta. Essa ponte prepara o confronto; abrir uma batalha não significa conceder sua vitória ou concluir o encontro na história.

**Para explicar:** “O WorldBattleBridge conecta os encontros da ilha à arena de combate, enviando os participantes e os dados necessários para começar a luta.”

## 33. WorldEnemyArt.java — a aparência dos inimigos genéricos na arena

Desenha adversários como agentes e doombots quando eles entram em combate a partir da exploração. Fornece poses e características visuais próprias para esses inimigos.

Complementa a arte dos heróis, permitindo apresentar adversários que não pertencem ao elenco principal de personagens Marvel selecionáveis. Sua função é desenhá-los na arena; o comportamento e as regras de combate permanecem no BattleEngine.

**Para explicar:** “O WorldEnemyArt desenha na arena os inimigos genéricos encontrados na exploração, como agentes e doombots.”

## Como contar essa arquitetura em uma apresentação

Uma explicação possível é:

> “O aplicativo tem uma Activity principal que coordena as abas, uma Activity para as lutas e outra para explorar o mundo. Os XMLs fornecem estruturas básicas, e boa parte das telas é montada em Java. Classes auxiliares buscam e armazenam os dados, enquanto componentes de desenho apresentam os personagens e os efeitos. Nas lutas, o BattleEngine calcula as regras e a FightActivity apresenta o combate. Na exploração, a VisitWorldActivity coordena a experiência, o WorldRules organiza a narrativa, o WorldMap controla o espaço e o WorldArt desenha a ilha.”

### As distinções que ajudam a explicar os arquivos

| Pergunta | Arquivos que melhor representam a resposta |
|---|---|
| Quem coordena a experiência e a interação? | MainActivity, FightActivity e VisitWorldActivity |
| Quem monta as interfaces das áreas principais? | MainActivity, HomeReference, BattleHubReference e StoryMode, apoiados pelo ScreenFragment |
| Quem busca, guarda ou prepara conteúdo? | ComicVineClient, CatalogStore, CharacterTranslator, GeminiClient e QuizGenerator |
| Quem define o que pode acontecer em uma luta? | BattleEngine |
| Quem define a sequência da campanha linear? | StoryMode |
| Quem define narrativa e progressão da ilha? | WorldRules, apoiado por WorldValues |
| Quem controla espaço físico e deslocamento na ilha? | WorldMap |
| Quem apresenta personagens, cenário e efeitos? | FighterArtView, WorldArt e os componentes visuais específicos |
| Quem conecta uma experiência a outra? | A navegação da MainActivity e a WorldBattleBridge nos encontros da ilha |

Em resumo: **as Activities coordenam; os componentes de conteúdo fornecem informações; os motores e regras determinam o comportamento; as classes visuais apresentam o resultado.** Algumas classes acumulam mais de um desses papéis na implementação atual.

Os arquivos foram descritos por sua participação no projeto existente. Nesta tarefa foi criado somente este guia; não houve alteração no código do aplicativo nem execução de build ou testes.
