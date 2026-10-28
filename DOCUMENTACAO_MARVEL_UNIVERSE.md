# Documentação Técnica Completa — MarvelUniverse

**Base analisada:** projeto Android `ProjetoMarvel`, namespace e application ID `com.example.projetomarvel`, nome apresentado `Marvel Universe`. **Data da análise:** 16 de outubro de 2026. **Escopo:** fontes de produção, testes, XML, assets, configurações de compilação e documentação existentes no diretório deste projeto.

Este documento descreve o código encontrado nessa data. Não é uma especificação de alterações futuras. Nenhuma regra de solicitações anteriores foi assumida como implementada sem conferência nos arquivos. A análise foi estática: não foram executados aplicativo, testes, APIs ou build nesta tarefa de documentação. A existência de um teste ou APK não comprova, por si, que a versão atual funciona em um dispositivo.

**Convenções de leitura:** “confirmado pelo código” significa que há uma implementação ou chamada verificável; “interpretação arquitetural” explica seu papel técnico, sem atribuir intenção histórica ao autor. Quando a intenção original, disponibilidade externa ou resultado visual não pode ser comprovado, aplica-se: **“Não foi possível determinar com segurança apenas pelos arquivos analisados.”** As referências de linha no inventário final correspondem à fotografia do código desta análise e podem mudar com futuras edições. Valores de chaves privadas não são reproduzidos.

## Índice

1. [Visão geral](#1-visão-geral)
2. [Tecnologias utilizadas](#2-tecnologias-utilizadas)
3. [Estrutura do projeto e cobertura](#3-estrutura-do-projeto-e-cobertura)
4. [Arquitetura geral](#4-arquitetura-geral)
5. [Mapa de telas](#5-mapa-de-telas)
6. [Navegação e contratos de Intent](#6-navegação-e-contratos-de-intent)
7. [Inicialização e ciclo de vida](#7-inicialização-e-ciclo-de-vida)
8. [Sistemas e funcionalidades](#8-sistemas-e-funcionalidades)
9. [Análise dos arquivos Java](#9-análise-dos-arquivos-java)
10. [Layouts XML](#10-layouts-xml)
11. [Recursos visuais, dados e assets](#11-recursos-visuais-dados-e-assets)
12. [AndroidManifest.xml](#12-androidmanifestxml)
13. [Gradle e configurações](#13-gradle-e-configurações)
14. [Comunicação Java ↔ XML](#14-comunicação-java--xml)
15. [Dados, concorrência e estado](#15-dados-concorrência-e-estado)
16. [Dependências entre arquivos](#16-dependências-entre-arquivos)
17. [Matriz telas × arquivos](#17-matriz-telas--arquivos)
18. [Matriz funcionalidades × arquivos](#18-matriz-funcionalidades--arquivos)
19. [Impacto da remoção](#19-impacto-da-remoção)
20. [Pontos técnicos importantes e testes existentes](#20-pontos-técnicos-importantes-e-testes-existentes)
21. [Glossário](#21-glossário)
22. [Como o MarvelUniverse funciona internamente](#22-como-o-marveluniverse-funciona-internamente)
23. [Inventário rastreável da base](#23-inventário-rastreável-da-base)

## 1. Visão geral

O MarvelUniverse reúne um catálogo de personagens Marvel e experiências interativas em um aplicativo Android nativo. A `MainActivity` apresenta cinco destinos: **Home, Jarvis, Quiz, História e Lutas**. A História também abre uma exploração independente chamada **Visitar Mundo**, cuja campanha se chama **Fraturas**. As lutas são executadas por uma Activity própria em orientação horizontal.

O público provável são pessoas interessadas no universo Marvel, em perguntas sobre personagens e em jogos simples de combate e narrativa. Essa caracterização do público é uma interpretação dos conteúdos; não existe segmentação de usuários, cadastro ou pesquisa de público implementada no código.

### 1.1 Experiências confirmadas

| Experiência | Comportamento implementado | Arquivos de referência |
|---|---|---|
| Abertura | Cena animada Capitão América × Homem de Ferro; depois Home | `MainActivity.showSplash`, `SplashCinematicView`, `activity_splash.xml` |
| Catálogo | Busca dados reais da Comic Vine, filtra editora Marvel, carrega progressivamente e salva SQLite | `MainActivity.loadCatalog`, `ComicVineClient`, `CatalogStore` |
| Busca | Filtra a coleção já recebida por nome traduzido, nome original ou identidade civil | `MainActivity.refreshVisibleCatalog`, `HomeReference` |
| Detalhes | Barra fixa, imagem, identidade, editora, poderes e descrição; busca adicional sob demanda | `MainActivity.details/detailScreen` |
| Tradução | Inglês → português no dispositivo, com retorno ao original em falhas | `CharacterTranslator` |
| Jarvis | Interface de conversa, respostas locais e chamada opcional ao Gemini com confirmação na tela | `MainActivity.jarvis/sendJarvis`, `GeminiClient` |
| Quiz | 24 perguntas, seis etapas de quatro; geração opcional pelo Gemini, banco local alternativo, erros e seis Joias | `MainActivity`, `QuizGenerator`, `GauntletView`, `GemArtView` |
| Campanha linear | Criação de protagonista ou seleção de herói, decisões e três batalhas | `StoryMode` |
| Combate livre | Roster de 13, seleção de adversário, três dificuldades, pré-luta e resultado | `BattleHubReference`, `MainActivity`, `FightActivity`, `BattleEngine` |
| Exploração | Ilha em pixel art, 29 regiões, NPCs, pistas, portões, diário, mapa, diálogos, confrontos e finais | `VisitWorldActivity`, classes `World*`, `world-data.json` |
| Progressão | Desbloqueios temporários de Thanos e Doom; histórico permanente de vitórias/derrotas livres | `MainActivity` e `FightActivity` |

Não foram encontradas implementações de áudio, login, multiplayer, servidor próprio, compras, inventário com gestão de itens por tela, Service Android, Navigation Component, Room ou WebView. Há objetos e flags narrativas de itens na exploração; isso não equivale a um sistema geral de inventário RPG. Há movimento em mapa, mas não níveis de experiência, atributos distribuíveis ou árvore de habilidades.

### 1.2 O que mudou em relação às descrições antigas

O código atual contém vida padrão **120**, Thanos **130**, aliados **20**; não 100 para todos. Poder comum, especial e passiva variam por personagem. Jarvis e Quiz podem usar Gemini. A Splash usa uma cena de confronto, sem a frase antiga de saudação. O Story Mode linear permanece somente em memória e é reiniciado ao sair de sua aba. O modo Fraturas tem estado e fluxo próprios. As imagens PNG dos lutadores ainda existem, mas o fluxo atual desenha os personagens de combate por `FighterArtView`, não por `FighterSprites`.

Esses são fatos do código atual, não correções feitas nesta tarefa. Textos de documentação anterior e algumas expectativas dos testes descrevem versões diferentes; as divergências são registradas no capítulo 20.

## 2. Tecnologias utilizadas

| Tecnologia | Uso observado |
|---|---|
| Java | Activities, Fragment, regras, modelos internos, HTTP, armazenamento e desenhos |
| XML Android | Contêineres de tela, item de catálogo, tema, ícones, animações, manifesto e backup |
| AndroidX AppCompat/Fragment | `MainActivity extends AppCompatActivity`; `ScreenFragment extends Fragment` |
| Views nativas | `FrameLayout`, `LinearLayout`, `ScrollView`, `TextView`, `EditText`, `Button`, `ImageView`, `ProgressBar` |
| RecyclerView | Reciclagem das linhas do catálogo, com `HeroAdapter` e `LinearLayoutManager` |
| Canvas/Paint/Path | Personagens, efeitos, Splash, Joias, Manopla, arena e exploração |
| Handler/Looper | Retorno à thread principal, contagem, digitação e atualização dos jogos |
| ExecutorService | Requisições Comic Vine, imagens, Gemini e geração do Quiz fora da UI |
| HttpURLConnection | HTTP direto; não há Retrofit/OkHttp explicitamente utilizado |
| JSON | `org.json` em APIs; `WorldJson` próprio no módulo de exploração |
| SQLiteOpenHelper | Cache `marvel_catalog.db` |
| SharedPreferences | Marcadores de sincronização e placar de lutas; limpeza de dados legados da história |
| ML Kit Translate | Tradução local dos textos Comic Vine; pode baixar modelos |
| Gradle Kotlin DSL | Arquivos `.gradle.kts` configuram o build; isso não significa que as classes do app sejam Kotlin |
| JUnit/AndroidX Test/Espresso | Testes locais de motor e testes de fluxo em dispositivo |

A barra inferior é uma `LinearLayout` preenchida manualmente; apesar da aparência de navegação inferior, **não há `BottomNavigationView` no fluxo inspecionado**. Material e ConstraintLayout são dependências declaradas, mas não se deve deduzir que toda tela use seus componentes. O tema Material3 é uma utilização efetiva de Material.

## 3. Estrutura do projeto e cobertura

```text
ProjetoMarvel/
├── DOCUMENTACAO_MARVEL_UNIVERSE.md    ← este documento
├── README.md
├── VISITAR_MUNDO_ANDROID.md
├── .gitignore
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── local.properties                  ← local, credenciais não reproduzidas
├── gradlew / gradlew.bat
├── gradle/
│   ├── libs.versions.toml
│   ├── gradle-daemon-jvm.properties
│   └── wrapper/
│       ├── gradle-wrapper.properties
│       └── gradle-wrapper.jar
├── .idea/                            ← configuração do Android Studio
└── app/
    ├── .gitignore
    ├── build.gradle.kts
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── keepRules/rules.keep
        │   ├── java/com/example/projetomarvel/  ← 33 arquivos Java
        │   ├── assets/
        │   │   ├── fonts/            ← 9 TTF e 4 licenças OFL
        │   │   └── visit_world/
        │   │       ├── world-data.json
        │   │       └── terrain/      ← 150 PNG, grade 15 × 10
        │   └── res/
        │       ├── anim/             ← 2 transições
        │       ├── drawable/         ← 10 XML
        │       ├── drawable-nodpi/    ← 13 PNG de referência
        │       ├── layout/           ← 6 XML
        │       ├── mipmap-anydpi-v26/ ← 2 ícones adaptativos XML
        │       ├── mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/
        │       ├── values/           ← strings, cores, tema
        │       ├── values-night/     ← tema noturno
        │       └── xml/              ← 2 regras de backup
        ├── test/java/com/example/projetomarvel/        ← 4 arquivos
        └── androidTest/
            ├── java/com/example/projetomarvel/         ← 2 arquivos
            └── assets/world-device-fixtures.json
```

Todos os arquivos Java de produção, seis layouts, XML de recursos, configurações principais e fontes dos testes foram lidos. Os dados de exploração foram relacionados às regras que os consomem; o inventário final cobre cada região, encontro e grupo de assets. As 150 imagens de terreno, as fontes e os ícones raster são tratados como grupos de recursos binários, sem supor significado funcional a partir apenas do nome. Diretórios de build e caches são separados do código autoral.

`main/java` concentra as classes em um único package; não há subpackages `repository`, `model` ou `viewmodel`. A separação de responsabilidades ocorre entre arquivos e classes internas. `assets` é acessado por caminho textual via `AssetManager`; `res` produz identificadores `R`. `src/test` roda no host e `src/androidTest` exige Android. `build/`, `.gradle/` e `.idea/` não são a implementação distribuída das telas.

## 4. Arquitetura geral

```text
AndroidManifest → MainActivity
                    ├── SplashCinematicView
                    ├── activity_main.xml + ScreenFragment + fragment_screen.xml
                    ├── HomeReference → HeroAdapter → item_hero.xml
                    │       └── ComicVineClient ↔ CatalogStore
                    │                           └── CharacterTranslator
                    ├── Jarvis → GeminiClient ou jarvisFallback
                    ├── Quiz → QuizGenerator + GeminiClient ou seedQuestions
                    │           └── GemArtView / GauntletView
                    ├── StoryMode → FightActivity
                    │       └── VisitWorldActivity
                    │              ├── WorldRules ↔ WorldValues
                    │              ├── WorldJson ← world-data.json
                    │              ├── WorldMap → colisões / câmera
                    │              ├── WorldArt → WorldCanvas → Canvas Android
                    │              └── WorldBattleBridge → FightActivity
                    └── BattleHubReference → seleção → pré-luta → FightActivity
                                                                   ├── BattleEngine
                                                                   ├── FighterArtView
                                                                   ├── CombatEffects
                                                                   ├── AttackEffects
                                                                   └── WorldEnemyArt
```

### 4.1 Responsabilidades e acoplamento

`MainActivity` é a coordenadora da interface portrait e também contém modelos, Adapter, estado do Quiz, conversa, sincronização e helpers visuais. `HomeReference`, `BattleHubReference` e `StoryMode` recebem a própria Activity e acessam `body`, `fragmentSurface` e outros atributos do package. Isso é composição de telas com forte acoplamento à Activity; não foi identificado MVVM ou um repositório único de dados.

O combate possui uma fronteira mais clara: `BattleEngine` não importa Android, enquanto `FightActivity` transforma eventos de UI em comandos e desenha o estado resultante. Assim, as regras podem ser exercitadas em testes Java. A exploração usa mapas dinâmicos `Map<String,Object>` e funções Java registradas em um `Scope`; `WorldCanvas` adapta os comandos dessas funções para desenho nativo.

`ScreenFragment` hospeda uma superfície vazia, mas não decide que tela mostrar. `MainActivity` usa uma string `screen` e constrói as Views após a transação do Fragment. A separação é distinta de ter um Fragment com controlador próprio para cada aba.

## 5. Mapa de telas

| Tela ou estado | Responsável | XML base | Conteúdo/ações e destino |
|---|---|---|---|
| Splash | `MainActivity.showSplash` | `activity_splash` | Confronto animado; toque antecipa a entrada; abre Home |
| Home | `HomeReference` + `MainActivity` | `activity_main`, `fragment_screen`, `item_hero` | Contagem, convite à História, busca, lista; card → Detalhes |
| Detalhes | `MainActivity.open/detailScreen` | Mesmos contêineres | Toolbar fixa; corpo rolável com imagem e textos; voltar → `previousScreen` |
| Jarvis | `MainActivity.jarvis` | Mesmos contêineres | Header, transcrição, campo, enviar; diálogo de confirmação Gemini quando configurado |
| Quiz carregando | `MainActivity.quiz` | Mesmos contêineres | Texto de geração e ProgressBar |
| Quiz pergunta | `MainActivity.quiz` | Mesmos contêineres | Quatro respostas, confirmar, feedback, próxima, acertos/erros/Joias |
| Conquista de Joia | `showGemUnlock` | Overlay Java | Joia ampliada, Manopla parcial, continuar |
| Quiz concluído | `quizCompleteScreen` | Contêiner do Quiz | Pontuação, seis Joias, reiniciar |
| História abertura/seleção/criação | `StoryMode.render` | `fragment_screen` | Seletor custom/11 heróis, nome, aparência, cor, poder, personalidade, resumo |
| História narrativa | `StoryMode.render` | Idem | Diálogos, decisões, consequências, preparação; batalhas numeradas |
| História conclusão | `StoryMode.render` | Idem | Epílogo e reinício; Doom desbloqueado após terceira vitória |
| Lutas | `BattleHubReference` | `fragment_screen` | Guia horizontal de habilidades, dificuldade e começar |
| Seleção jogador/BOT | `MainActivity.pickScreen` | Idem | Prévia, 13 cards, bloqueios, confirmação em dois passos |
| Pré-luta livre | `MainActivity.preFight` | Idem | Dois combatentes, VS, nomes, orientação e LUTAR |
| Combate | `FightActivity` | `activity_fight` | HUD, arena, movimentação, ataques, defesa, especial, sair |
| Resultado | `FightActivity.showResult` | Overlay Java | Vitória/derrota, HP, golpes, pose do jogador, repetir/retornar |
| Visitar Mundo criação | `VisitWorldActivity.renderCreation` | `activity_visit_world` | Quatro passos e prévia em quatro direções |
| Exploração Fraturas | `VisitWorldActivity` | Idem | Mapa/câmera, personagem, NPCs, objetivo, pad, conversar/examinar |
| Conversa na ilha | `showDialogue` | Overlay Java | Retrato, texto, duas escolhas quando existentes e continuar |
| Encontro na ilha | `showEncounter` | Overlay Java | Protagonista × NPC, contexto, ir para luta ou preparar |
| Diário/mapa/saída/final da ilha | `book` e métodos específicos | Overlay Java | Dossiê, mapa grande, confirmação de saída, epílogo |

Não há XML diferente para cada pergunta, fala ou resultado. Muitas “telas” são estados que substituem os filhos de um mesmo contêiner.

## 6. Navegação e contratos de Intent

### 6.1 Dentro da MainActivity

```text
Splash → Home ↔ Jarvis ↔ Quiz ↔ História ↔ Lutas
          │                    │          └→ Seleção jogador → Seleção BOT → Pré-luta
          └→ Detalhes           ├→ criação → resumo → narrativa → pré1/pré2/pré3
                               └→ VISITAR MUNDO → VisitWorldActivity
```

`open(String tab)` troca o `ScreenFragment` com `replace(...).commitNow()`. O Bundle do Fragment recebe `screen`, mas a renderização efetiva usa `MainActivity.screen`. Não há `addToBackStack`; o retorno é programado em `onBackPressed`. Algumas transições internas de Lutas alteram `screen` e chamam `renderTab` sem substituir o Fragment.

`details(Hero h)` guarda o objeto em `selectedHero`, grava a tela de origem em `previousScreen` e abre `Detalhes`; não usa Intent nem serializa o personagem. `makeNav` oculta a navegação em Detalhes e Pré-luta. A seta de Detalhes abre a origem. Voltar do sistema trata História, Pré-luta, Seleção e Detalhes, depois retorna à Home antes de encerrar a Activity.

### 6.2 Transições entre Activities

| Origem → destino | Gatilho | Contrato |
|---|---|---|
| `MainActivity` → `FightActivity` | LUTAR da pré-luta livre | `player`, `bot`, `difficulty`; `startActivity` |
| `StoryMode` → `FightActivity` | Iniciar uma das três batalhas | `startActivityForResult`, request `721` |
| `StoryMode` → `VisitWorldActivity` | VISITAR MUNDO | `startActivity`, sem extras |
| `VisitWorldActivity` → `FightActivity` | Ir para a luta | `WorldBattleBridge.intent`; request `842` |
| `FightActivity` → chamador | Botão de resultado ou saída | `setResult` para História/mundo, depois `finish` |

### 6.3 Extras reais de batalha

| Chave | Tipo e valor padrão no receptor | Uso |
|---|---|---|
| `player` | String; Homem-Aranha se null | Nome do jogador, seleção de skill/arte |
| `bot` | String; Homem de Ferro se null | Adversário e skill |
| `difficulty` | int 1, limitado a 0–2 | Fácil=0, Médio=1, Difícil=2 |
| `story_battle` | int -1 | 0/1/2 identifica campanha linear; -1 outras lutas |
| `story_color` | int ARGB, vermelho de fallback | Cor do protagonista customizado |
| `story_skin` | int -1 | -1 usa herói; >=0 identifica custom na campanha/mundo |
| `story_build`, `story_hair`, `story_style` | int 1/0/0 | Porte, cabelo, apresentação |
| `story_power` | String Energia se null | Skill custom e desenho do poder |
| `story_choice_tech` | boolean false | Redução do dano do BOT na batalha 2 da história |
| `story_choice_rescue` | boolean enviado por StoryMode | Não foi encontrada leitura correspondente em FightActivity/BattleEngine |
| `world_encounter` | String null | Presença ativa `worldMode`; identifica o NPC/encontro no retorno |
| `world_bot_generic` | boolean false | Solicita `Skill.GENERIC`, independentemente do nome do BOT |
| `world_bot_label` | String | Nome narrativo no HUD |
| `world_generic_kind` | String | Aparência do inimigo genérico, como doombot/agent |
| `world_custom` | boolean enviado pela ponte | Não é lido em FightActivity; a detecção efetiva usa `story_skin` |

No retorno linear são enviados `story_battle` e `story_victory`. `MainActivity.onActivityResult` exige request 721, RESULT_OK e Intent não null. Abre História e encaminha para `StoryMode.battleResult`. Vitória atualiza a fase; derrota conserva a preparação.

No retorno ao mundo são enviados `world_encounter` e `world_victory`. `VisitWorldActivity.onActivityResult` trabalha com seu `pendingEncounter`, limpa a pendência e só conclui a regra se request=842, resultado correto, vitória e encontro ainda permitido. Cancelar ou perder não concede o evento. A batalha da ilha não atualiza o placar de lutas livres nem os booleans globais de desbloqueio.

### 6.4 Bundles e recriação

`ScreenFragment.create` usa Bundle apenas para a string `screen`. `VisitWorldActivity.onSaveInstanceState` escreve a sessão em JSON no Bundle (`session`) e a chave `pending`; em `onCreate`, uma sessão recebida pode voltar ao campo estático `activeSession`. `MainActivity` e `FightActivity` não implementam salvamento equivalente dos próprios estados completos. `configChanges` no manifesto evita recriação por algumas mudanças, mas não é uma garantia de sobrevivência a encerramento de processo.

## 7. Inicialização e ciclo de vida

### 7.1 Caminho completo de abertura

1. O launcher resolve MAIN/LAUNCHER para `MainActivity`, única Activity exportada.
2. `onCreate(Bundle b)` chama o pai e define cores das barras do sistema.
3. Cria `CharacterTranslator`, `CatalogStore` e um `LruCache<String,Bitmap>` para imagens, com capacidade aproximada de 1/8 da memória máxima da VM em KB, no mínimo 1024 KB.
4. `seedQuestions` carrega 24 perguntas locais e as copia para `localQuestions`.
5. Instancia `StoryMode(this)`, cujo construtor limpa as preferências antigas `story_mode`.
6. `showSplash` infla `activity_splash.xml`; a custom View começa seu relógio quando é anexada à janela.
7. Um Handler agenda a entrada após 1850 ms, seguida de fade de 220 ms. Toque antecipa a chamada. O teste `root != null` reduz reentrada depois de a interface principal existir.
8. O fim da animação chama `open("Home")`, que infla `activity_main`, recupera contêineres e instala os insets.
9. Faz a transação síncrona de `ScreenFragment`, constrói o corpo apropriado, renderiza Home e barra inferior.
10. `loadCatalog` inicia no executor de I/O. A interface não espera o download completo.

### 7.2 Ciclo das três Activities

| Classe | Eventos implementados e efeito |
|---|---|
| MainActivity | `onCreate` inicializa; `onResume` redesenha Lutas quando necessário; `onActivityResult` recebe campanha; `onBackPressed` navega; `onDestroy`, apenas se finishing, fecha tradutor e interrompe `io`/`imageIo` |
| FightActivity | `onCreate` lê extras, monta arena, reinicia motor e conta; `onResume` limpa pausa/tempo anterior; `onPause` pausa simulação e limpa controles; `onDestroy` remove callbacks do Handler |
| VisitWorldActivity | `onCreate` lê asset e monta mundo; `onResume` inicia tick; `onPause` cancela tick, limpa entrada e salva referência da sessão; `onSaveInstanceState` serializa; `onActivityResult` conclui encontros; `onBackPressed` fecha contexto/solicita saída |

Não há overrides de `onStart`/`onStop` com regras próprias. `MainActivity.onDestroy` não encerra explicitamente `geminiIo` e `quizIo`, não cancela todos os seus posts e não fecha explicitamente `CatalogStore`; isso é um limite real do ciclo atual, não uma funcionalidade de limpeza presumida.

## 8. Sistemas e funcionalidades

### 8.1 Catálogo Comic Vine

O catálogo é distinto do roster. `ComicVineClient` consulta `/characters/` com `format=json`, limit 100 e User-Agent descritivo. Primeiro podem ser consultados os doze personagens prioritários por nome. A lista de IDs da editora `/publisher/4010-31/` forma uma fila finita; se essa consulta falhar, o app usa varredura geral por offset. Mesmo em modo direcionado, cada registro é aceito apenas quando `publisher.name` é `Marvel Comics` ou `Marvel`, ignorando caixa.

`Page.returned` conta todos os itens recebidos da API, não só os Marvel aceitos. Essa diferença é necessária para avançar corretamente numa página com outras editoras. `seen` remove IDs duplicados entre cache, busca inicial e páginas. Cada lote novo é salvo em transação SQLite e adicionado à coleção no Handler principal. Até quatro tentativas por lote usam esperas de 1,5/3/6 segundos, em thread de I/O. O offset salvo permite retomar; ao trocar entre `publisher` e `scan`, volta a zero. Ao completar, salva `catalog_sync_at` e zera o offset.

Cache não vazio com sincronização de menos de 24 horas evita uma nova varredura. Sem chave, a Home abre com o que houver em SQLite e informa a falta de configuração. Falha de rede deixa o cache disponível. Não há botão de carregar mais. A lógica adiciona personagens novos; não é observada uma atualização completa de todos os objetos já presentes em `seen`, nem remoção de registros que deixaram de vir da editora.

### 8.2 Busca, ordenação, tradução e detalhes

Sem consulta, `refreshVisibleCatalog` escolhe um registro canônico para cada prioridade: Spider-Man, Iron Man, Captain America, Thor, Hulk, Black Widow, Black Panther, Doctor Strange, Scarlet Witch, Wolverine, Deadpool e Captain Marvel. Pontua nome exato e identidade civil; o restante usa números pseudoaleatórios derivados do ID. A ordem é estável para o mesmo ID, embora armazenada em `sessionOrder`.

Com consulta, não mantém prioridades: procura substring no nome exibido, original e civil, e ordena alfabeticamente o resultado. Não foi encontrada normalização de acentos. A busca só conhece os dados recebidos; páginas novas chamam a mesma atualização.

O Adapter trunca o resumo a aproximadamente 150 caracteres antes de traduzi-lo. `holder.card.tag` recebe o ID; o callback só escreve se o card ainda representa esse ID. `loadImage` faz controle parecido por URL em `ImageView.tag` e compartilha downloads pendentes. Isso resolve o risco comum de um resultado atrasado preencher uma linha reciclada incorreta.

Detalhes recebem imagem, nome, nome real e editora já presentes. Descrição/poderes ausentes disparam `fetchDetails`; a descrição usa `description`, depois `deck`, remove HTML e limita a até 3000 caracteres, preferindo fim de frase depois de 2200. O banco é atualizado. Falhas mantêm os textos disponíveis. A toolbar fixa é irmã do ScrollView, não filha dele.

### 8.3 Jarvis

`jarvis` desenha cabeçalho, um indicador Online, transcrição e composer. Não há lista de perguntas sugeridas. “Online” é um texto da interface; não é medição ativa da rede ou da saúde do Gemini. Cada reconstrução da tela insere a saudação; as bolhas exibidas não são reconstruídas de um histórico persistido.

Sem configuração Gemini, enviar usa resposta local. Com configuração, um AlertDialog mostra a pergunta e informa que ela e o contexto recente serão enviados; “Enviar ao Gemini” usa rede e “Resposta local” evita a chamada. `sendJarvis` coloca a pergunta e indicador de digitação, trabalha em `geminiIo` e captura falhas com `jarvisFallback`.

Homem-Aranha/Peter Parker e Quarteto/nomes reconhecidos têm espera mínima de 5000 ms. Outras perguntas têm mínimo de 750 ms. A resposta pode demorar mais por rede/fila. `jarvisLastDisplayAt` ordena exibição com intervalo mínimo de 100 ms. O histórico remoto é limitado a 12 turnos; a cada envio remoto acrescenta pergunta e resposta. Há respostas locais específicas de Homem-Aranha e Quarteto, e uma resposta cortês genérica para outros assuntos.

### 8.4 Quiz

Ao entrar pela primeira vez, `startQuizGeneration` começa com `localQuestions`. Quando a chave Gemini existe, `quizLoading` bloqueia a tela de perguntas até terminar a tentativa de geração. Resultado inválido/rede com falha deixa o banco local. O parser exige exatamente 24 perguntas, quatro alternativas distintas, índice de resposta 0–3, etapas sequenciais de quatro e textos dentro dos limites. A validação é estrutural: não comprova a verdade factual da pergunta gerada.

`selected` identifica uma alternativa ainda não confirmada. Confirmar altera `correct` ou `phaseMisses`; o segundo erro da etapa chama `resetQuiz` e reinicia todo o desafio. A primeira confirmação apenas mostra feedback. “Próxima” avança `qIndex`; a cada múltiplo de quatro incrementa Joias e zera erros da etapa. Na pergunta 24 concluída, seta `quizDone` e `thanosUnlocked`.

`showGemUnlock` recebe a quantidade conquistada, oculta nav, anima a Joia e desenha `GauntletView(number)`. Só as primeiras `number` posições acendem. Concluir mostra `GauntletView(6)` e REINICIAR. `resetQuiz` zera progresso e tenta gerar uma sessão de perguntas, mas não limpa o desbloqueio estático de Thanos. `quizGeneration` invalida respostas de gerações antigas; não cancela a requisição HTTP já enviada.

### 8.5 Campanha linear StoryMode

São 25 estados de `PHASES`, indexados de 0 a 24:

```text
0 landing → 1 hero_choice
  custom: 2 name → 3 appearance → 4 color → 5 power
  herói: salta essas quatro telas
→ 6 personality1 → 7 personality2 → 8 summary
→ 9 intro1 → 10 intro2 → 11 dialogue1 → 12 choice1 → 13 consequence1
→ 14 pre1 → [Wolverine] → 15 after1
→ 16 dialogue2 → 17 choice2 → 18 consequence2
→ 19 pre2 → [Homem-Aranha] → 20 after2
→ 21 reveal → 22 prep → 23 pre3 → [Dr. Doom] → 24 ending
```

Há opção customizada e os onze primeiros heróis do roster; Doom e Thanos não aparecem nesse seletor linear. `selectedHero` vazio escolhe custom; `protagonist` centraliza o nome usado. `ally` substitui um interlocutor que seria o mesmo herói escolhido por J.A.R.V.I.S. ou Wong. A cor muda o tema; fases 21–23 usam verde de Doom. Poder, cabelo, pele, porte e apresentação chegam ao desenho e à batalha.

A primeira decisão trata resgate/âncora e altera diálogos. A segunda escolhe tecnologia/magia; o ramo tecnológico envia `story_choice_tech`, reduzindo dano do BOT final a 76% antes de outras defesas. Textos narram outras vantagens, mas não se deve assumir regras extras sem receptor correspondente. O enredo termina salvando o universo e a terceira vitória desbloqueia Doom.

`save` é um método vazio intencionalmente comentado. `resetForTabExit` zera o estado linear quando `MainActivity.open` sai de História. Recomeçar usa fase 1; sair de aba usa fase 0. O retorno da batalha mantém a Activity hospedeira e não precisa passar por esse reset de aba.

### 8.6 Combate: vida e ataques atuais

O motor é a fonte de verdade. `Fighter` nasce com 120 HP, exceto Thanos=130 e projeção `ALLY`=20. Slot 0 é soco, 1 chute, 2 poder comum; o botão Especial é outra ação. Durações base são 0,28/0,40/0,50 s e cooldowns 0,38/0,70/1,25 s. `attack` verifica ação válida, orientação e recargas, inicia um movimento; o efeito é resolvido mais adiante pelo relógio, não no clique.

| Lutador | Poder comum real | Especial e passiva principais |
|---|---|---|
| Homem-Aranha | Teia 5, aplica slow por 1,5 s | Estilingue com troca de lado e golpe 15; imunidade durante especial; sentido reduz velocidade de projétil próximo a 55% |
| Homem de Ferro | Projétil 8 | Unibeam 35, aviso 0,5 s, override de defesa 5, stun no alvo e exaustão no emissor; planar ao segurar pulo |
| Wolverine | Corte corpo a corpo 6, alcance 216 | Duas investidas de 10; cura 5 após 2 s sem agir/receber dano, reiniciando relógio |
| Capitão América | Escudo 4 na ida e 4 na volta | Imunidade por 5 s; defesa especializada pode zerar ataques pesados/especiais |
| Thor | Martelo 8 e faísca de 2, alcance `WIDTH/3-48` | Raio marcado de 15; dash aéreo de 0,16 s, uma vez por salto |
| Hulk | Palma 6; remove tiros/efeitos adversários não especiais e interrompe ataque próximo | Salto com onda 15; impede pulo após acerto; dano +5 quando HP <=30 |
| Pantera Negra | Investida 2, direção influenciada pela última movimentação | Explosão `min(10,defendedAttacks)*6`; guarda acumula energia |
| Doutor Estranho | Projétil padrão 8 | Portal próximo 10 e teleporte; snapshot ao atingir <=20% e restauração única ao morrer |
| Viúva Negra | Dois disparos de 3 | Arma próximo golpe físico para stun de 2 s; ritmo e física do salto próprios |
| Feiticeira Escarlate | Energia padrão 8 | Projétil aplica distorção por 5 s; surge uma projeção de 20 HP ao ficar com <=30 HP |
| Capitã Marvel | Feixe 7 | Investida Supernova 15; golpes físicos acelerados, poder comum mantém duração base |
| Dr. Doom | Cinco raios verdes de 3, posições e atrasos escalonados | Sentença 20 marcada; escudo automático a cada 7 s absorve um contato |
| Thanos | Energia 9 | Estalo: preparação 1,5 s, encerra alvo com HP=0 se não interrompido; não recebe knockback |

Soco/chute base são 8/10; Doom/Thanos recebem +1. Super Força recebe +2 e mais +2 durante especial; Ninja reduz soco em 1; Hulk em fúria ganha +5. Portanto, não existe uma única tabela 8/8/10 aplicável a todos os poderes atuais. Alguns danos podem ser modificados por defesa, congelamento e escolha narrativa.

Os especiais iniciam indisponíveis: 15 s normalmente, 20 s Thanos, 10 s Estranho/Ninja/Genérico. Capitão/Escarlate/Super Força têm efeito prolongado e redefinem 15 s de recarga ao fim. Ataque ou estado especial pode bloquear outras ações conforme `busy` e `canAct`.

### 8.7 Poderes customizados e adversário genérico

| Skill | Implementação |
|---|---|
| ENERGY | Poder 9 no custom; acertos acumulam `energyHits` até 12; explosão especial até 24 |
| ICE | Poder 7; especial sem dano direto congela por 3 s; deixa gelo no chão por 5 s; congelado recebe `ceil(dano*0.5)` |
| SPEED | Movimento ×1,42; investida comum de dano 2 e especial 15; física/ritmo próprios |
| STRENGTH | Movimento ×0,80, soco/chute com bônus; ataque comum salta e produz onda de dano zero no código atual; especial de 5 s amplia bônus |
| NINJA | Soco 7 com alcance 150, shuriken comum; fumaça/teleporte especial de 10 s de recarga |
| TELEKINESIS | Poder comum 2 e deslocamento do alvo; especial de puxar com dano 3; objetos laterais de dano 5 a cada 15 s |
| GENERIC | Inimigo do mundo: tiro 8; especial dash com recarga de 10 s, sem dano/empurrão, limitado por paredes e adversário |

Nomes legados `Elemental`/`Tecnologia` ainda têm desenho em `AttackEffects.drawCustom`, mas não constam nas opções atuais de `StoryMode.POWERS` e não ganham uma Skill específica em `customSkill`.

### 8.8 Movimento, defesa, colisão e IA

O motor usa arena de largura 844 e bordas 55. Jogador anda a 220 unidades/s antes de modificadores; BOT a 130 × skill × dificuldade. `x` é posição horizontal; `y=0` é chão e valores positivos representam altura. Pulo usa `vy=650`, ou 710 com Super Força, e gravidade base 1900. `step` limita dt a 0,05 s. Movimentação pode atravessar o adversário no ar; `face` atualiza direção automaticamente. Os botões mantêm direção de tela mesmo depois da troca de lado.

`guard` é mantido por toque, interrompe ataque e reduz dano normalmente para 25% arredondado, mínimo 1 em ataques positivos. Cinco segundos de defesa contínua forçam 3 s de bloqueio. Congelamento/distorção desativam defesa. Capitão, Pantera e overrides de efeitos têm regras próprias. Defesa e hit não são meras animações: alteram estado usado em `hurt`.

Projéteis verificam proximidade horizontal e intervalo vertical do corpo (`y+10` a `y+130`, expandido pelo raio). Efeitos usam formas: feixe direcional, raio vertical, onda de chão, área e portal. Projeções podem interceptar contato antes do mestre. A separação física impede sobreposição no chão; Thanos não é deslocado por ela e o oponente é reposicionado. A colisão é geométrica simplificada, não por pixel da ilustração.

IA decide em intervalos `(0.17+random*0.22)/aiRate`. Fácil/Médio/Difícil usam taxas 0,72/1/1,65 e velocidades 0,82/1/1,2. História tem taxas 1,08/1,4/1,9 e velocidades 1,04/1,14/1,3 nas três lutas. O BOT aproxima/recua pela distância, percebe ataques/projéteis, tenta defender/pular, evita golpes marcados no difícil, usa especiais e possui escolhas específicas para Wolverine, Hulk e Thor. A semente injetada permite cenários reproduzíveis nos testes; a Activity usa `System.nanoTime` para variá-la.

### 8.9 Visitar Mundo / Fraturas

É um modo distinto da campanha linear. Tem personagem próprio, 24 objetivos principais M01–M24, 17 encontros principais, encontros opcionais, 12 pistas principais (mais FV6a complementar), 16 aliados acompanhados, confiança 0–2 e três finais. O mapa tem 240 × 156 tiles de 32 unidades: 7680 × 4992. O JSON define 69 NPCs, 71 objetos, 768 props, 644 sólidos e 21 portões. Visibilidade e portões dependem de flags.

O jogador entra por Nome → Aparência → Cor → Poder. Pode escolher custom ou um dos 13 `COMBAT_HEROES`; Deadpool está nos dados como NPC, mas é excluído da seleção e barrado na ponte de combate. Esse seletor não usa os booleans de bloqueio do menu de lutas livres. A ilha tem sprites em quatro direções desenhados por outra cadeia gráfica, `WorldRules.drawSprite → WorldCanvas`, em pixel art.

No frame ativo, direções são normalizadas para que diagonais não sejam mais rápidas; velocidade é 113 unidades/s. NPCs de patrulha usam 18. `WorldMap.move` subdivide deslocamento em passos de até 5 unidades e testa X e Y separadamente, permitindo deslizar em obstáculos. Água, sólidos, portões fechados e atores bloqueiam. Câmera segue com amortecimento exponencial.

Interação não inicia só por proximidade: `updateNearest` escolhe alvo em raio <45, habilita conversar/examinar e o usuário toca no botão ou no ator. Conversa, modal e batalha pendente pausam movimento. Escolhas têm label, resposta e efeitos; `recordChoice` registra índice, tom H/C, relações, flags e notas. Repetir certas conversas permite gestos que completam confiança.

Vitória na arena chama `completeEncounter` e concede flags; perder/cancelar conserva o encontro. A campanha progride por Nova York → Wakanda → Asgard → Encruzilhada/Terra-Esmaecida → Latvéria → Doom → Thanos. Doze NPCs, como Venom, Loki, Hela e Visão, resolvem partes por diálogo/evidências em vez de luta. Isso é controlado por `NARRATIVE_ONLY` e `combatantAllowed`.

O final `restored` exige pelo menos 10 Aliados Plenos, escolha `destiny=0` e pelo menos 10 pistas. `stitched` exige pelo menos 5 aliados quando não satisfaz o anterior; o restante é `patched`. `rememberEnding` mantém um Eco em memória do processo para diálogos futuros, sem salvar permanentemente. Sair da ilha limpa `activeSession`, mas não `ecoMemory`.

Diálogos mencionam apoio de Doom, intervenção a 30% de HP, estabilizador e futuras integrações. Esses textos existem, mas nem todos se convertem em mecânicas: `WorldBattleBridge` não transmite essas condições ao motor. É preciso distinguir consequência narrativa implementada de uma promessa textual.

## 9. Análise dos arquivos Java

Todos os caminhos desta seção partem de `app/src/main/java/com/example/projetomarvel/`. Todos os arquivos pertencem a esse package. As localizações individuais e linhas estão no inventário final.

### 9.1 `MainActivity.java` — coordenação, catálogo, Jarvis, Quiz e navegação

**Tipo:** Activity pública, herda `AppCompatActivity`. **Importância:** crítica. **Telas:** todas as interfaces portrait, incluindo seleção e detalhes. **Quem usa:** Android/manifesto, classes de composição, `ScreenFragment`, testes de fluxo. **Dependências:** AppCompat/Fragment, RecyclerView, recursos XML, Typography, helpers de dados/IA/arte, StoryMode e Intents de batalha. Sua remoção quebra referências Java e remove o ponto de entrada.

#### Declaração, imports e modelo interno

```java
public class MainActivity extends AppCompatActivity {
```

`public` permite que o framework instancie a Activity. `extends` aproveita o ciclo de vida, a janela, layout inflation e o suporte a Fragments. Os imports Android fornecem Views, Bundle e Handler; AndroidX fornece AppCompat/RecyclerView; `org.json`, `java.net`, `java.io` e executores suportam rede/dados. A classe não implementa uma interface de navegação; seus listeners são lambdas e objetos anônimos.

`Hero` é uma classe estática interna com `id`, `name`, `real`, `deck`, `image`, `publisher`, `description`, `powers`. O construtor inicialmente copia deck para description, mas o cliente de catálogo sobrescreve description com o campo efetivamente recebido. `QA` guarda enunciado, array de quatro opções, índice correto e explicação opcional. Não são entidades independentes em arquivos de modelo; `ComicVineClient` e `CatalogStore` referenciam `MainActivity.Hero`, e `QuizGenerator` retorna `MainActivity.QA`.

#### Grupos de atributos

| Grupo | Atributos e razão de existir |
|---|---|
| Chrome/telas | `root`, `body`, `nav`, `content`, `fragmentSurface`, `screen`, `previousScreen`, `navigationInset`; localizam superfícies e controlam retorno/margens |
| Catálogo | `heroes` fonte completa, `visibleHeroes` projeção filtrada, `homeQuery`, `heroList`, `heroAdapter`, `homeCount`, `catalogStatus`, `catalogSyncing`, `catalogError` |
| Imagens/tradução | `imageCache`, `pendingImages`, `translations`, `loadingDetails`; evitam downloads duplicados e repetição de tarefas por personagem |
| Trabalho assíncrono | `main` ligado ao Looper principal, `io` serial, `imageIo` com três threads, `geminiIo` e `quizIo` separados |
| Roster | `roster`/`civilian` alinhados por índice; `playerPick`, `botPick`, `choosingBot`, `playerChosen`, `botChosen`, `battleDifficulty` |
| Quiz | `questions`, `localQuestions`, `previousQuizQuestions`, `qIndex`, `correct`, `gems`, `phaseMisses`, `selected`, `answered`, `quizDone`, `quizLoading`, `quizGeneration`, `quizSessionStarted` |
| Jarvis | `geminiClient`, `jarvisHistory`, `jarvisLastDisplayAt`, `JARVIS_PERSONA`; persona enviada ao serviço, contexto e ordem das respostas |
| Sessão | `static thanosUnlocked`, `static doomUnlocked`; sobrevivem à recriação da Activity enquanto o processo existir |

`final` nos executores/listas impede trocar a referência, mas permite alterar conteúdo. `static` nos desbloqueios os separa da vida da Activity. O código não persiste esses booleans em disco.

#### Métodos de inicialização, tela e visual

| Método | Entrada/retorno | Quando e passo relevante |
|---|---|---|
| `onCreate(Bundle)` | estado Android; void | Inicializa tradutor, cache, perguntas e história, depois Splash |
| `showSplash()` | void | Infla Splash, agenda entrada e toque, termina com Home + catálogo |
| `open(String)` | nome de destino; void | Escolhe background, reseta história ao sair, instala Fragment, cria hierarquia apropriada, renderiza/nav |
| `renderTab()` | void | Limpa filhos e ação flutuante, define barras e delega pelo `screen` |
| `home()` / `fights()` | void | Instanciam os compositores `HomeReference` e `BattleHubReference` |
| `text(String,float,int)` | TextView | Factory: texto, tamanho sp, cor e Barlow |
| `title(String)` | void | Acrescenta título usando Cinzel no Quiz e Bebas em outras telas |
| `section(String,String)` | TextView do valor | Acrescenta heading e conteúdo de detalhes; retorno permite tradução assíncrona |
| `button(String,int)` | Button | Factory com cor via background tint |
| `rounded(int[],int,int,int)` | GradientDrawable | Gradiente, raio, borda; dimensões convertidas por `dp` |
| `styledAction(String,boolean)` | Button | Aparência laranja e habilitação das ações de batalha |
| `roleBadge(String,boolean)` | TextView | Rótulo JOGADOR/BOT e cores diferentes |
| `dp(float)` | int pixels | Multiplica densidade para dimensões de Views |
| `scrollBodyTop()` | void | Se o pai for ScrollView, agenda scroll para origem |
| `installNavInsets()` | void | Listener de WindowInsets ajusta nav, padding inferior e ação fixa pelo delta |
| `makeNav()` | void | Recria cinco itens com ícones/cores, ripple e click → open |
| `accentFor(String)` | int cor | Cor por aba, sem persistência |

Os drawables internos `StarfieldDrawable`, `JarvisBackdrop`, `StoryBackdrop`, `HomeBackdrop`, `BattleBackdrop` e `VersusBackdrop` implementam `draw`, `setAlpha`, `setColorFilter`, `getOpacity`. Desenham gradientes, estrelas, grade ou divisão oblíqua. `StoryBackdrop` lê `storyMode.themeColor`. Seus setters de alpha/filtro estão vazios; a declaração de Drawable não implica suporte efetivo a todos os tratamentos visuais.

#### Métodos do catálogo e detalhes

`loadCatalog()` não retorna uma lista sincronicamente. Ele agenda a tarefa e publica lotes. Primeiro carrega SQLite, cria `seen` com os IDs e publica a coleção. Verifica chave e TTL; tenta sementes por nome apenas em cache vazio e offset zero. Monta fila da editora ou fallback; cada loop calcula fim/offset, tenta rede, salva novos e atualiza UI. Completar persiste data; qualquer exceção torna a sincronização pausada. A importância do método é coordenar disponibilidade imediata do cache com enriquecimento sem bloquear a tela.

`refreshVisibleCatalog()` copia `heroes` dentro de `synchronized`, limpa a projeção e escolhe busca ou catálogo. Usa HashSet para prioridades únicas e comparadores para o restante. Por fim notifica o Adapter, atualiza contagem e controla visibilidade da mensagem. A cópia evita iterar sobre a coleção principal enquanto ela é substituída; o trabalho visual acontece no caminho principal.

`displayName(String)` traduz um conjunto de nomes por `contains`/igualdade, devolvendo o original nos demais. Não faz tradução indiscriminada de identidades civis. `heroColors(String)` escolhe gradientes por nome. `priorityIndex(String,String[])` procura primeiro termo contido, mas não foi identificada chamada ativa relevante dele no fluxo atual.

`details(Hero)` guarda identidade do objeto, origem e abre tela. `detailScreen()` constrói o corpo, placeholders e callbacks. O conjunto `loadingDetails` impede duplicar busca do mesmo ID. O callback compara tela/objeto selecionado antes de atualizar. `translateText(String,Consumer<String>)` devolve vazio para texto vazio; usa cache em memória; pede ML Kit e conserva original em falha. O retorno real ocorre pelo Consumer, não por `return String`.

`loadImage(String,ImageView)` marca URL, tenta LruCache, agrupa Views em `pendingImages`, abre conexão com timeouts 10/15 s, decodifica Bitmap e publica no Handler. No retorno só aplica se a tag ainda corresponder. Não grava bitmaps de rede em SQLite nem em disco explicitamente.

`plainText(String)`, `read(InputStream)` e `parseHeroes(JSONArray)` são utilitários presentes. O fluxo REST atual usa parsing em `ComicVineClient`, portanto não se deve atribuir a esses helpers a responsabilidade ativa principal. `parseHeroes` só deduplica internamente o JSONArray recebido, não reconcilia todo o catálogo.

#### Adapter e eventos de catálogo

`HeroAdapter extends RecyclerView.Adapter<HeroAdapter.Holder>`. `Holder` recupera `hero_card`, `hero_image`, `hero_name`, `hero_real_name`, `hero_deck` com `findViewById` no item inflado. `onCreateViewHolder(ViewGroup,int)` infla `item_hero.xml` sem anexar imediatamente. `onBindViewHolder(Holder,int)` lê `visibleHeroes[position]`, configura tags/textos/gradiente/imagem, pede tradução e click de detalhes. `getItemCount` retorna tamanho da projeção. Reciclagem evita criar uma View para cada personagem simultaneamente, embora todos os objetos do catálogo possam ficar em memória.

#### Métodos do Jarvis

| Método | Parâmetros/retorno | Execução e estado |
|---|---|---|
| `jarvis()` | void | Constrói tela, transcrição e composer; click ignora entrada vazia, consulta configured, confirma envio se remoto |
| `addBubble(LinearLayout,String,boolean)` | destino, conteúdo, se é usuário; void | Cria linha alinhada direita/esquerda, avatar opcional e TextView com largura limitada |
| `sendJarvis(LinearLayout,String,boolean)` | conversa, pergunta, uso de Gemini; void | Mostra pergunta/digitação, classifica assuntos, executa I/O, atualiza histórico remoto e agenda exibição |
| `jarvisFallback(boolean,boolean)` | flags de tema; String | Homem-Aranha tem precedência, depois Quarteto, depois resposta padrão |

`JARVIS_PERSONA` é texto de instrução para o serviço externo. Sua presença não comprova que a API respeita todas as instruções. A classificação local usa substrings em minúsculas; os cinco segundos dependem delas. A Activity não tem um banco geral de conhecimento local para qualquer pergunta Marvel.

#### Métodos do Quiz

| Método | Contrato | Fluxo |
|---|---|---|
| `seedQuestions()` | void | Converte 24 arrays de texto; separa opções pela barra vertical usando expressão regular; converte índice numérico; copia para localQuestions |
| `startQuizGeneration()` | void | Marca sessão, incrementa token, inicia com banco local; se configurado, pede JSON e valida; callback só aceita geração atual |
| `quiz()` | void | Trata loading/final; recupera questão atual; monta score, alternativas, feedback e tray; registra listeners |
| `resetQuiz()` | void | Zera índices/contadores/Joias e gera novamente; não altera desbloqueios |
| `quizCompleteScreen()` | void | Exibe final e listener de REINICIAR |
| `showGemUnlock(int)` | número 1–6; void | Só na tela Quiz, monta overlay, animação e Manopla; continuar remove overlay e reexibe nav |
| `addGemProgress(int)` | quantidade; void | Helper de mini progresso textual presente; não é o tray usado pelo quiz atual |

Trecho central real:

```java
if(qIndex%4==0){gems=qIndex/4;phaseMisses=0;}
if(qIndex==24){quizDone=true;thanosUnlocked=true;}
```

O índice é quantidade de perguntas já ultrapassadas. A Joia é concedida ao sair da quarta resposta confirmada, não a cada clique em alternativa. O teste de dois erros acontece antes do avanço e apaga o desafio inteiro. A conclusão não exige 24 acertos, pois um erro por etapa é permitido.

#### Métodos de batalha e retorno

`characterLockedByQuiz(int)` bloqueia índice 12 quando `!thanosUnlocked`; `characterLockedByStory(int)` bloqueia índice 11 quando `!doomUnlocked`. `pickScreen()` constrói dois passos com preview, cards e ação fixa: impede bloqueado e impede BOT igual ao jogador. Re-renderiza preservando scroll após toque; confirmação do jogador muda `choosingBot`, e confirmação do BOT muda `screen` para Pré-luta.

`preFight()` mostra dois `FighterArtView`, rótulos e LUTAR. Seu listener cria Intent, envia nomes/dificuldade, abre Lutas para deixar a tela de retorno pronta e inicia a arena. `onActivityResult` recebe apenas a campanha linear; o mundo trata seu próprio retorno. `onBackPressed` não depende de back stack de Fragment e decide explicitamente o próximo estado.

#### Por que essa estrutura funciona e quais limites são visíveis

As strings de tela e campos da Activity permitem manter o Quiz ao trocar Views sem reiniciar todos os contadores. Tecnicamente, essa estrutura provavelmente foi utilizada para reproduzir rapidamente um protótipo com composição compartilhada, embora a intenção original do desenvolvedor não possa ser confirmada apenas pelo código. O custo é que os compositores e modelos conhecem a Activity e vários estados distintos convivem na mesma classe. Dados de sessão não salvos em Bundle não são automaticamente restaurados após morte de processo.

### 9.2 `HomeReference.java` — composição da Home

**Tipo:** auxiliar de apresentação, package-private. **Importância:** alta. **Consumidor:** `MainActivity.home`. **Dependências:** atributos/helpers do host, TextWatcher, RecyclerView/LinearLayoutManager. **XML:** superfície comum e item usado pelo Adapter.

O construtor recebe `MainActivity host`. `d` e `t` delegam conversão e criação de texto; `bg` delega gradiente. `render()` usa `host.body` como header e assume que seu pai é uma LinearLayout de página, criada especialmente no ramo Home de `open`. Monta título, subtítulo, contagem, card da campanha e campo de busca. O card chama `host.open("História")`. O `TextWatcher.onTextChanged` grava `homeQuery` e chama `refreshVisibleCatalog`; before/after estão vazios. Depois adiciona status e RecyclerView diretamente na página, com peso 1 para ocupar o restante.

O layout separa header fixo e lista que rola, em vez de colocar o RecyclerView dentro do ScrollView genérico. Remover a classe quebra a chamada de `home`; substituir o contêiner da Home sem revisar seu cast de pai causa falha em runtime.

### 9.3 `ComicVineClient.java` — REST e parsing

**Tipo:** cliente de API, sem Views. **Importância:** alta para dados reais. **Consumidores:** `MainActivity.loadCatalog/detailScreen`. **Dependências:** `HttpURLConnection`, `org.json`, `Html`, `TextUtils`, `MainActivity.Hero`. `apiKey` é instância privada; não há log de URLs/chaves na classe.

| Método/tipo | Parâmetros e retorno | Responsabilidade |
|---|---|---|
| `Page` | characters, returned, total | DTO de página, separa total bruto da coleção filtrada |
| `Details` | description, powers | DTO de detalhes textuais |
| construtor | String apiKey | Guarda a configuração para requisições |
| `fetch(int)` | offset → Page; throws IOException | Varredura geral por offset |
| `fetchNamed(String)` | nome → Page | Requisição da semente prioritária |
| `marvelIds()` | → ArrayList<Integer> | Consulta publisher 4010-31, exige resposta válida e deduplica IDs positivos |
| `fetchIds(List<Integer>,int,int)` | lista e intervalo exclusivo de fim → Page | Concatena IDs com barra vertical, aplica URL-encode e filtra characters |
| `fetchDetails(int)` | ID → Details | Endpoint 4005-ID, prioridade description/deck, truncamento, lista de poderes |
| `fetchPage(int,String)` | offset/nome opcional → Page | Monta filtros e delega |
| `fetchCharacters(String)` | sufixo de filtro → Page | Endpoint, field_list, status, filtro Marvel, URL de imagem e modelos |
| `request(String)` | URL → JSONObject | Timeouts 15/25 s, User-Agent/Accept, exige HTTP 200, lê UTF-8 e disconnect em finally |
| `plain(String)` | HTML → String | Remove marcação, trata null/vazio |

`request` não faz retry sozinho; a política de tentativas pertence à Activity. `fetchCharacters` solicita campos leves; description/powers podem ser processados se presentes, mas não constam desse `field_list`. `fetchDetails` é quem os pede explicitamente. Remover o cliente impede sincronização e detalhes adicionais; dados já salvos ainda poderiam ser exibidos se as chamadas fossem removidas/adaptadas.

### 9.4 `CatalogStore.java` — SQLite

**Tipo:** `public final ... extends SQLiteOpenHelper`. **Importância:** alta para cache persistente. **Consumidores:** MainActivity. **Banco:** `marvel_catalog.db`, versão 2. Tabela `characters`: `id INTEGER PRIMARY KEY`, `name`, `real_name`, `deck`, `image`, `publisher`, `description`, `powers` como TEXT.

`onCreate(SQLiteDatabase)` executa CREATE TABLE. `onUpgrade(db,oldVersion,newVersion)` derruba a tabela e recria: migração destrutiva de cache, não preservação histórica. `save(List<Hero>)` é synchronized, abre transação, converte cada Hero em ContentValues e usa CONFLICT_REPLACE. `setTransactionSuccessful` só ocorre após todos; finally termina a transação. `updateDetails(int,String,String)` atualiza por `id=?`, com argumento separado. `load()` usa Cursor em try-with-resources e devolve ArrayList de Hero com todas as colunas.

Sem esse arquivo, há erro de compilação nos consumidores. Sem o banco em runtime, a classe o recria; perder o arquivo de banco provoca nova sincronização, não perda de progresso da história, que não usa essa tabela. A API pública de cache ainda depende do modelo interno da Activity.

### 9.5 `CharacterTranslator.java` — tradução isolada

**Tipo:** helper público, com interface `Callback.onTranslated(String)`. **Importância:** auxiliar para idioma. Construtor cria TranslatorOptions inglês/português e cliente ML Kit. `translate(String,Callback)` evita chamada para texto vazio, solicita `downloadModelIfNeeded` e depois tradução. Falha em qualquer estágio devolve source. `close()` libera o cliente.

Não usa Azure nem chama um serviço de tradução textual via HTTP escrito no app. Modelos podem precisar de download; seu sucesso não foi verificado nesta análise. O cache das strings traduzidas pertence à MainActivity, não ao helper. Sua remoção quebra compilação; falha do modelo reduz conveniência linguística e conserva conteúdo original.

### 9.6 `GeminiClient.java` — geração remota opcional

**Tipo:** cliente HTTP package-private. **Consumidores:** Jarvis e geração de Quiz. **Configuração:** `BuildConfig.GEMINI_API_KEY`. **Endpoint literal encontrado:** `https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent`. Isso documenta a string usada; não comprova a existência/disponibilidade desse modelo ou a validade do contrato no serviço externo.

`Turn` armazena `role`/`text`; `configured()` verifica chave não vazia. `generate(String instruction,List<Turn> turns,JSONObject responseFormat)` exige configuração, monta systemInstruction e contents, aceita apenas papéis `user`/`model`, usa temperatura 0,55 ou 0,8 quando estruturado e POST JSON. A chave vai no header `x-goog-api-key`. Timeouts: 8/20 s. Status não 2xx vira IOException genérica. Lê candidates[0].content.parts, concatena textos e rejeita resposta vazia. `readLimited(InputStream)` lê blocos de 4096 bytes até limite 256000, UTF-8. `finally` fecha conexão.

Não existe SDK Gemini declarado: a integração é REST manual. Não há streaming ou ferramentas remotas. Quem chama captura exceção e usa alternativa local. Sem a classe, Jarvis/Quiz não compilam; sem rede/chave, os caminhos alternativos existem. Nenhuma chave foi copiada para este documento.

### 9.7 `QuizGenerator.java` — conteúdo e validação de perguntas

**Tipo:** utilitário estático. **Importância:** alta para geração opcional; não controla pontuação. `LEVELS` especifica seis dificuldades. `prompt(Set<String> previous)` pede 24 perguntas em português, variedade temática, versão quando relevante, quatro opções e explicação. Acrescenta até 24 perguntas anteriores para evitar repetição, mas não compara novamente esse conjunto em `parse`.

`responseFormat()` monta JSON schema com `stage`, `question`, `options`, `answerIndex`, `explanation`, campos required e envelope `questions`. `parse(String)` exige 24 itens, etapa `i/4+1`, pergunta única por minúsculas, quatro opções distintas e índice válido. `clean(String,int min,int max)` normaliza whitespace e rejeita comprimentos: pergunta 15–230, opção 1–120, explicação 8–190. Retorna `ArrayList<MainActivity.QA>`.

Os limites não garantem precisão enciclopédica. O schema e o prompt orientam conteúdo, enquanto parse protege formato e tamanho. Remoção quebra geração; o banco local permanece definido em MainActivity, mas exige adaptação das referências para compilar.

### 9.8 `ScreenFragment.java` — superfície de destino

**Tipo:** Fragment público final. **Importância:** alta para navegação portrait. `ARG_SCREEN="screen"`; `create(String)` instancia, cria Bundle e associa argumentos. `onCreateView(LayoutInflater,ViewGroup,Bundle)` infla `fragment_screen.xml`. `getScreen()` devolve argumento ou Home.

Não tem RecyclerView, listeners ou estado de Quiz próprios. Não chama `MainActivity.renderTab` por si. A Activity obtém `fragment_surface` depois de `commitNow` e adiciona filhos. Remover o Fragment ou XML quebra navegação; renomear o ID sem atualizar o host causa referência não encontrada/nula.

### 9.9 `StoryMode.java` — máquina de estados da campanha linear

**Tipo:** controlador package-private, recebe `MainActivity`. **Importância:** alta. **Estado:** `phase`, `color`, `power`, `wins`, `personality[2]`, `choices[2]`, `build`, `skin`, `hair`, `style`, `name`, `selectedHero`. Constantes de opções estão em arrays alinhados. Não contém SharedPreferences de progresso ativo; só limpa o legado no construtor.

| Método | Parâmetros/retorno | Ação e consequência |
|---|---|---|
| construtor | host | Guarda Activity e limpa `story_mode` antigo |
| `accent()` / `themeColor()` | → int | Determinam tema pela fase, cor custom ou herói |
| `protagonist()` | → String | Seleciona um único nome para todas as falas/combates |
| `ally(String)` | interlocutor → String | Evita herói protagonista conversando consigo como aliado |
| `save()` | void | No-op documentado; não persiste nada |
| `d`, `t`, `shape`, `gap` | → medidas/Views/layout | Delegam factories do host |
| `add`, `kicker`, `title`, `card`, `narration` | textos/estilo; void | Padrões visuais reutilizados entre estados |
| `art(String,boolean,boolean)` | legenda, mostrar figura, tema Doom | Monta portal, figura opcional e legenda; custom recebe setPower |
| `appearanceRow(String,String[],int,IntConsumer)` | opções e callback | Seleção atualiza estado, redesenha e restaura posição do ScrollView |
| `appearanceControls()` | void | Apresentação, porte, pele, cabelo |
| `dialog(String,String,boolean)` | falante, texto, self | Monta avatar e balão com lado correspondente |
| `options(String[],String[],int,IntConsumer)` | títulos/subtítulos/seleção | Cria escolhas e chama callback de seleção |
| `header()` / `chapters()` | void | Marca origem/ato e progressão de vitórias |
| `footer(String,boolean,Runnable)` | ação principal | Botão fixo `screen_action`; execução pelo click |
| `secondary(String,Runnable)` | ação auxiliar | Botão no corpo, inclusive Visitar Mundo |
| `next()` / `back()` | void | Incrementam/decrementam fase nos limites e renderizam |
| `choice(int,int)` / `personality(int,int)` | posição/opção | Alteram array, save no-op e render |
| `chooseProtagonist()` | void | Grid custom + 11 heróis, roteia para nome ou personalidade |
| `preBattle(int)` | 0–2 | Mostra adversário definido e chama startFight |
| `startFight(int)` | 0–2 | Intent de campanha com aparência/poder/decisões; request 721 |
| `battleResult(int,boolean)` | resultado | Vitória registra wins, fase 15/20/24 e Doom no terceiro |
| `resetState(int)` | fase inicial | Limpa caracterização/progresso, sem limpar unlock estático |
| `resetForTabExit()` | void | Zera para landing |
| `restart()` | void | Zera para hero_choice e renderiza |
| `render()` | void | Switch de 25 estados; constrói apenas o atual |
| `profile()` | → String | Combina duas respostas em rótulo de personalidade |

`render` começa removendo conteúdo e o botão anterior. A tela de nome usa EditText até 22 caracteres e TextWatcher para habilitar botão. Cor só modifica a seleção/redesenha; o avanço atual depende do footer. Poder monta seis opções; personalidade exige uma opção antes de avançar. O resumo antecede qualquer pergunta do portal. Em `default`, prefixo `pre` é convertido em número e usado em `preBattle`.

`battleResult` não modifica fase numa derrota. O sistema não contém encadeamento automático das três lutas sem narrativa. A ausência dessa classe remove a campanha linear e o acesso pelo botão ao mundo; o código do mundo continua existindo, mas precisaria de outra origem para ser acessado.

### 9.10 `BattleHubReference.java` — guia e dificuldade

**Tipo:** compositor de Lutas. **Importância:** média/alta. `render()` monta título, texto, cards de habilidades, três botões de nível e começar. `addAbilityCards(LinearLayout)` usa matriz de 13 linhas × três descrições para poder normal/especial/característica, em HorizontalScrollView. São textos de apresentação, não configuração do motor. `d` delega dp; `renderAgain` salva scroll, limpa/recria corpo e restaura scroll ao trocar nível.

Começar zera `playerChosen`/`botChosen`, define `choosingBot=false`, coloca `screen="Seleção"` e atualiza nav/corpo. Dificuldade é gravada em `MainActivity.battleDifficulty`. Cards podem descrever recursos com pequenas diferenças em relação à regra efetiva; o motor deve ser consultado para valores. Sem esse arquivo, a chamada `fights()` não compila e a entrada da seleção fica sem composição.

### 9.11 `FightActivity.java` — UI, relógio e tradução do motor para Canvas

**Tipo:** Activity Android pública, sem AppCompat. **Importância:** crítica para combate. **XML:** `activity_fight` IDs `fight_root`, `fight_stage`, `fight_hud`, `fight_controls`. **Chamadores:** batalha livre, StoryMode e WorldBattleBridge. **Dependências:** BattleEngine e tipos Fighter/Projectile, artes, efeitos, Handler e recursos tipográficos.

#### Estado e inicialização

`battle` contém simulação; `playerFighter`, `botFighter` e `projectiles` são referências aos objetos desse motor. `WORLD_W=844`, `WORLD_H=380`, `GROUND_Y=270` definem o espaço de desenho. `active`, `over`, `paused` controlam atualização. Booleans de toque e `previousFrame` controlam entrada/dt. `effectTime`, `damageTime`, `dustTime` são feedbacks visuais. Nomes, poder, aparência, dificuldade e campos world/story vêm do Intent.

`onCreate` chama pai, aplica modo imersivo e landscape, lê todos os extras, monta UI, cria motor e agenda countdown. `worldMode` deriva da presença de `world_encounter`; não depende só de um boolean enviado pelo chamador. `botMax` é previsto por nome para o HUD e depois `updateHealth` sincroniza com maxHp do motor.

#### Construção e controles

`build()` infla layout, monta duas colunas de vida e VS, adiciona `Arena` e status ao stage. “Sair” fica no topo direito. No rodapé, movement reúne Pulo acima de Trás/Frente; attacks reúne Soco topo, Defesa esquerda, Poder direita e Chute abaixo. Medidas são dinâmicas em dp. O grupo de ação tem alpha 0,70. Especial é acrescentado ao `fight_root`; um listener de layout posiciona o botão a partir da posição do Poder. Portanto, não está dentro da cruz original.

`hold(String,int,Runnable,Runnable)` retorna Button circular. Em ACTION_DOWN reduz escala e executa down; UP/CANCEL restaura e executa up. Movimento/defesa dependem de manter toque. Pulo marca solicitação e mantém `jumpHeld`, usado por Homem de Ferro; Trás/Frente também tentam `airDash` de Thor. `attackButton(int,String,int)` usa click para chamar `startAttack` e anima apenas se o motor aceitar. `action`/`label` são factories, não regras de combate. `healthColumn(String,boolean)` cria role, nome, contador e ProgressBar e guarda referências para atualização.

#### Relógio e atualização

```java
float dt = previousFrame == 0 ? 0 : Math.min((now - previousFrame) / 1_000_000_000f, .05f);
```

`System.nanoTime` mede intervalo monotônico. A divisão converte nanos em segundos; o limite evita passo gigante após atraso. O `ticker` roda no Handler principal a cada 16 ms, mas isso não garante 60 FPS. Se ativo e não pausado, chama `step`; nos outros estados reduz timers visuais. Sempre invalida arena e atualiza aparência dos botões. Não há thread separada de simulação.

`resetFight()` cria BattleEngine com nomes, skill custom, dificuldade, fase, decisão e semente. Atualiza referências e zera entradas/contadores. `countdown(int)` exibe 3/2/1 em intervalos de 700 ms e LUTE por 650 ms, depois ativa combate. `step(float)` consome jumpRequested, chama motor com direção da tela e jumpHeld, percorre `battle.hits`, atualiza números/efeitos/status, conta acertos positivos do jogador/aliado e finaliza se o motor acabou.

`startAttack(Fighter,int)` retorna boolean se a luta está ativa e o motor aceitou. `updateHealth` reflete HP/maxHp e cores por porcentagem; `healthColor` usa <=25 vermelho, <=50 amarelo. `updateAttackButtons` altera alpha por prontidão, mostra segundos do lock de guarda e recarga/ATIVO/NO CHÃO do especial. `lastSpecialState` evita reconstruir seu background e texto em todo quadro quando nada mudou.

#### Resultado e ciclo de vida

`finishFight(boolean)` protege contra duplicidade, desativa luta, marca KO/poeira, salva wins/losses apenas em combate livre e agenda resultado após 1100 ms. `showResult(boolean)` monta overlay com título, descrição, HP do jogador, acertos e figura **do jogador** em pose de vitória ou KO. Repetir remove overlay e chama reset/countdown; retornar entrega o resultado ao chamador. O resultado do mundo usa rótulo próprio; História tem continuar/voltar.

`returnToCaller(boolean)` envia extras do modo e termina. Para mundo mantém landscape; para outros solicita portrait. `onBackPressed` equivale a sair com vitória false. `onPause` limpa entradas/guarda e impede avanço da simulação; `onResume` zera previousFrame para evitar salto de tempo. O countdown é agendado pelo mesmo Handler e não contém guarda de paused própria. `onDestroy` remove callbacks. `specialMove(String)` permanece helper nominal, sem papel central na simulação atual; `specialColor` resolve cor custom ou AttackEffects.

#### Classe interna `Arena`

`onDraw(Canvas)` salva matriz, calcula `min(width/WORLD_W,height/WORLD_H)`, centraliza e escala uniformemente. Aplica shake por estado, depois fundo, efeitos de mundo, projéteis, combatentes, projeções e impactos. O motor representa altura positiva para cima; o Canvas usa `GROUND_Y - y`.

`drawBackdrop` desenha gradiente, brilho, nuvens, skyline, torre e chão quadriculado; fase final usa verdes. `drawFighter` escolhe pose na prioridade KO → especial → gelo → dano → ataque → guarda → ar → caminhada → idle. Usa `fighter.dir`, transparência de Ninja/Marvel/poeira e escalas individuais. Custom chama `drawPowered`; generic chama `WorldEnemyArt`; heróis chamam `drawAnimated`. `CombatEffects.drawStatus` acrescenta estados.

`drawProjectiles` escolhe renderer por style e por custom. `drawImpact` desenha raios e número de dano/DEFESA com fade. Essas rotinas não determinam dano nem vitória. Sem FightActivity, os Intents não poderiam abrir a batalha; sem BattleEngine, a Activity não compila; sem os renderers, as chamadas de desenho também quebram compilação.

### 9.12 `BattleEngine.java` — simulação independente de Android

**Tipo:** classe final package-private, Java puro. **Importância:** crítica. **Consumidores:** FightActivity, CombatEffects e testes. **Sem XML/Context:** parâmetros e estado não dependem de tamanho de View. Importa apenas ArrayList e Random.

#### Tipos e estruturas internas

| Tipo | Campos e função |
|---|---|
| `Skill` | 13 heróis, ENERGY/ICE/SPEED/STRENGTH/NINJA/TELEKINESIS, ALLY e GENERIC; define branches de regra |
| `Form` | BEAM, STRIKE, BURST, WAVE, SLASH, PORTAL, SMOKE, TIME, VOLLEY, THOR_SPARK, GREEN_RAY |
| `Fighter` | Nome/skill/custom, posição/velocidade/HP, ataque/guard/cooldown, temporizadores especiais, slow/frozen/distortion, passivas, aliado/mestre e snapshot |
| `Projectile` | Dono, x/y, vx/vy, dano, life, maxDistance, origem, raio, direction/style e flags de slow, gelo, retorno, hammer/special/unblockable |
| `Effect` | Dono/Form, geometria, dano, delay/duration/age, override de dano defendido, resolvido e direção |
| `IcePatch` | Dono, posição, vida de 5 s |
| `Hit` | Alvo/atacante, dano calculado, guarded/blocked; fila de eventos para UI |
| `Snapshot` | Cópias player/bot, tempo e listas de tiros/efeitos/gelo para retrocesso |

`Fighter.copy` cria outro Fighter e chama `copyState`. A cópia do aliado remonta o vínculo `master`; `copyState` copia os muitos atributos explicitamente. `Projectile.copy` e `Effect.copy` preservam referências de dono e copiam seus campos. Isso tem relevância para rewind: não há serialização do motor, só snapshots em memória.

#### Entradas e seleção de comportamento

Construtores recebem nomes, poder custom, flag custom, dificuldade, fase, decisão tecnológica, semente e opcional `genericBot`. Geram jogador x220/BOT x624, Random e orientação. `heroSkill(String)` compara nomes; `customSkill(String)` mapeia seis opções; `powerName(Skill)` faz conversão inversa para custom; `opponent(Fighter)` reconhece quem é mestre de projeção; `root(Fighter)` remete aliado ao principal.

`face()` atualiza dirs conforme posição, se distância >0,1. `busy(Fighter)` restringe controles durante especiais, com exceções para Capitão/Escarlate/Super Força. `canAct` lê over, vida, stun, frozen, exhaustion e busy. `canSpecial` acrescenta ataque inativo, cooldown zerado, nenhum especial ativo e chão para Estranho. São predicados booleanos, não comandos que iniciam ações.

`guard(Fighter,boolean)` ativa/cancela guarda, verifica locks/estados e limpa ataque ao ativar. `jump(Fighter)` só começa no chão, sem distorção/noJump; define vy, hover e libera dash aéreo. `airDash(Fighter,int)` exige Thor no ar, direção não zero, não usado e ação válida; define 0,16 s a 510 unidades/s. `speed` e `pace` retornam multiplicadores distintos: movimento e ritmo de ataque não são a mesma variável.

#### Ataques e especiais

`basicDamage(Fighter,int)` computa físico; `commonDamage(Fighter)` resolve poder por skill; `specialDamage(Fighter,int)` soma modificadores de Hulk/Super Força. Os valores detalhados estão no capítulo 8. Esses métodos não aplicam defesa: o alvo é tratado depois em hurt.

`attack(Fighter,int)` retorna false para slot fora de 0–2, ação bloqueada, cooldown ou ataque ativo. Atualiza orientação, zera tempo/resolução, limpa guarda e quietTime, define duração/recarga e configura trajetórias de Strength/Speed/Panther. `resolveAttack(Fighter,int)` é chamado no tempo certo de animação: slots físicos usam alcance e contato; slot2 despacha garras, palmas, volley, beam, salto, raios ou projétil.

`special(Fighter)` também retorna boolean; só executa quando permitido. Registra início/alvo, define recarga/duração/estágio e cria efeitos/tiros conforme skill. `dashEnd(Fighter,float)` limita avanço a distância desejada, borda e margem de 65 do oponente. `effect(...)` instancia e adiciona Effect. `shoot(...)` cria tiro na mão (`x+dir*48`, `y+78`) e velocidade com direção. O sinal de `dir` é decisivo para render e colisão.

`advanceSpecial(Fighter,float)` progride temporização e fases: interpola dash, salta com seno, resolve golpes em marcos, teleporta, conclui ondas e executa estalo. Estágio impede repetir dano a cada frame. `specialTime >= specialDuration` encerra e faz ajustes de aterrissagem/recarga. A atualização do estalo zera HP diretamente; `hurt` interrompe a preparação de Thanos quando recebe dano positivo.

#### `step(float dt,int movement,boolean jumpHeld,boolean runAI)` em sequência

1. Retorna se over; limita dt a 0–0,05, soma tempo e reduz shake.
2. Guarda `revision`, orienta e reduz timers com `tick`.
3. Executa `ai` se runAI; testes podem desabilitar.
4. `move` aplica velocidade do jogador/BOT, incluindo gelo, dash ou deslocamento telecinético.
5. Avança especial ou física vertical, limita x, separa corpos e reorienta.
6. Para ataques ativos, soma tempo e atualiza trajetórias/dash; resolve uma vez ao atingir fração da duração e encerra o ataque ao fim.
7. Aplica `passives` e `allies`.
8. Envelhece efeitos, resolve ao chegar em delay e remove ao fim. Rewind muda revision, provocando saída segura do passo antigo.
9. Atualiza tiros; sentido aranha reduz passo espacial, mas life diminui pelo dt normal. Testa aliado/alvo, aplica dano/estado, retorno de escudo, fim de martelo/faísca e descarte por alcance/tempo/bordas.
10. Reduz/remove gelo, separa e orienta novamente.

`revision` é um contador usado ao restaurar snapshot para não continuar processando uma lista de eventos anterior à volta no tempo. `hits` não é limpo automaticamente por step; a Activity consome e limpa depois de mostrar feedback.

#### Física e colisões

`tick` reduz cooldown, stun, hitTime, guardTime/lock, noJump, electric, specialCooldown, slow/frozen/distortion/alert. Conta quietTime e walkTime. Guarda contínua chega a 5 s, desliga e bloqueia 3. BOT solta guarda quando guardTime expira.

`move` zera velocidade quando não pode agir ou atacando; mantém última direção útil, suaviza deslizamento em gelo e soma airDash/teleMove. `physics` integra y e gravidade; Iron com pulo mantido perto da queda plana no máximo 1,15 s, se estados permitirem. `separate` mantém gaps 62 ou 110 com aliado e abre exceção de atravessamento para Speed e alturas separadas.

`melee`, `meleeDirectional`, `meleeTouches` tratam alcance direcional e vertical, com possível interceptação por aliado. `projectileContact` e `projectileTouches` escolhem alvo e testam caixa do corpo. `effectTouches` diferencia feixe, coluna, onda e área. Coluna usa distância X; onda exige y<45; isso explica por que pulo desvia de alguns efeitos e não de todos.

#### Dano, passivas, rewind e aliados

`hurt` tem overload com `noKnockback`. Retorna false para luta encerrada/alvo morto, imunidade de especial Aranha/Capitão ou escudo de Doom. Escudo consumido publica Hit blocked de dano zero. Depois aplica escolha tecnológica, gelo e guarda, desconta HP limitado a zero, reseta quietTime, interrompe preparação de Thanos, aplica stun/hitTime/empurrão e cancela ataque. Publica Hit mesmo para contato defendido. Morte de aliado marca KO sem terminar a luta; morte de Estranho com snapshot disponível chama restore; demais mortes encerram com victory comparando o root do atacante.

`snapshot(Fighter)` copia mundo de combate e fixa HP salvo de Estranho em 20% do máximo. `restore(Fighter)` restaura combatentes, tempo, tiros/efeitos/gelo, preserva flags de rewind usado, marca uso e cria TIME visual. A restauração não é uma nova Activity nem reload do Android.

`passives` reinicia quietTime ao agir/defender, cura Wolverine, renova escudo de Doom, lança objetos de telecinese e cria/renova IcePatch. `livingAlly` testa projeção viva; `spawnAlly` cria Projeção do caos, HP20 e master; `allies` só atende Escarlate e gera uma onda ao <=30. Posiciona aliado do lado do mestre, tenta soco em intervalos de 0,35 s e resolve seu dano8. `followPushedAlly` reposiciona mestre quando o empurrão atravessaria o espaço reservado.

#### Inteligência artificial e helpers matemáticos

`aiRate`/`aiSpeed` priorizam fase da história quando >=0 e depois dificuldade. `ai(dt)` reduz aiDecision; ao vencer o prazo escolhe movimento/distância, examina tiros unblockable e efeitos ainda não resolvidos, decide saltar/defender/dash/especial/ataque. Ações ainda passam pelos mesmos predicados e cooldowns. `clamp`, `down`, `smooth`, `lerp` limitam, reduzem timers e interpolam trajetórias. `smooth=t*t*(3-2*t)` suaviza começo/fim; seno descreve arco vertical de ataques roteirizados.

Sem BattleEngine, a UI pode existir em conceito, mas não compila com suas referências atuais e não há regra de dano/movimento/resultado. Sua ausência é simultaneamente falha de compilação e perda funcional central.

### 9.13 `FighterArtView.java` — personagens vetoriais e poses

**Tipo:** View pública final e biblioteca estática de desenho. **Importância:** alta. **Telas:** Splash, seleção, pré-luta, história, guia, batalha e resultado. **Consumidores:** MainActivity, StoryMode, FightActivity, SplashCinematicView, CharacterPortraitView e BattleHubReference. **Dependências:** Canvas, Paint, Path, Color; não infla XML próprio.

As constantes POSE_IDLE/WALK/JUMP/PUNCH/KICK/BLAST/HIT/GUARD/KO/VICTORY/CLASH codificam o movimento. Os tipos privados SPIDER a CUSTOM escolhem peças, cores e proporções. Campos imutáveis `hero`, `mirrored`, `overridePrimary`, `skin`, `build`, `hair`, `style` identificam a figura; `displayPose` e `displayPower` podem ser alterados por setters.

Há quatro construtores encadeados: o mais simples usa cor=-1, skin=-1, build=1, hair/style=0; o mais completo guarda toda a caracterização. `onDraw` calcula margem de no mínimo 10 e 7% do menor lado; usa largura lógica 205 para KO, 130 para outras poses e altura202. A escala é o mínimo entre os encaixes para manter proporção.

| Método | Contrato e funcionamento |
|---|---|
| `setPose(int)` / `setPower(String)` | Alteram apresentação e chamam invalidate |
| `drawPowered(...)` | Desenha rig por drawAnimated e depois equipamentos/símbolos do poder quando skin>=0 e power não null |
| `draw(...)` | Overloads de desenho estático idle com cor opcional |
| `drawAnimated(...)` | Overloads delegam ao completo; recebe Canvas, posição, escala, nome, espelho, cor, pose, fase e aparência |
| `poly(float...)` | Pares de coordenadas → Path fechado |
| `drawCape(...)` | Capas específicas de Thor, Estranho, Doom e Escarlate, com sombra/contorno |
| `drawInsignia(...)` | Emblemas como teia, reator, estrela, raio, colar, diamante e símbolo custom |
| `star(...)` | Path alternando raio externo/interno em dez vértices |
| `drawHeldItem(...)` | Garras, escudo, martelo, bastão, Manopla e magia; escudo cresce em CLASH |
| `drawHead(...)` | Máscara, cabelo, rosto, capuz e olhos; Doom usa ramo frontal simétrico |
| `type(String)` | Mapeia substring/nome para tipo; desconhecido → CUSTOM |
| `shade(int,float)` | Multiplica canais RGB para sombrear |
| `skinColor(int)` | Mapeia três tons de pele |
| `primary`, `secondary`, `accent` | Nome → paleta de corpo/roupa/acento |
| `visualScale(String)` | Hulk .83, Thanos .80, Doom .76, Widow/Scarlet .69 e outros .72 |

O draw completo salva Canvas, traduz para pés e escala uniformemente, espelhando X quando necessário. Primeiro sombra e transformação global; depois capa, pernas, torso, detalhes individuais, emblema, braços/mãos, acessórios e cabeça. Caminhada usa seno de phase*11, pulo recolhe pernas, chute estende perna, soco estende mão e inclina tronco, guarda sobe mãos e arco, vitória ergue braços. CUSTOM escolhe largura por build/style, pele/cabelo e símbolos do poder. A máscara frontal de Doom permanece reconhecível com espelhamento simétrico do rig.

O código atual **não chama `FighterSprites.drawPortrait/drawBattle`**. Assim, a arte ativa é vetorial e animável, embora os PNG permaneçam no projeto. Remover FighterArtView quebra todos os consumidores. O aspecto final não foi comparado visualmente com as imagens nesta tarefa.

### 9.14 `FighterSprites.java` — recorte e desenho de PNG, caminho atualmente sem consumidores

**Tipo:** helper estático package-private. **Importância no caminho ativo:** baixa; recurso legado/disponível. Possui `app` Context estático, HashMap de Bitmap por resource ID e Paint filtrado. Não foi encontrada chamada de inicialização/desenho fora da própria classe na produção atual.

`initialize(Context)` guarda applicationContext. `resource(String)` mapeia os 13 nomes aos PNG `fighter_*`, incluindo substrings de variantes. `available` exige inicialização e ID válido. `bitmap` tenta cache; decodifica sem escala de densidade e com inSampleSize2; recorta fundo; recicla origem; memoriza resultado.

`removeBackdrop(Bitmap,boolean)` lê pixels, escolhe cores de referência na borda/topo, faz flood fill por quatro vizinhos em fila `int[]` e marca fundo transparente. Um segundo percurso de componentes remove ilhas com menos de 32 pixels, como estrelas. Calcula bounding box alfa>16 e recorta, evitando reciclar a mesma instância quando já ocupa tudo. `background` usa distância de cor para casos comuns e limiar RGB específico para fundo de Doom. `distance` soma diferenças absolutas RGB.

`drawPortrait` encaixa com margem e preserva aspect ratio; `drawBust` usa aproximadamente os 54% superiores; `drawBattle` normaliza altura190*scale e aplica KO/dano/ataque/caminhada com transformações simples. Doom não é espelhado nesses métodos. Todos retornam boolean para permitir fallback quando bitmap não existe. Sem inicialização, retornam false.

Excluir esta classe e seus PNG não tem chamada ativa comprovada a quebrar no fluxo atual, mas elimina a implementação de recorte e as referências disponíveis. Sua remoção não foi feita nem é recomendada por esta análise documental.

### 9.15 `CharacterPortraitView.java` — busto circular

**Tipo:** custom View package-private. **Importância:** média. Construtor guarda nome. `onDraw` usa o menor lado, desenha círculo escuro, aplica clip circular, chama `FighterArtView.draw` com translação para mostrar busto e contorna o círculo. Estado é só nome e Paint; sem listeners. Consumidores são os cards da seleção e protagonista StoryMode. Não usa os PNG atualmente. Sem ela, compilações dos seletores falham ou seria preciso trocar os retratos.

### 9.16 `AttackEffects.java` — arte do poder comum

**Tipo:** utilitário estático com construtor privado. **Importância:** alta visual, sem regra de dano. `color(String)` define cor por personagem. `draw(Canvas,float,float,int,String,Paint)` posiciona efeito e espelha por direction: Viúva tem bala curta; Aranha teia circular; Wolverine/Pantera garras; Capitão escudo com estrela; Thor martelo; Hulk arcos; Estranho círculos/runas; demais energia com gradiente.

`drawCustom` recebe power/color e desenha shuriken, floco, onda, trilha de velocidade, telecinese ou energia; ainda trata Elemental/Tecnologia legados. Ambos salvam/restauram Canvas e ajustam Paint. A Activity usa a geometria dos projéteis para chamar; ampliar um raio visual aqui não amplia hitbox no BattleEngine. Sem o arquivo, efeitos comuns não compilam nos consumidores.

### 9.17 `CombatEffects.java` — apresentação de especiais e estados

**Tipo:** utilitário estático, usa tipos do BattleEngine. **Importância:** alta para leitura do combate. Sem XML/listeners. Sua responsabilidade é tornar visível o que o motor já decidiu.

| Método | Uso/retorno |
|---|---|
| `specialPose(Fighter)` | Retorna pose de pulo/chute/guarda/soco/rajada conforme skill e tempo |
| `color(Fighter,int)` | Cor custom ou personagem; Gelo usa ciano |
| `drawWorld(Canvas,BattleEngine,float,int,Paint)` | Gelo e Effects, com aviso durante age<delay e fade depois |
| `drawStatus(Canvas,BattleEngine,Fighter,float,int,Paint)` | Escudo, gelo, locks, eletricidade, dash, charge de estalo, teia do estilingue, fúria, propulsores e contadores |
| `drawAlly(Canvas,Fighter,float,Paint)` | Projeção com animação de soco, movimento e barra própria de HP20 |
| `drawProjectile(Canvas,Projectile,float,Paint)` | Estilos meteor/object/scarlet_special/marvel_beam/tele_special/freeze |
| `smallText(...)` | Texto centralizado com fonte condensada, depois restaura alinhamento |
| `clean(Paint)` | Limpa shader/sombra/alpha/estilo/stroke para não vazar estado entre efeitos |

O renderer usa `ground-e.y`, radius/range, direção, idade e duração do próprio Effect. BEAM desenha aviso fino e feixe espesso; STRIKE/GREEN_RAY marcam coluna; WAVE cresce no chão; BURST cresce circular; TIME/PORTAL formam elipse; SMOKE partículas. O aviso ajuda a entender a janela de reação, mas quem determina contato é `effectTouches`. Remoção quebra compilação/desenho de especiais; não muda intencionalmente regras do motor.

### 9.18 `SplashCinematicView.java` — abertura ativa

**Tipo:** View pública com construtores Context e Context/AttributeSet, permitindo inflação XML. **Importância:** visual alta para abertura. `start` nasce em `onAttachedToWindow` via uptimeMillis. `onDraw` limita t a1,85 s, desenha gradientes, entrada por easing dos dois heróis, feixe e faíscas de choque. Chama `postInvalidateOnAnimation` enquanto t<1,85.

`glow` desenha gradiente radial; `hero` faz camada alfa e chama rig com POSE_CLASH; `segment` normaliza intervalos e `ease` suaviza. Não decide navegação: `MainActivity.showSplash` possui o Handler. O XML não contém TextView com saudação. Remover/renomear essa classe sem mudar XML causa InflateException em runtime; remover o layout quebra `R.layout` em compilação.

### 9.19 `ArcReactorView.java` — componente de abertura disponível, sem referência ativa

**Tipo:** View pública, construtor para XML. `onDraw` cria círculos, arcos, 12 marcas e brilho ciano. `start` cria rotação 0→360 em7200 ms e pulsos X/Y de900 ms com repetição/reverse, coordenados por AnimatorSet. Evita iniciar outra vez se spin não null. `onDetachedFromWindow` cancela animações e limpa campos.

Não foi encontrada instanciação/uso no XML atual; a Splash ativa usa SplashCinematicView. Sem a classe não há perda funcional comprovada do caminho atual, mas perde-se esse componente HUD. O comentário que o associa à abertura não é suficiente para considerá-lo ativo.

### 9.20 `GauntletView.java` — Manopla progressiva

**Tipo:** View package-private. Construtor limita `unlocked` a0–6 e ativa layer software para sombras. `polygon(float...)` cria Path. `onDraw` encaixa espaço128×160, constrói mão dourada por linhas/curvas, bracelete e seis posições de Joia. Índice<unlocked usa cor e brilho; restante escuro. As coordenadas de slots e `STONE_COLORS` estão alinhadas Espaço/Mente/Realidade/Poder/Tempo/Alma.

Não guarda progresso por si; o valor é imutável na instância e vem do Quiz. É usada em overlay de conquista e conclusão, ao contrário do drawable `ic_gauntlet.xml`, que tem as seis Joias estáticas. Remover a View quebra esses usos.

### 9.21 `GemArtView.java` — Joia em três faces

**Tipo:** View package-private. Guarda `color` e Paint. `poly` constrói polígonos, `blend` mistura RGB com branco/preto, e `onDraw` encaixa quadrado lógico100, desenha hexágono facetado, brilho e borda clara. Usada no tray e conquista do Quiz. Não contém unlock, timer ou estado de pergunta. Sem ela, a renderização das Joias não compila nos pontos de uso.

### 9.22 `StoryArtView.java` — portal de fundo

**Tipo:** View package-private com flag doom. `onDraw` desenha gradiente, halo radial, círculos sólidos/tracejados com DashPathEffect e fragmento translúcido. Doom troca azuis por verdes. `StoryMode.art` compõe essa View sob avatar/legenda. Não anima relógio próprio nem processa escolha. Remoção afeta as apresentações da campanha e quebra a chamada do compositor.

### 9.23 `HeroEmblemView.java` — emblemas presentes sem uso ativo encontrado

**Tipo:** View package-private. `hero` é índice no array `inks` de nove cores. `onDraw` normaliza100×100 e escolhe teia, reator, garras, estrela/escudo, raio, gamma, pata, número4 ou círculo místico. `ring`, `line`, `star` reutilizam primitivas.

Não foi encontrada construção atual fora do arquivo; cards ativos usam CharacterPortraitView. Não generalizar seu índice para os13 lutadores: o array tem9 posições e acessa `inks[hero]` diretamente. Sua ausência não tem impacto de fluxo comprovado hoje, mas remove biblioteca de emblemas.

### 9.24 `Typography.java` — fontes compartilhadas

**Tipo:** helper estático package-private. `CACHE` associa nome a Typeface. `font(Context,String)` sincroniza cache, carrega `fonts/<name>.ttf` uma vez e devolve. Wrappers `bebas`, `barlow`, `barlowMedium`, `barlowSemi`, `barlowBold`, `rajdhani`, `rajdhaniSemi`, `rajdhaniBold`, `cinzel` preservam nomes exatos dos assets.

Importância visual alta em quase todas as telas portrait/arena. Sem a classe os consumidores não compilam; sem um TTF chamado, a criação da fonte pode falhar em runtime. O helper não captura exceções de fonte ausente. A exploração usa em várias Views serif/monospace de sistema; não depende inteiramente dessas famílias.

### 9.25 `VisitWorldActivity.java` — exploração e sessões

**Tipo:** Activity pública final, landscape. **Importância:** crítica para Visitar Mundo. Embora tenha poucas linhas físicas, contém cerca de40KB de código denso; a extensão não deve ser avaliada apenas pela contagem de linhas. **XML:** `activity_visit_world`, ID `visit_world_root`. **Dependências:** todos os módulos World, Intent de FightActivity, Canvas, Handler e dados de assets.

#### Estado e criação

`activeSession` e `ecoMemory` são mapas estáticos do processo. `data` é o JSON base; `session` a exploração; `draft` o rascunho do protagonista; `script`/`speaker` a conversa. `rules`, `world`, `art` compõem a lógica. `actors` e `objects` são cópias com coordenadas pixel `px/py`; não são Views por personagem. `held` associa pointer IDs a direção; `keys` registra teclado. `creating`, `modal`, `dialogueOpen`, `battlePending`, `resumed` controlam pausa. `creationStep`, `lineIndex`, `pendingEncounter` e `nearest` localizam o contexto.

`onCreate` lê `visit_world/world-data.json` com stream em blocos8192, decodifica UTF-8 e parse próprio. Erro vira IllegalStateException com mensagem de mundo indisponível. Cria rules/map/art, entrega AssetManager e substitui a função global `rememberEnding` por callback da Activity. Converte NPC x/y tile em `x*32+16`, `y*32+20`, inicia patrolIndex=1. Monta WorldView, HUD e overlay em camadas. Se Bundle/session estática existe, chama enter; caso contrário, renderCreation. O Runnable tick só segue quando resumed.

`renderCreation` constrói overlay com quatro etapas e duas colunas. Passo0 troca custom/marvel; custom exige nome não vazio até22 caracteres; Marvel lista HEROES filtrados por COMBAT_HEROES. Aparência percorre APPEARANCE, cor percorre COLORS, poder percorre POWERS. Para herói, passos intermediários informam preservação do visual/poder. O footer avança creationStep ou cria sessão e entra. Direções de prévia são up/left/down/right.

`character()` chama `rules.run("characterFromDraft",draft)`. `updatePreview` recalcula personagem e textos sem começar narrativa. `newSession(Map)` produz mapas/listas vazios e spawn com direção down, marca campanha fraturas/version1/active, inclui eco anterior e startedAt. `enter(Map,boolean)` rejeita personagem Deadpool, restaura HUD/câmera, aplica storyIdentity aos atores, ajusta figura de casa para alguns protagonistas, valida posição e publica aviso.

#### Movimento e atualização

| Método | Parâmetros/retorno e passo a passo |
|---|---|
| `frame(float,long)` | dt e uptime; se sessão ativa e sem pausa lê held/keys, normaliza vetor, move, patrulha, escolhe alvo, avisa portão; sempre atualiza câmera; HUD a cada400ms e save a cada1500ms |
| `moveNPCs(List<Object>,float)` | Percorre patrulhas; pausa perto do jogador (<65); converte destino tile/pixel, move com colisão a18/s e atualiza direção/andando |
| `paused()` | boolean, OR de criação/modal/diálogo/batalha |
| `visible(List<Object>)` | Nova lista apenas com `rules.isVisible` da sessão |
| `player()` / `flags()` | Retornam mapas internos da sessão |
| `clearInput()` | Limpa held/keys e walking do jogador |
| `changed()` | Salva referência, limpa input, atualiza HUD e minimapa |
| `updateNearest(List<Object>)` | Busca menor distância<45 entre NPCs e objetos visíveis, habilita botão/hint |
| `updateHUD()` | Resolve região, objetivo, descoberta e nota; personaliza nome de casa por herói |
| `toast(String)` | Mostra TextView por4500ms; não é o Toast Android do sistema |

`buildHUD()` cria painel de região/subtítulo/objetivo, Diário/Sair, minimapa clicável, cruz de64dp+gap2 com pointer input, botão Conversar/Examinar, hint e aviso. `d`, `small`, `text`, `bg`, `button`, `column`, `row`, `add`, `place` são factories de medida, estilo e layout; `small` consulta screenHeightDp<500.

#### Conversa, escolha e progressão

`interact()` só abre conversa se não pausado e nearest válido. `openConversation(Map)` limpa entrada, marca NPC conhecido em `met`, vira o NPC ao jogador e escolhe `dialogueFor` ou `objectDialogue`. Começa lineIndex0, response=null e abre o overlay.

`showDialogue()` monta painel inferior rolável, retrato e fala. Se a linha tem opções e a escolha já está salva, recupera resposta pelo índice. Sem resposta, cria dois botões e chama `rules.recordChoice(session,line.id,index,option)`. Depois mostra response; continuar sobe linha e ao fim chama finishDialogue. O painel é limitado a83% da altura em caso de conteúdo grande.

`finishDialogue()` executa callback `onFinish` usando Fn, fecha diálogo, processa `travel` movendo posição para a região de destino e reinicia câmera. Se há encounter, abre preparação; se solved e ainda não celebrated, mostra final. Conversas não são iniciadas automaticamente por atravessar tiles; exigem ação manual.

`showEncounter(Map)` testa `encounterAllowed`, resolve identidade/variante, busca metadados do encontro e monta livro com dois sprites/contexto. “Ir para a luta” chama `launchFight`, que guarda ID em pendingEncounter, marca battlePending, salva e inicia Intent da ponte com request842. `onActivityResult` só completa vitória válida; em Doom agenda a conversa pós-luta, em Thanos mostra final. `encounterConsequence(String)` retorna aviso específico por encontro ou padrão.

#### Modais, saída e memória

`book(String,String)` cria base de modal central com ScrollView e Fechar, marca modal/limpa input. `closeOverlay` remove modal e limpa entrada. `showJournal` lista objetivo, pistas, aliados, notas em ordem inversa e relações. `showMap` cria MapView grande com legenda. `showExit` explica fim da sessão; `showEnding` obtém endingSummary e oferece explorar/voltar. `exit` limpa activeSession e session, e termina Activity. `rememberEnding(Map)` conserva ending, H/C, aliados de relação2, escolhas e nome no ecoMemory.

`save()` apenas atribui `activeSession=session`; não escreve arquivo. `onSaveInstanceState` serializa JSON em Bundle; `onPause` remove tick e mantém sessão. A retomada de uma luta conserva o mundo. Encerrar pelo botão inicia criação nova na próxima entrada. O Eco é memória distinta e só desaparece com limpeza explícita/processo.

#### Classes internas e teclado

`WorldView.zoom()` retorna densidade*2; logicalWidth/Height convertem pixels para unidades do mundo. `onDraw` escala e chama WorldArt com sessão/atores/objetos/alvo; durante criação mostra mundo de fundo sem sessão. `onTouchEvent` converte toque para coordenada mundial, ordena candidatos por profundidade e abre o que está perto e sob a região tocada. Não é pathfinding de toque.

`SpriteView` guarda character/direction, calcula escala por40×46 e chama WorldArt.sprite. `MapView` guarda large e chama mini quando há sessão. `onKeyDown/onKeyUp/keyDirection` mapeiam WASD/setas; E/Enter interage, J diário, M mapa. `onBackPressed` fecha diálogo, modal, criação ou pede saída, nessa prioridade.

Sem a Activity, o botão VISITAR MUNDO aponta para classe ausente ou não registrada, e o modo não abre. As campanhas lineares/livres não precisam do mapa para suas regras, mas FightActivity hoje referencia WorldEnemyArt, portanto remoção em bloco dos módulos exige revisar essa dependência.

### 9.26 `WorldRules.java` — regras compiladas de Fraturas

**Tipo:** classe Java final, com registro de funções em `Scope global`. **Importância:** crítica para exploração. **Tamanho:** mais de2300 linhas, incluindo centenas de comandos gráficos. Comentário informa geração a partir da campanha Fraturas; o arquivo é parte da implementação de produção, portanto não foi descartado como simples build gerado.

O construtor copia `data` para global, chama `installBuiltins` e registra funções/constantes com métodos `install_*`. `run(String,Object...)` obtém a Fn pelo nome e a executa. Cada `install_nome` cria uma closure Java via `fn(global,(scope,args)->{...})`; variáveis locais aparecem como `s19`, `a20` etc. `throw returned(...)` implementa retorno dessas closures e é capturado pelo wrapper em WorldValues. Não há engine JavaScript nem código Web executado em WebView.

#### Identidade, caracterização, dados e visibilidade

| Função registrada / instalador correspondente | Entradas → saída e regra |
|---|---|
| `slotIdentity` | slot → ID; defender=iron, leader=captain, demais conservam |
| `storyIdentity` | NPC, personagem → cópia enriquecida; substitui slots e identifica variante da mesma identidade ou extra; Wolverine usa Terra-Esmaecida |
| `combatantAllowed` | ID → boolean; recusa ausente/provisional/NARRATIVE_ONLY; aceita encontros com herói permitido ou genérico doombot/agent e extras válidos |
| `heroById` | ID → registro de HEROES |
| `defaultDraft` | → mapa custom, nome vazio, heroId captain, aparência/cor/poder padrões |
| `characterFromDraft` | draft → personagem; Marvel copia registro, custom monta identidade/paleta/poder/aparência |
| `npcCharacter` | NPC → personagem desenhável; herói, vilão específico, Loki ou resident |
| `isVisible` | entidade,sessão → boolean; requires, every(when), extra permitido e hideAfter |
| `visibleNPCs` | sessão → NPCS filtrados |

`isVisible` e `walkable` são responsabilidades diferentes: ocultar um NPC depende de história; bloquear um portão depende da colisão e flag no mapa. Não assumir que tirar uma imagem do desenho remove todo sólido.

#### Pistas, confiança e consequências

| Função | Estado lido/alterado |
|---|---|
| `clueCount(s)` | Conta chaves de CLUES com flag, exclui FV6a |
| `fullAllies(s)` | Filtra ALLIES com relationships[id] exatamente2 |
| `trust(s,id,amount=1)` | Soma e limita relação a0–2 |
| `gesture(s,id)` | Executa uma vez por G-id, aumenta confiança e acrescenta nota |
| `collectClue(s,id)` | Só uma vez por flag, acrescenta nota CLUES[id] e refreshProgress |
| `recordChoice(s,id,index,option)` | Evita sobrescrever escolha, conta tone H/C, aplica afinidade/relation/flag/note/effect; retry remove índice para permitir nova tentativa |
| `refreshProgress(s)` | Migra formato storyRevision antigo, converte antigos encontros narrativos, recalcula M01–M24 e portões/resumos R1–R6, adiciona notas de missões concluídas |

R1 exige ao menos3 de FV1–FV5; R2 ao menos2 de FV6–FV8; R3 exige FV9/FV10; R4 FV11; R5 FV12; R6 todos os anteriores. `marketOpen` depende de spider ou widow e M08 de ambos; `asgardOpen` depende de Sif/Frigga; `wingsOpen` pede duas de Gamora/Magneto/Wolverine; `pymOpen` exige FV1–FV8 e duas visitas a Rocket. Essas relações explicam rotas liberadas além do simples avanço numérico de missão.

#### Encontros e objetivos

`encounterAllowed(s,id)` primeiro exige combatantAllowed, depois consulta requisitos por ID. Patrulha exige fragment+venom; defensor exige stationClear; agente exige defender+goblin; Strange exige smallfragment; Panther exige strange; spider/widow exigem panther; extra exige odinAwake. Os demais seguem cadeia de flags. Essa função **não testa diretamente se encounters[id]==completed**. `completeEncounter` contém proteção contra concessão repetida; diálogos de repetição também fazem desvio quando já concluído.

`completeEncounter(s,id)` rejeita requisito ausente com erro, retorna false se já completed, marca encontro e flag, concede ring/stationClear/oscClear/mineClear/armoryClear conforme adversário, marca S12 nos extras e, no Thanos, solved+ending+rememberEnding. Depois recalcula progresso e registra nota. `MISSIONS` contém24 tripletas ID/texto/alvo. `objective(s)` recalcula flags, escolhe primeira M incompleta e ajusta alvo ao subpasso faltante; terminado, aponta dp9. `targetLocation(s)` procura o alvo em NPCS/OBJECTS.

#### Finais e memória

`resolveEnding(s)` aplica condições de restored/stitched/patched do capítulo8. `endingSummary(s)` retorna kind,title,text,epilogue e menciona aliados/Alvarez conforme estado. `breachCast(s)` combina aliados plenos e elenco base, filtra heróis de combate, limita8 e devolve se aparecem plenos ou em silhueta, com informação de variante.

`names` é dicionário de apelidos; `protagonist(s,lines)` seleciona fala específica pelo character.id, depois custom, depois vazio. O Eco anterior aparece em diálogos de Wong, Groot, Deadpool e marcos. Não altera diretamente os desbloqueios do menu livre.

#### Construção de roteiros e diálogos

| Função | Forma retornada e função |
|---|---|
| `line(text)` | `{text}` |
| `talk(text,onFinish)` | `{lines:[line],onFinish}` |
| `opt(label,response,tone,ally,effect)` | Opção com resposta e callback |
| `choice(id,text,a,b)` | Linha com ID e duas opções |
| `done(s,flag)` | Closure que seta flag e chama refreshProgress |
| `remember(s,id,title,text)` | Encaminha nota para builtin addNote |
| `gesturePrompt(...)` | Retorna oportunidade de gesto se requisito e G-id não usado; opção de adiar com retry |
| `repeat(n,s)` | Oferece gestos pendentes ou falas pós-encontro/pós-final |
| `dpDialogue(n,s)` | Nove aparições de Deadpool, links de pistas, Echo e condição de fourthWall |
| `dialogueFor(n,s)` | Resolve storyIdentity, obtém narrativa e antepõe apresentação da variante |
| `narrativeDialogueFor(n,s)` | Roteador por ID e estado; residentes/aliados/objetos narrativos, pós-Doom, extras e encontros |
| `blockedText(id,s)` | Mensagem do pré-requisito faltante |
| `narrativeScene(n,s)` | Cenas dos12 NARRATIVE_ONLY, com onFinish de flags e notas, sem batalha |
| `encounterScript(n,s)` | Contexto, decisões H/C, falas de protagonista/Eco e campo encounter para arena |
| `objectDialogue(o,s)` | Viagem, placas, pistas, pickups, fragmento/equipamento, transmissão, flor, ritmo, mural, planta e núcleo |

No roteador, Rosa/Marcus iniciam a investigação e entregas; home varia por protagonista; Alvarez fornece confiança/atalho; Shuri libera cartão; Banner fornece notas/ressonância; Sif/Frigga/Eir/Odin constroem acesso Asgard; Rocket/Pym coordenam ramo de investigação; Cynthia/Boris aprofundam Doom. Venom/Duende/Killmonger/Loki/Hela/Star-Lord/Gamora/Magneto/Visão/Ultron/Antman/Gavião não se tornam automaticamente combates por aparecerem no mundo.

Os callbacks fecham sobre `s`/`f`; ao executar uma opção no Android, alteram a mesma sessão. `recordChoice` é a fronteira que torna a decisão efetiva; mostrar somente o texto não concede flags. O puzzle rhythm avança DÓ, MI, SOL em visitas/estado de script; opções erradas têm retry.

#### Desenho compilado no mesmo arquivo

`OUTLINE` guarda cor de contorno; `shade(hex,amount)` multiplica RGB e retorna texto rgb(...). `drawSprite(ctx,character,x,y,direction,time,walking,scale)` usa retângulos/polígonos, pose por fase0–3, respiração e blink. Side/back mudam olhos/costas e left espelha. Diferencia dimensões de Hulk/Thanos e custom, capas, armas, teias, máscaras, roupas e poderes. Inclui residentes e vilões narrativos, além dos13 combatentes.

`NPC_PHASE(id)` calcula fase a partir da soma de char codes/7. `PAL` mapeia13 tipos de terreno a cores. `draw_tile(ctx,tx,ty,x,y)` consulta WorldMap.at/region e usa ruído determinístico para água, areia, grama, caminhos de cada reino, cidade, ponte/Bifrost, ruínas e pisos especiais. Verifica vizinhos para bordas de caminhos/ponte. É o fallback quando uma imagem de chunk não abre.

`draw_prop(ctx,p,time)` escolhe geometria de police-tape, swing, train, bamboo, mirror, dead-tree, lantern, steam, rail, star, aurora, rune, fissure, orb, pillar, castle/nucleus/wall/tent/station/cantina/osc, tree, rock, crystal, lamp, column, boat, fountain, bench, bollard, car, palácios e prédios. Formas repetidas usam loops de janelas/tijolos/folhagens. Props dinâmicos recebem time continuamente; estáticos são cacheados em WorldArt.

`draw_object(ctx,o,time,session)` desenha sombra, portal quando travel existe, placa/porta por kind e pedestal/objeto restante com brilho periódico. A lógica de coleta não está nesse desenho, mas em objectDialogue e callbacks.

Sem WorldRules, deixam de existir caracterização do mundo, NPCs desenháveis, diálogos, regras, objetivo e finais. Embora parte do terreno esteja pré-renderizada, a exploração não funciona só com seus PNG.

### 9.27 `WorldValues.java` — valores dinâmicos e closures nativas

**Tipo:** utilitário Java puro, importado estaticamente por WorldRules e módulos. **Importância:** crítica para regras compiladas. Não acessa Android/XML.

`NativeObject` define `get`, `put`, `method` para wrappers como WorldMap/WorldCanvas. `Fn` representa função com `Object[]`; `Body` executa com Scope; `Scope` tem parent e LinkedHashMap vars. `get` sobe para pai; `let` define local; `set` encontra a variável no escopo pai ou define na raiz. `Return`, `Break`, `Continue` são exceções sem stack trace usadas como controle de fluxo das regras; não representam necessariamente falhas do aplicativo.

| Grupo de métodos | Função concreta |
|---|---|
| `fn`, `call`, `returned` | Criam closure com novo Scope, executam Fn e convertem sinal de retorno em valor |
| `error`, `always`, `discard` | Constroem erro, permitem branches gerados e descartam expressão usada como statement |
| `argument`, `coalesce` | Acessam argumento faltante como null e avaliam fallback preguiçoso |
| `Spread`, `Pair`, `spread`, `kv`, `args`, `arr`, `obj` | Montam arrays/mapas, expandem valores e pares, preservam ordem |
| `list`, `map`, `number`, `integer`, `str`, `truth`, `equal` | Conversões; null numérico vira0, parse inválido NaN, string vazia é false, números comparados numericamente |
| `and`, `or` | Curto-circuito com Supplier e retorno do próprio valor, não apenas boolean |
| `get`, `put`, `remove`, `update` | Acesso dinâmico a Map/List/String/NativeObject, índices/length e incremento |
| `unary`, `binary` | Operadores lógicos/aritméticos/comparação/bitwise implementados pelo Java |
| `construct` | Cria Set ou Error dos nomes suportados |
| `method` | Despacha métodos de string/lista/NativeObject: includes, push/unshift, join/slice, find/some/every/map/filter/forEach/reduce |
| `installBuiltins` | Boolean, Math, Object, Array, parseInt, noise, addNote e rememberEnding padrão |

O builtin `addNote` deduplica por id. `rememberEnding` inicial não faz nada e é substituído na Activity. A sintaxe lembra operações de outra linguagem, mas trata-se de Java compilado chamando interfaces Java. Não é um interpretador geral que recebe e executa scripts arbitrários. Operação não suportada em `method` lança IllegalStateException; vários helpers devolvem null/mapa/lista vazia para tipos inesperados.

### 9.28 `WorldJson.java` — serialização de mapas

**Tipo:** parser/writer Java puro. **Importância:** alta para asset e sessão/Bundle. `parse(String)` chama `Reader.value`; `write(Object)` monta StringBuilder e `append`. Writer trata null, boolean/número, Map, Collection e string com escapes. Ao serializar mapas ignora valores Fn; não consegue guardar closures no Bundle.

`Reader` guarda source e cursor i; `skip` pula whitespace; `take` avança caractere; `value` reconhece objeto, array, string, true/false/null e número; `string` lê aspas e escapes, inclusive unicode. Números são Double. Não é uma biblioteca com validação extensa de esquema ou limites de profundidade; JSON malformado pode provocar exceção de índice/parse. O asset é entrada local controlada; a sessão preserva o formato construído pelo módulo.

Sem o arquivo, Activity/arte/testes não compilam. Não confundir esse parser com org.json usado nas APIs de rede.

### 9.29 `WorldMap.java` — geometria, colisão e câmera

**Tipo:** modelo/serviço de mapa, `implements NativeObject`. **Importância:** crítica para exploração. O construtor extrai cols/rows, calcula width/height usando tile32, converte tiles para byte[], guarda regiões/NPCs/objetos/props/sólidos/portões, pré-calcula região por tile e acrescenta pequenos sólidos dos objetos. `tileRegions` agiliza minimapa.

| Método | Parâmetros/retorno e lógica |
|---|---|
| `at(int,int)` | Tipo de tile, 0 fora dos limites |
| `region(double,double)` | Pixel → região; percorre regiões de trás para frente e usa mais próxima se fora de qualquer retângulo |
| `byId(String)` | Região ou null |
| `overlap(Object,double,double,double)` | Colisão retângulo × raio/caixa do ator |
| `walkable(double,double,double,Map,List,String)` | Testa quatro cantos na água, sólidos, portões cuja flag ainda é falsa e NPCs exceto ignore |
| `move(Map,double,double,Map,List,String,double)` | Subdivide max(dx,dy)/5 e move X/Y separadamente se walkable |
| `camera(Map,float,float,float)` | Alvo central com y em52%, limites e suavização `1-exp(-dt*9)`; primeira posição é direta |
| `get(String)` | Expõe cols/rows/width/height às funções |
| `put(String,Object)` | Retorna valor sem modificar atributos fixos |
| `method(String,Object[])` | Expõe at/region ao NativeObject |

Os atores usam coordenadas em pixels do mundo; regiões/tile grid são unidades tile. Remoção causa perda de limites/colisão/câmera e compilação dos consumidores.

### 9.30 `WorldArt.java` — renderização por camadas e caches

**Tipo:** renderer Android, recebe WorldRules e WorldMap. **Importância:** crítica visual para exploração. O construtor registra noise, `this.map` e enum de TILE no global. Assets são atribuídos pela Activity. Paint não filtra bitmap para manter pixel art.

`chunk(int,int)` consulta LruCache de12 chunks por chave x,y. Tenta abrir `terrain/x-y.png`; falha gera Bitmap512×512 RGB_565 desenhando16×16 tiles via regras. `sprite(Canvas,Object,float,float,String,double,boolean,float)` quantiza caminhada em4 frames, blink/breath em bits e cria chave com JSON do personagem+direção+fase. Cache de180 bitmaps40×46 evita redesenhar o mesmo frame constantemente. `prop(Canvas,Object,double)` desenha steam/aurora/fissure/orb/fountain dinamicamente; demais renderiza uma vez em bitmap com margens e cache por byte count de20MB.

`render` desenha chunks visíveis, detalhes de água e uma lista de camadas ordenada por depth: props, objetos, NPCs e jogador. Faz culling para itens fora da câmera, sobrepõe portões fechados, efeitos de flags, atores da brecha e marcadores de objetivo/proximidade. Jogador/NPC têm escala1,2; orientação e walking vêm da sessão. `rect` é primitiva; `terrainNoise` e wrapper noise geram variação determinística. `mini` projeta grade em w/h, escurece áreas não descobertas, mostra rótulos no mapa grande, objetivo e jogador.

A ordenação por pés (`py`/y) dá sensação de profundidade na exploração, diferente da arena de câmera fixa. Não se instancia uma View para cada tile/NPC. Sem WorldArt, o modelo narrativo pode existir, mas mapa/sprites/HUD gráfico não desenham e Activity não compila.

### 9.31 `WorldCanvas.java` — adaptador gráfico

**Tipo:** `NativeObject` que encapsula Canvas. **Importância:** alta. `styles` armazena fillStyle,font,textAlign,globalAlpha e `stack` copia estilos ao save. Paint tem antialias/filterBitmap false. `get`/`put` leem/escrevem estilos.

`color(String)` aceita rgb(...), `#RRGGBBAA` convertendo alfa para posição Android, e Color.parseColor; inválido retorna branco. `ink` aplica cor×globalAlpha e fill. `text` extrai tamanho px via regex, escolhe serif/monospace, peso bold e alinhamento. `n` converte argumento em float.

`method` suporta save/restore/translate/scale, fillRect, beginPath/moveTo/lineTo/closePath/fill, fillText e measureText. Retângulo é posição+dimensão; Android recebe coordenada final somada. Despacho desconhecido gera IllegalStateException. `restore` recupera matriz do Canvas e estilo, para manter resultado consistente entre funções. Sem ele, comandos compilados das regras não chegam às APIs gráficas nativas.

### 9.32 `WorldBattleBridge.java` — contrato exploração → arena

**Tipo:** utilitário estático. **Importância:** alta para encontros. `intent(Context,Map session,Map npc,WorldRules)` valida personagem de combate e NPC permitido, resolve `npcCharacter` e cria Intent para FightActivity. Herói é custom apenas se id="custom". Ajusta nome de Doom e preserva nomes exatos Thor/Hulk para o motor; envia world_encounter/label/generic/kind, difficulty1 e dados custom quando aplicável.

O módulo não altera flags da campanha: preparar Intent não significa vencer encontro. Não envia todas as flags/relacionamentos para o motor. Não consulta unlocks da MainActivity. Isso mantém a progressão da ilha independente do roster desbloqueado livre, conforme o código. Sem a ponte o modo pode conversar, mas launchFight não compila/abre combate.

### 9.33 `WorldEnemyArt.java` — inimigos genéricos da ilha na arena

**Tipo:** helper Canvas estático. **Importância:** média visual. `draw(Canvas,x,feet,mirror,kind,pose,phase)` usa escala .72, postura, caminhada, golpe/guarda e KO. Diferencia doombot robótico de agente por cores/máscara/equipamentos; `box` desenha retângulo e `limb` membro com stroke arredondado. Não usa nome de herói para simular um inimigo genérico.

`FightActivity.Arena.drawFighter` chama esse renderer para Skill.GENERIC; não é a arte pixel do NPC no mapa. Dano e dash ficam em BattleEngine. Remover quebra compilação da arena e perde aparência desses adversários.

## 10. Layouts XML

Os seis arquivos ficam em `app/src/main/res/layout/`. O XML declara a estrutura estável mínima; Java acrescenta a maior parte do conteúdo. Não há arquivo `fragment_home.xml`, `fragment_quiz.xml` ou menu XML de navegação no projeto analisado.

### 10.1 `activity_main.xml`

```text
FrameLayout #main, match_parent, background #0C0D12
├── FrameLayout #screen_container, match_parent
└── LinearLayout #bottom_navigation
    68dp de altura, horizontal, bottom, margens laterais14dp/inferior28dp, elevation12dp
```

**Tela:** todas as telas portrait depois da Splash. **Java:** `MainActivity.open` recupera `main`, `screen_container`, `bottom_navigation`. O primeiro abriga o Fragment; a segunda camada é a barra inferior acima do conteúdo. `makeNav` cria seus cinco itens, background/ripple e listeners. `installNavInsets` ajusta margem inferior somando navegação do sistema, evitando o botão de gestos ou três botões.

A altura da barra e seus ícones são independentes do número de personagens. O contêiner ocupa todo o espaço; o corpo usa padding inferior para não ficar escondido pela barra flutuante. Remover o XML provoca erro em `R.layout.activity_main`; remover um dos IDs quebra compilação ou a obtenção da View. Trocar tipos sem adaptar casts pode produzir ClassCastException.

### 10.2 `fragment_screen.xml`

```text
FrameLayout #fragment_surface, match_parent, fundo transparente
```

`ScreenFragment.onCreateView` infla esse recurso. `MainActivity.open` obtém `fragment_surface` e insere: página vertical na Home, corpo direto no Jarvis, toolbar+ScrollView nos detalhes ou ScrollView genérico nos demais destinos. `StoryMode.footer` e botões de pré-luta/seleção são adicionados como irmãos em posições fixas sobre o corpo.

O Fragment não tem seus próprios textos ou botões no XML. A tag `screen_action` pertence às Views criadas por Java, não a um ID XML. O reset dessa ação remove o botão anterior ao renderizar nova fase. Sem o arquivo, não existe superfície padrão para os destinos.

### 10.3 `item_hero.xml`

```text
LinearLayout #hero_card, horizontal, altura wrap, minHeight150dp, marginBottom13dp
├── LinearLayout vertical, width0 + weight1
│   ├── TextView #hero_real_name, 1 linha, 12sp
│   ├── TextView #hero_name, até2 linhas, 25sp/bold
│   └── TextView #hero_deck, até3 linhas, 13sp
└── ImageView #hero_image, 112×150dp, centerCrop
```

**Consumidor:** HeroAdapter.Holder/`onCreateViewHolder`. **Finalidade:** mesma estrutura para qualquer registro da Comic Vine. O bind preenche textos/imagem, limita resumo, escolhe gradiente e instala click. `clipToOutline=true` permite recorte compatível com background arredondado definido em Java. Ellipsize end e maxLines limitam expansão visual. `character_image` fornece descrição acessível.

`centerCrop` pode recortar a foto do catálogo para preencher área; essa decisão é independente da escala proporcional dos lutadores na arena. Não significa que a imagem inteira de todo personagem será visível na Home. Sem esse XML/IDs o Adapter não consegue criar/bindar linhas, mesmo que a API e o banco funcionem.

### 10.4 `activity_splash.xml`

```text
FrameLayout #splash_root, fundo #030609
└── com.example.projetomarvel.SplashCinematicView, match_parent
```

Nome completo da classe permite LayoutInflater criar a custom View pelo construtor Context/AttributeSet. `MainActivity.showSplash` recupera a raiz para listener e fade. O tempo da Activity é1850ms antes do fade; a View desenha até1,85s. Não há saudação nem ArcReactorView neste arquivo atual. Perder a classe gráfica causa falha de inflação; perder layout causa falha de referência de recurso.

### 10.5 `activity_fight.xml`

```text
FrameLayout #fight_root, match_parent, background #17111D
├── FrameLayout #fight_stage, match_parent
├── LinearLayout #fight_hud, topo, altura65dp, horizontal
│   fundo #DB25142E, paddingStart/End14dp
└── LinearLayout #fight_controls, rodapé, altura166dp, horizontal
    paddingStart/End8dp, bottom5dp, @drawable/bg_fight_controls
```

`FightActivity.build` acrescenta Arena/status/saída no stage, HUD nas colunas, grupos de botões nos controls e Especial na raiz. O desenho ocupa a tela inteira, enquanto HUD/controles se sobrepõem. `bg_fight_controls` é transparente; o fundo não esconde a arena por um painel opaco. Os controles não são Buttons declarados em XML; buscar um ID como btnPunch não funciona nesta implementação.

As medidas de HUD/controles são dp da tela; a simulação usa844×380. A uniformidade do Canvas vem de `Arena.onDraw`, não dos atributos desse layout. Sem a raiz ou IDs, build não localiza suas superfícies.

### 10.6 `activity_visit_world.xml`

```text
FrameLayout #visit_world_root, match_parent, background #132831, keepScreenOn=true
```

VisitWorldActivity cria três camadas: WorldView, hud e overlay. `keepScreenOn` evita desligamento automático enquanto a janela estiver visível. O próprio XML não contém pad, mapa, diálogo nem personagem; são composição Java e Canvas. A configuração landscape está no Manifest/Activity. Sem o XML, a Activity não infla sua superfície.

## 11. Recursos visuais, dados e assets

### 11.1 Drawables XML e animações

| Arquivo em `res` | Conteúdo | Consumidor/estado |
|---|---|---|
| `drawable/bg_fight_controls.xml` | Shape com solid totalmente transparente | activity_fight: área de controle |
| `drawable/ic_gem_lock.xml` | Vector de cadeado, stroke roxo | MainActivity.quiz, slots ainda bloqueados |
| `drawable/ic_gauntlet.xml` | Vector da mão e seis gemas já coloridas | Não há referência ativa encontrada; Quiz usa GauntletView progressiva |
| `drawable/ic_nav_home.xml` | Contorno de edifício | makeNav, aba Home |
| `drawable/ic_nav_jarvis.xml` | Capacete/assistente tecnológico | makeNav e header Jarvis |
| `drawable/ic_nav_quiz.xml` | Mão/Manopla estilizada | makeNav |
| `drawable/ic_nav_story.xml` | Cubo de seis faces/linhas | makeNav |
| `drawable/ic_nav_fight.xml` | Dois lutadores e brilho | makeNav |
| `drawable/ic_launcher_background.xml` | Fundo circular verde/escuro | Ícones adaptativos |
| `drawable/ic_launcher_foreground.xml` | Capuz e máscara frontal de Doom, aço/verde | Ícones adaptativos e monochrome |
| `anim/screen_enter.xml` | translate12%p→0 e alpha.35→1,260ms | Transação de ScreenFragment |
| `anim/screen_exit.xml` | translate0→-6%p e alpha1→.7,220ms | Mesma transação |

`vector` usa viewport para coordenadas lógicas e `pathData` para geometria escalável; o Java tinge ícones de navegação por `setColorFilter`. Não há selectors/animator XML próprios encontrados: estados de botão são em geral definidos por Java com GradientDrawable, alpha, enabled e animação de propriedades.

### 11.2 Imagens `drawable-nodpi/fighter_*.png`

São13 imagens de referência, nomes listados abaixo. `nodpi` impede ajuste automático por densidade. A correspondência está em `FighterSprites.resource`, mas o fluxo de combate atual não chama essa classe.

| Arquivo | Identidade mapeada |
|---|---|
| `fighter_spider_man.png` | Homem-Aranha |
| `fighter_iron_man.png` | Homem de Ferro |
| `fighter_wolverine.png` | Wolverine |
| `fighter_captain_america.png` | Capitão América |
| `fighter_thor.png` | Thor |
| `fighter_hulk.png` | Hulk |
| `fighter_black_panther.png` | Pantera Negra |
| `fighter_doctor_strange.png` | Doutor Estranho |
| `fighter_black_widow.png` | Viúva Negra |
| `fighter_scarlet_witch.png` | Feiticeira Escarlate |
| `fighter_captain_marvel.png` | Capitã Marvel |
| `fighter_doctor_doom.png` | Dr. Doom |
| `fighter_thanos.png` | Thanos |

Essas imagens não são as fotos da Comic Vine, que são baixadas por URL e mantidas no imageCache. Também não são os sprites pixel da ilha, que são rasterizados a partir de comandos Java.

### 11.3 `assets/fonts`

| Família | Arquivos | Wrapper de uso |
|---|---|---|
| Barlow | `Barlow-Regular.ttf`, `Barlow-Medium.ttf`, `Barlow-SemiBold.ttf`, `Barlow-Bold.ttf` | Textos e botões |
| Bebas Neue | `BebasNeue-Regular.ttf` | Títulos e nomes de batalha |
| Rajdhani | `Rajdhani-Medium.ttf`, `Rajdhani-SemiBold.ttf`, `Rajdhani-Bold.ttf` | Jarvis, kickers e HUD tecnológico |
| Cinzel | `Cinzel.ttf` | Título/pergunta do Quiz |

As licenças `OFL-barlow.txt`, `OFL-bebasneue.txt`, `OFL-cinzel.txt`, `OFL-rajdhani.txt` registram SIL Open Font License 1.1 e autores. Elas não são código executado, mas integram a distribuição/licenciamento das fontes. Não foi feita análise jurídica da licença; a função técnica observável é acompanhar as famílias redistribuídas.

### 11.4 `assets/visit_world/world-data.json`

É a base local de Fraturas, não substituto de catálogo Comic Vine. `VisitWorldActivity.onCreate` carrega o arquivo inteiro e WorldJson o transforma em mapas/listas. Suas principais chaves:

| Chave | Quantidade/estrutura | Consumidor |
|---|---|---|
| `WORLD` | cols240, rows156, tile32, spawn x656/y4656 | Nova sessão e dimensões |
| `REGIONS` |29, com id/name/subtitle/realm/x/y/w/h/color | WorldMap.region/byId e minimapa |
| `NPCS` |69, com identidade, posição tile, role, hero/kind e condições opcionais | Atores, patrulha, narrativa, desenho |
| `OBJECTS` |71, com id/name/loc/x/y/kind e opções como travel/requires | Interação e colisões locais |
| `HEROES` |14, incluindo Deadpool NPC | Paletas/identidades em characterFromDraft/npcCharacter |
| `COMBAT_HEROES` |13 IDs | Seleção e validação de protagonistas/encontros |
| `NARRATIVE_ONLY` |12 IDs | Resolve por cenas em vez de luta |
| `ENCOUNTERS` |17 | Ordem/contexto/marcos de confrontos principais |
| `CLUES` |13 entradas, FV1–FV12 e FV6a | Pistas e notas, FV6a não conta no total12 |
| `ALLIES` |16 IDs | Contagem de Aliados Plenos e finais |
| `AFFINITIES` |17 pares ID→H/C | Ganho de confiança por escolha de tom |
| `APPEARANCE` |style/build/skin/hair, três opções cada | Criação custom |
| `COLORS` |7 pares nome/cor | Roupa e identidade custom |
| `POWERS` |6 registros name/icon/detail | Caracterização de poder |
| `LINKS` |24 ligações região/região/flag | Dados de conectividade presentes; colisão ativa usa map.gates já calculados |
| `map` |cols/rows/tile/tiles/props/solids/gates | Geometria final:37440 tiles,768 props,644 sólidos,21 gates |

As posições dos NPCs/objetos são em tile e depois enriquecidas por px/py. Os gates já trazem retângulo em pixels e flag exigida; exemplos: M01 abre NY-01→NY-02; fragment abre NY-02→NY-03. A flag importa para colisão, não só aparência. `LINKS` não tem processamento direto encontrado nos módulos de produção atual, pois geometria/portões já estão em `map`.

#### Regiões reais

| ID | Nome | Reino |
|---|---|---|
| NY-01 | Queens · Bodega de Rosa | ny |
| NY-02 | Central Park · Fissura | ny |
| NY-03 | Metrô 8th Ave | ny |
| NY-04 | Torre Stark | ny |
| NY-05 | Cobertura da Oscorp | ny |
| NY-06 | Sanctum Sanctorum | ny |
| WK-01 | Fronteira de Wakanda | wakanda |
| WK-02 | Birnin Zana | wakanda |
| WK-03 | Laboratório de Shuri | wakanda |
| WK-04 | Mina de Vibranium | wakanda |
| WK-05 | Câmara de Ressonância | wakanda |
| BR-01 | Ponte Arco-Íris Partida | bifrost |
| BR-02 | Observatório de Heimdall | bifrost |
| AS-01 | Cidade Dourada | asgard |
| AS-02 | Salão de Odin | asgard |
| AS-03 | Biblioteca Real | asgard |
| AS-04 | Cripta de Asgard | asgard |
| AS-05 | Arena Dourada | asgard |
| EN-01 | Praça da Encruzilhada | crossroads |
| EN-02 | Cantina Ponto Cego | crossroads |
| EN-03 | Setor dos Refugiados | crossroads |
| EN-04 | Terra-Esmaecida | faded |
| EN-05 | Torre de Vigília | crossroads |
| EN-06 | Estação Pym | crossroads |
| LV-01 | Muralha Esmagada | latveria |
| LV-02 | Cidade Espelho | latveria |
| LV-03 | Castelo · Salão de Armas | latveria |
| LV-04 | Laboratório dos Pilares | latveria |
| LV-05 | Núcleo de Convergência | latveria |

#### Encontros principais e progressão real

| Ordem | ID | Participação na progressão |
|---|---|---|
|1|station-guard|Doombot da Fissura no metrô|
|2|defender|Teste do defensor tecnológico/Stark|
|3|osc-guards|Agente que protege antena e contrato|
|4|strange|Espelho/identidade, Marco I|
|5|panther|Desafio da fronteira|
|6|spider|Contenção do portador|
|7|widow|Ordem falsa/Fury|
|8|mine-guard|Doombot da extração|
|9|hulk|Ressonância, Marco II|
|10|marvel|Quarentena de Asgard|
|11|thor|Coração do Bifrost, Marco III|
|12|wolverine|Trauma da Terra-Esmaecida|
|13|leader|Capitão/resistência, Marco IV|
|14|armory-guard|Doombot de Convergência|
|15|scarlet|Colar de Domínio de Wanda|
|16|doom|Destino e fusão de mundos, Marco V|
|17|thanos|Reversão do Núcleo, FINAL|

#### Pistas e confiança

FV1 runa de metal; FV2 quatro vozes; FV3 sinal que acompanha o portador; FV4 contrato V. von D.; FV5 convergência; FV6 LAT-ETN/falso Fury; FV7 Pilar II; FV8 ressonância; FV9 Titã; FV10 metade; FV11 Conta; FV12 selo no mundo morto. FV6a é confirmação complementar da transmissão falsa.

Os16 `ALLIES` são iron, strange, panther, widow, hawkeye, hulk, loki, marvel, thor, starlord, gamora, magneto, wolverine, vision, captain, scarlet. Não confundir todo relationship existente com integrante de ALLIES: o diário também pode mostrar outros IDs, mas fullAllies usa esse array fixo. H/C são contadores de tom usados por afinidades e Eco, não dois pontos de vida ou classes de poder.

### 11.5 Terreno pré-renderizado

`assets/visit_world/terrain/` contém150 PNG com padrão `x-y.png`, x0–14/y0–9, cobrindo chunks de512×512. WorldArt só carrega os próximos à câmera, cache12. A grade mundial é menor que a borda completa de10 chunks em Y; a última faixa cobre o limite com margem. A presença de um arquivo não prova que todo seu conteúdo está acessível por caminhada.

As imagens são assets de fundo. Sólidos/portões pertencem ao JSON/WorldMap; apagar apenas o desenho não remove colisão. Se o arquivo não abrir, WorldArt tenta `draw_tile` para reconstruir o chunk. O app não depende de baixar mapas de um servidor.

### 11.6 Ícones e valores

`mipmap-anydpi-v26/ic_launcher.xml` e `ic_launcher_round.xml` apontam background/foreground de Doom e monochrome. Há pares WEBP normal/round em mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi para densidades. O Manifest referencia os nomes mipmap, deixando Android resolver variante.

`values/strings.xml` define somente `app_name=Marvel Universe` e `character_image=Imagem do personagem`. Grande parte dos textos está literal em Java ou WorldRules/JSON. `values/colors.xml` contém black/white, enquanto muitas paletas são inteiros ARGB diretamente no código. `values/themes.xml` define Base.Theme.ProjetoMarvel herdando Material3.DayNight.NoActionBar e Theme.ProjetoMarvel que herda a base; `values-night/themes.xml` redefine a mesma base para modo noturno, sem cores próprias adicionais.

## 12. AndroidManifest.xml

**Local:** `app/src/main/AndroidManifest.xml`. Android lê o manifesto para conhecer componentes, permissões e ponto de entrada antes de executar Java.

| Declaração | Significado no projeto |
|---|---|
| `xmlns:android`, `xmlns:tools` | Namespaces de atributos; tools está disponível, sem regra de merge particular nessa declaração |
| `uses-permission INTERNET` | Autoriza rede Comic Vine/imagens/Gemini/modelos; não garante conectividade |
| `allowBackup=true` | App participa de backup conforme mecanismos/configurações do sistema |
| `dataExtractionRules=@xml/data_extraction_rules` | Regras modernas de extração declaradas |
| `fullBackupContent=@xml/backup_rules` | Configuração de backup completo |
| `icon`, `roundIcon` | Recursos mipmap |
| `label=@string/app_name` | Nome no sistema |
| `supportsRtl=true` | Declara suporte de direção; não comprova revisão de todo layout em RTL |
| `theme=@style/Theme.ProjetoMarvel` | Tema comum, sem ActionBar automática |

O Manifest não tem `package` literal; o namespace/applicationId estão em Gradle. As classes relativas `.MainActivity`, `.FightActivity`, `.VisitWorldActivity` são resolvidas no pacote do app.

`MainActivity` é exported=true e tem intent-filter MAIN/LAUNCHER, orientação portrait, teclado adjustResize e `configChanges=orientation|screenSize|keyboardHidden`. `FightActivity` é exported=false, landscape e trata os mesmos configChanges. `VisitWorldActivity` é exported=false, landscape, adjustResize e os mesmos configChanges. As duas Activities de jogo só são abertas internamente pelo fluxo normal.

Não há Service, BroadcastReceiver, Provider, deep link ou SplashActivity declarada. A Splash é um layout da MainActivity. Remover a declaração de uma Activity pode deixar a referência Java compilável, mas o Intent falharia em runtime por componente não registrado. Remover a permissão pode quebrar operações de rede, ainda que a UI abra.

### 12.1 Arquivos de backup

`res/xml/backup_rules.xml` tem raiz full-backup-content, mas includes/excludes estão comentados. `res/xml/data_extraction_rules.xml` tem cloud-backup vazio e bloco de device-transfer comentado. Não existe regra autoral ativa excluindo explicitamente o SQLite ou battle_record. Os detalhes finais de backup variam com o sistema; não foi possível determinar com segurança apenas pelos arquivos analisados quais dados de uma instalação concreta já foram copiados/restaurados.

## 13. Gradle e configurações

### 13.1 `settings.gradle.kts`

Define repositórios de plugins google com filtros de grupo, mavenCentral e gradlePluginPortal. Inclui plugin `org.gradle.toolchains.foojay-resolver-convention`1.0.0. Dependências usam google/mavenCentral com `FAIL_ON_PROJECT_REPOS`, evitando repositórios próprios de subprojetos. Nome da raiz é `ProjetoMarvel`, único módulo incluído `:app`. Sem esse arquivo, o Gradle não descobre a estrutura/configuração igual à atual.

### 13.2 `build.gradle.kts` da raiz

Declara `alias(libs.plugins.android.application) apply false`. Disponibiliza a versão do plugin via catálogo sem aplicá-lo à raiz. Não contém código de tela, tasks de geração da campanha ou servidor.

### 13.3 `app/build.gradle.kts`

| Opção | Valor encontrado |
|---|---|
| Plugin | Android application via alias |
| namespace/applicationId | com.example.projetomarvel |
| compileSdk | release37 |
| minSdk |24|
| targetSdk |37|
| versionCode/versionName |1 /1.0|
| Runner de teste | androidx.test.runner.AndroidJUnitRunner |
| buildConfig | Habilitado |
| Java source/target compatibility | VERSION_11 |
| Release optimization | enable=false |

Lê Properties do arquivo local.properties. `COMIC_VINE_API_KEY` tem fallback vazio. `GEMINI_API_KEY` tenta propriedade local e, se ausente, variável de ambiente de mesmo nome, depois vazio. Escapa barra/aspas para gerar literais em BuildConfig. Chaves não estão definidas como constantes diretamente numa classe Java de produção. Entretanto, BuildConfig é compilado no cliente: usar local.properties evita versionar o segredo, mas não torna o valor impossível de extrair do aplicativo. Não foi encontrada infraestrutura de servidor para esconder credenciais do cliente.

### 13.4 `gradle/libs.versions.toml` e bibliotecas

| Dependência/versionamento | Papel e uso observado |
|---|---|
| AGP9.4.1 | Plugin com.android.application; compila/empacota recursos e Java |
| androidx.appcompat:appcompat1.6.1 | AppCompatActivity e AlertDialog de confirmação |
| com.google.android.material:material1.10.0 | Tema Theme.Material3.DayNight.NoActionBar; não significa uso de BottomNavigationView |
| androidx.activity:activity-ktx1.13.0 | Dependência declarada; código autoral é Java, não foi identificado uso direto de extensões Kotlin |
| androidx.constraintlayout:constraintlayout2.1.4 | Declarada; seis layouts atuais usam FrameLayout/LinearLayout, sem ConstraintLayout |
| androidx.recyclerview:recyclerview1.4.0 | RecyclerView/Adapter/LayoutManager da Home |
| com.google.mlkit:translate17.0.3 | CharacterTranslator e download/tradução de modelo |
| junit:junit4.13.2 | Suíte de teste local |
| androidx.test.ext:junit1.3.0 | AndroidJUnit4 e asserções em contexto Android |
| androidx.test.espresso:espresso-core3.7.0 | Interação por texto/click nos testes de fluxo |

Não foi feita consulta externa de versões disponíveis nesta tarefa; os números acima são os declarados no projeto, não recomendação de upgrade. Não foi comprovada a necessidade de cada dependência não utilizada diretamente; algumas podem ser transitivas/base de template. Não foram alteradas dependências para produzir esta documentação.

### 13.5 Wrapper, JVM, propriedades e regras

`gradle/wrapper/gradle-wrapper.properties` fixa Gradle9.6.0-bin, SHA256 de distribuição, armazenamento em GRADLE_USER_HOME, timeout10000 e validação da URL. `gradle-wrapper.jar` é o bootstrap binário. `gradlew` e `gradlew.bat` resolvem diretório do projeto, JAVA_HOME/java, opções da JVM e chamam o JAR; não são parte do aplicativo instalado.

`gradle/gradle-daemon-jvm.properties` é gerado por updateDaemonJvm e fixa toolchainVersion25 com URLs por sistema/arquitetura. Isso trata a JVM que executa o build. Já `compileOptions` Java11 controla a compatibilidade das fontes/bytecode do app; os dois números não precisam representar a mesma função.

`gradle.properties` define `org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8` e configuration-cache=true. Paralelismo aparece apenas comentado. `app/src/main/keepRules/rules.keep` contém comentários de exemplo R8, inclusive WebView apenas comentado; não há keep rule ativa implementando JavaScript. `app/.gitignore` ignora /build; `.gitignore` da raiz ignora local.properties, .gradle e vários artefatos IDE/build.

`local.properties` é configuração local de SDK/credenciais, não fonte da UI. Seus valores privados não foram copiados para a documentação. `README.md` descreve configuração Comic Vine e visão anterior. `VISITAR_MUNDO_ANDROID.md` descreve entrega/testes passados da exploração; afirmações históricas ali não foram tratadas como resultados executados agora.

### 13.6 Android Studio e arquivos gerados

`.idea/AndroidProjectSystem.xml` seleciona sistema Gradle; `gradle.xml` vincula raiz/app e modo de runner; `compiler.xml` informa bytecode25 da IDE; `misc.xml` aponta JDK25/jbr-25 e projeto Android; `runConfigurations.xml` ajusta producers de teste; `deploymentTargetSelector.xml` guarda escolha do alvo; `.idea/.gitignore` ignora shelf/workspace. `workspace.xml` é estado local de editor e execução, agrupado como metadado sem regra funcional do APK.

Diretórios `.gradle`, `build`, `app/build` contêm caches, manifests mesclados, símbolos, classes, APKs e relatórios gerados. `R`, `BuildConfig` e classes dex não devem ser documentados como classes autorais separadas. Sua existência não demonstra atualidade em relação às fontes. O inventário final lista arquivos relevantes de entrada e agrupa saídas/binaries.

## 14. Comunicação Java ↔ XML

### 14.1 Exemplo real da Home

```text
MainActivity.open("Home")
  → setContentView(R.layout.activity_main)
  → findViewById(R.id.screen_container)
  → ScreenFragment.create("Home")
  → inflate(R.layout.fragment_screen)
  → findViewById(R.id.fragment_surface)
  → HomeReference.render()
  → new RecyclerView + setAdapter(new HeroAdapter)
  → HeroAdapter.onCreateViewHolder
  → inflate(R.layout.item_hero)
  → Holder.findViewById(hero_name/hero_real_name/hero_deck/hero_image)
  → onBindViewHolder
  → card.setOnClickListener(v → details(hero))
```

O XML do item define tipos/IDs/medidas, o Adapter associa dados e o listener decide navegação. O ID é gerado em `R` a partir de `@+id`. Texto e imagem são preenchidos após inflação.

### 14.2 Exemplo real da arena

```java
setContentView(R.layout.activity_fight);
LinearLayout hud = findViewById(R.id.fight_hud);
LinearLayout controls = findViewById(R.id.fight_controls);
stageFrame = findViewById(R.id.fight_stage);
```

Depois, `build` acrescenta View Arena e botões. O click do soco chama `startAttack(playerFighter,0)`; o ticker chama `battle.step`; o Canvas consulta `fighter.attack` e desenha POSE_PUNCH. O arquivo XML não contém cálculo de dano. Reciprocamente, o BattleEngine não contém referência de layout.

### 14.3 Tipos diferentes de recurso

- `R.layout.*`: hierarquia inflada.
- `R.id.*`: procura um nó daquela hierarquia.
- `R.drawable.*`: vetor/shape referenciado em XML ou setImageResource/background.
- `R.anim.*`: animação de transição aplicada pelo FragmentManager.
- `R.string.*`: textos declarados em values.
- `assets/...`: caminhos literais abertos por AssetManager; não existem `R.assets.world_data` no código.

Uma custom View declarada com nome completo no XML precisa de construtor que aceite AttributeSet. Views montadas só por Java podem ter apenas Context. Os blocos XML com fill/paths são geometria de recurso; o Canvas dos jogos executa geometria em Java no frame.

## 15. Dados, concorrência e estado

### 15.1 Ciclo das informações

| Informação | Origem | Armazenamento | Leitura/vida útil |
|---|---|---|---|
| Catálogo | Comic Vine | SQLite + ArrayList heroes | Persiste em disco e preenche Home |
| Detalhes | endpoint character | SQLite description/powers + Hero | Sob demanda, reutilizável |
| Imagem de catálogo | URL do registro | LruCache de Bitmap | Memória da Activity; sem cache de arquivo autoral |
| Tradução | ML Kit | HashMap translations | Memória da Activity; texto original no banco |
| Sincronização | paginação local/rede | getPreferences: catalog_sync_at/offset/mode | Preferências persistentes da MainActivity |
| Filtro | EditText | homeQuery | Memória da Activity, reaplicado às páginas novas |
| Seleção de luta | Toque em cards | índices e flags de MainActivity | Enviado por Intent, sessão da Activity |
| Dificuldade | Botões de Lutas | battleDifficulty | Memória, enviado ao motor |
| Quiz | banco local ou Gemini | questions/contadores | Mesma MainActivity; não é salvo em disco |
| Unlocks | conclusão Quiz/Story | static booleans | Processo, independente dos resets |
| Placar livre | fim da luta | SharedPreferences battle_record wins/losses | Persistente; guia atual não exibe obrigatoriamente esses números |
| Jarvis UI | pergunta/resposta | Views montadas | Recriar tela perde bolhas visuais |
| Jarvis remoto | turnos | jarvisHistory limitado12 | Memória; separado da transcrição visual |
| Story linear | criação/escolhas/vitórias | campos de StoryMode | Uma permanência na aba, reset ao sair |
| Mundo base | JSON asset | data/map/rules | Recriado ao abrir Activity |
| Sessão da ilha | newSession/decisões | Map session + static activeSession + Bundle | Retorno da luta/recriação; saída explícita limpa |
| Eco | final da ilha | static ecoMemory | Processo, entre explorações |
| Motor de batalha | construtor/comandos | Fighters/listas/timers/Snapshot | Uma instância da arena; retry cria outra |

### 15.2 Esquema da sessão da ilha

```text
session
├── version=1, campaign="fraturas", active=true, startedAt
├── character { id, type, kind, name, body, legs, accent, skin, power, appearance... }
├── player { x, y, direction, walking... }
├── flags { M01..., FV1..., ring..., H, C, solved... }
├── choices { ID_da_decisão: índice }
├── relationships { ID: 0..2 }
├── met [IDs de NPCs]
├── discovered [IDs de regiões, inicial NY-01]
├── notes [{ id, title, text }]
├── encounters { ID: "completed" }
├── eco { ending, H, C, allies, choices, protagonist } ou null
└── ending / storyRevision quando atribuídos pelas regras
```

Mapas dinâmicos facilitam compartilhar dados entre regras e apresentação, mas chaves e formatos são contratos textuais sem checagem estática de campos. `WorldJson` remove Fn ao serializar; os callbacks são recriados a partir das regras quando novas conversas são abertas.

### 15.3 Threads e callbacks

| Executor/relógio | Trabalho | Publicação |
|---|---|---|
| `io` serial | Banco inicial, filas Comic Vine e retry | `main.post` atualiza coleção e UI |
| `imageIo`3 threads | Downloads Bitmap e detalhes de personagem | Handler aplica só à View/seleção ainda correspondente |
| `geminiIo` serial | Jarvis, contexto e fallback | `main.postDelayed` respeita mínimo de digitação |
| `quizIo` serial | Requisição/parse Quiz | Handler compara quizGeneration |
| Fight handler | Ticker16ms, countdown, resultado | Tudo na thread principal da arena |
| Mundo handler | Frame16ms, HUD/avisos/retorno | Tudo na thread principal de exploração |
| ML Kit callbacks | Download/tradução assíncronos | Consumer atualiza textos/cache conforme contexto |

Não há coroutine/RxJava. `synchronized` no cache SQLite serializa métodos; o motor é atualizado sequencialmente pelo ticker. `Random` com seed torna simulações previsíveis, mas não torna tarefas HTTP determinísticas.

### 15.4 Reset não é a mesma coisa que persistência

Reiniciar Quiz limpa perguntas/progresso e gera novo lote; não limpa unlock. Reiniciar Story linear limpa protagonista/decisões/wins; não limpa Doom. Sair da aba História chama resetForTabExit. Encerrar e recriar MainActivity conserva booleans static enquanto o processo vive, mas não todos os contadores de instância. Sair do mundo limpa activeSession; Eco permanece. Retry de batalha recria BattleEngine e restaura HP/recargas, sem conceder vitória anterior ao modo chamador automaticamente.

## 16. Dependências entre arquivos

Na tabela, as setas representam chamadas/imports/construções observadas; não apenas associação de nomes.

| Arquivo | Depende de | Utilizado por | Responsabilidade |
|---|---|---|---|
| MainActivity.java | ScreenFragment, HomeReference, StoryMode, BattleHubReference, APIs/cache/arte | Manifest, compositores, testes | Coordenar destinos e experiências portrait |
| ScreenFragment.java | fragment_screen.xml, AndroidX Fragment | MainActivity | Superfície de destino |
| HomeReference.java | MainActivity, RecyclerView | MainActivity.home | Header/busca/lista |
| ComicVineClient.java | HTTP, org.json, MainActivity.Hero | MainActivity | Catálogo/detalhes reais |
| CatalogStore.java | SQLite, MainActivity.Hero | MainActivity | Cache persistente |
| CharacterTranslator.java | ML Kit | MainActivity | Tradução com fallback |
| GeminiClient.java | HTTP, BuildConfig, JSON | MainActivity | Geração remota |
| QuizGenerator.java | JSON, MainActivity.QA | MainActivity | Prompt/schema/parse |
| StoryMode.java | MainActivity, FighterArtView, StoryArtView, Fight/VisitWorldActivity | MainActivity | Campanha linear |
| BattleHubReference.java | MainActivity, FighterArtView | MainActivity | Guia e dificuldade |
| FightActivity.java | activity_fight, BattleEngine, artes/efeitos | MainActivity, StoryMode, WorldBattleBridge | Arena e resultado |
| BattleEngine.java | java.util | FightActivity, CombatEffects, testes | Simulação |
| FighterArtView.java | Canvas, Color, Path | Splash, retratos, Story/seleção/arena | Rig e poses |
| FighterSprites.java | PNG, Bitmap, Context | Sem consumidor ativo encontrado | Recorte legado |
| CharacterPortraitView.java | FighterArtView | Seletores | Busto circular |
| AttackEffects.java | Canvas | FightActivity, CombatEffects | Arte de poder comum |
| CombatEffects.java | BattleEngine, FighterArtView, AttackEffects | FightActivity | Especial/status/projeções |
| SplashCinematicView.java | FighterArtView, relógio Android | activity_splash.xml | Abertura |
| ArcReactorView.java | Canvas/ObjectAnimator | Sem consumidor ativo encontrado | Componente HUD |
| GauntletView.java | Canvas/Path | MainActivity | Manopla parcial/final |
| GemArtView.java | Canvas/Path | MainActivity | Joia |
| StoryArtView.java | Canvas | StoryMode | Portal |
| HeroEmblemView.java | Canvas | Sem consumidor ativo encontrado | Emblemas |
| Typography.java |9 TTF em assets | UI portrait/arena | Cache de fontes |
| VisitWorldActivity.java | asset JSON, WorldRules/Map/Art/Bridge | StoryMode, Manifest, testes | Modo ilha |
| WorldRules.java | WorldValues, dados globais | Mundo/arte/ponte | Regras/cenas/desenhos compilados |
| WorldValues.java | Coleções/funções Java | WorldRules e World* | Operações dinâmicas |
| WorldJson.java | Java puro, WorldValues.Fn | Activity/arte/testes | JSON do mundo/sessão |
| WorldMap.java | WorldValues, map do JSON | VisitWorldActivity, WorldArt | Geometria/câmera |
| WorldArt.java | WorldRules/Map/Canvas, terrain assets | VisitWorldActivity | Pixel art/render |
| WorldCanvas.java | Canvas, WorldValues.NativeObject | WorldArt/regras | Adaptar comandos |
| WorldBattleBridge.java | WorldValues/Rules, Intent | VisitWorldActivity | Arena a partir de encontro |
| WorldEnemyArt.java | FighterArtView poses, Canvas | FightActivity | Genéricos da ilha |

## 17. Matriz telas × arquivos

| Tela | Java principal | XML | Auxiliares/dados |
|---|---|---|---|
| Splash | MainActivity | activity_splash | SplashCinematicView, FighterArtView |
| Home | MainActivity, HomeReference | activity_main, fragment_screen, item_hero | ComicVineClient, CatalogStore, CharacterTranslator, Typography |
| Detalhes | MainActivity | activity_main, fragment_screen | ComicVineClient, tradução e imageCache |
| Jarvis | MainActivity | activity_main, fragment_screen | GeminiClient, JARVIS_PERSONA e fallback |
| Quiz/pergunta/final | MainActivity | activity_main, fragment_screen | QuizGenerator, GeminiClient, GemArtView, GauntletView, ic_gem_lock |
| História/criação/diálogo | StoryMode | activity_main, fragment_screen | StoryArtView, FighterArtView, CharacterPortraitView |
| Lutas/guia | BattleHubReference | activity_main, fragment_screen | FighterArtView, MainActivity.battleDifficulty |
| Seleção/pré-luta | MainActivity | activity_main, fragment_screen | FighterArtView, CharacterPortraitView, roster/civilian |
| Arena/resultado | FightActivity | activity_fight | BattleEngine, CombatEffects, AttackEffects, FighterArtView, WorldEnemyArt |
| Caracterização da ilha | VisitWorldActivity | activity_visit_world | WorldRules/Values, APPEARANCE/COLORS/HEROES/POWERS |
| Ilha/HUD | VisitWorldActivity | activity_visit_world | WorldMap/Art/Canvas, JSON, terrain |
| Diário/mapa/conversa | VisitWorldActivity | activity_visit_world | WorldRules, session, SpriteView/MapView |

## 18. Matriz funcionalidades × arquivos

| Funcionalidade | Arquivos/métodos envolvidos | Como funciona |
|---|---|---|
| Entrada do app | Manifest, MainActivity.onCreate/showSplash | Launcher → dependências → cinematic → Home |
| Navegação inferior | MainActivity.open/makeNav, ScreenFragment, XML base | Itens Java trocam superfície e renderer |
| Catálogo completo | MainActivity.loadCatalog, ComicVineClient, CatalogStore | Cache primeiro, fila/páginas, dedupe e publicação incremental |
| Busca | HomeReference TextWatcher, refreshVisibleCatalog | Filtra objetos recebidos e notifica Adapter |
| Card/detalhe | item_hero, HeroAdapter, detailScreen | Click guarda Hero; detalhe sob demanda e toolbar fixa |
| Tradução | CharacterTranslator/translateText | Modelo local e cache de strings, falha conserva source |
| Imagens remotas | MainActivity.loadImage | Pool de downloads, agrupa URL, cache e tag |
| Resposta Jarvis | MainActivity.jarvis/sendJarvis, GeminiClient | Consentimento remoto ou local; espera/typing/history |
| Perguntas Quiz | seedQuestions, QuizGenerator, GeminiClient | Banco24 ou JSON validado da geração |
| Erros/Joias | MainActivity.quiz/resetQuiz/showGemUnlock | Um erro por etapa, segundo reinicia; quatro perguntas concede Joia |
| Desbloqueio | MainActivity static flags, StoryMode.battleResult | Quiz final→Thanos; terceira luta linear→Doom |
| Protagonista linear | StoryMode, FighterArtView | Custom ou11 heróis, aparência/poder/tema e nome único |
| Roteiro linear | StoryMode.PHASES/render | Estado discreto, decisões e três chamadas de arena |
| Escolha de luta | BattleHubReference, pickScreen/preFight | Dificuldade, jogador/BOT e Intent |
| Dano/HP/defesa | BattleEngine | Validação, contato, modificadores e estado final |
| Pulo/travessia | BattleEngine.jump/physics/separate/face | Altura/velocidade permitem evitar contato e trocar lados |
| Especial/passiva | BattleEngine.special/advanceSpecial/passives/allies | Skill seleciona regra; timers limitam execução |
| Feedback visual | FighterArtView, AttackEffects, CombatEffects, Arena | Desenha a partir do estado, não aplica dano |
| Exploração | VisitWorldActivity.frame, WorldMap | Input, movimento, colisão, câmera, proximidade |
| Conversa/decisão ilha | WorldRules, WorldValues, showDialogue | Script/callback muda flags e relações |
| Objetivo/portões | WorldRules.refreshProgress/objective, WorldMap | Flags derivadas liberam caminhos e mudam alvo |
| Combate narrativo ilha | WorldBattleBridge, FightActivity, onActivityResult | Arena real; só vitória completa encontro |
| Final/Eco | WorldRules.resolveEnding, rememberEnding | Condições de aliados/pistas/escolha; memória do processo |
| Pixel art | WorldRules.draw*, WorldCanvas, WorldArt, PNG terrain | Comandos nativos/cache/culling/depth |

## 19. Impacto da remoção

Esta análise é contrafactual: nenhum arquivo foi removido. “Falha de compilação” pressupõe apagar o arquivo mantendo consumidores; “runtime” pressupõe que a referência ainda possa compilar, como nome de classe em XML ou asset por string.

| Arquivo/grupo | Compilação | Runtime/funcionalidade/visual |
|---|---|---|
| MainActivity | Referências diretas quebram | Sem entrada/navegação principal |
| ScreenFragment/fragment_screen | Recursos/classes quebram | Sem superfície portrait |
| HomeReference/item_hero | Referências quebram | Home ou linhas deixam de ser montadas |
| ComicVineClient | Referências quebram | Perde rede de catálogo/detalhes após adaptação |
| CatalogStore | Referências quebram | Sem cache; apagar apenas DB recria e exige dados novos |
| CharacterTranslator | Referências quebram | Sem tradução; falha de modelo já tem fallback |
| GeminiClient/QuizGenerator | Referências quebram | Sem geração remota; banco/fallback local existe mas consumidores precisam adaptar |
| StoryMode/StoryArtView | Referências quebram | Perde campanha/portal e acesso atual ao mundo |
| BattleHubReference | Referências quebram | Perde guia e escolha de dificuldade |
| FightActivity | Referências quebram | Intents não abrem arena |
| BattleEngine | Referências quebram | Sem regras de combate e tipos do renderer |
| FighterArtView | Referências quebram | Personagens/Splash/poses não desenham |
| CharacterPortraitView | Referências quebram | Sem bustos de seleção |
| AttackEffects/CombatEffects | Referências quebram | Sem efeitos comuns/especiais/status |
| WorldEnemyArt | Referência da arena quebra | Perde aparências de doombot/agente |
| SplashCinematicView | XML por nome pode ainda compilar | Inflação da Splash falha em runtime |
| GauntletView/GemArtView | Referências quebram | Sem representação progressiva das Joias |
| Typography | Referências quebram | Fontes compartilhadas indisponíveis |
| Um TTF usado | Java pode compilar | createFromAsset pode falhar ao abrir aquela fonte |
| FighterSprites/ArcReactorView/HeroEmblemView | Sem consumidor atual encontrado | Baixo impacto no caminho ativo, perda de componentes disponíveis |
| fighter PNG / ic_gauntlet.xml | Referências de FighterSprites podem quebrar se PNG apagado isoladamente | Não há exibição ativa comprovada; preservar para histórico/ref |
| VisitWorldActivity | Referência de StoryMode quebra | Sem ilha |
| WorldRules/Values/Json/Map/Art/Canvas/Bridge | Referências quebram | Respectivamente narrativa, operações, parsing, geometria, desenho, adaptação e integração deixam de existir |
| world-data.json | Acesso é string, pode compilar | onCreate lança erro ao carregar; ilha não entra |
| terrain PNG | Pode compilar | chunk tenta fallback de draw_tile; mudança visual/desempenho possível |
| activity_fight/activity_visit_world/activity_main/activity_splash | R.layout quebra | Sem seus contêineres |
| ic_nav_* / ic_gem_lock | R.drawable quebra | Perda visual após eventual adaptação |
| Ícones/tema/strings referenciados | Resource linking pode falhar | Identidade/tema/labels afetados |
| Registro de Activity no Manifest | Referência Java pode compilar | ActivityNotFoundException/launcher ausente |
| INTERNET | Compila | Rede pode falhar; não apaga dados locais |
| backup_rules/data_extraction_rules | Resource linking se referência mantida | Regras de backup deixam de ser configuradas |
| Gradle/settings/catalog | Build pode não configurar/resolver | Não altera APK já instalado; impede reconstrução adequada |
| Wrapper scripts/JAR | Código do app não muda | Fluxo de build via wrapper deixa de funcionar |
| local.properties | BuildConfig pode ficar vazio; SDK depende do ambiente | Sem credencial, API cai em caminhos locais/erro de configuração |
| Testes/fixtures | Build de testes quebra quando referenciados | APK de produção pode continuar; perde verificação |
| README/VISITAR/licenças | Normalmente sem efeito no Java | Perde instruções/contexto/licenciamento; não são dispensáveis por isso |
| .idea/workspace/build/cache | Não são fonte autoral do app | Perde estado local/artefatos; build tende a regenerar saídas |

## 20. Pontos técnicos importantes e testes existentes

### 20.1 Padrões e fronteiras observados

**Composição programática:** os layouts comuns definem superfícies; centenas de Views são montadas por Java. Isso permite factories compartilhadas e estados de roteiro sem muitos XML, mas a árvore real exige ler os compositores. Recursos de texto/cor não estão integralmente centralizados em values.

**Modelos internos e acesso package-private:** Hero/QA vivem na Activity e são usados por API/cache/QuizGenerator. Helpers acessam atributos do host diretamente, explicando por que várias classes precisam continuar no mesmo package. `MainActivity` é um ponto de forte dependência.

**Dois modelos de jogo:** o motor de luta usa tipos e enums Java explícitos; o mundo usa mapas e funções dinâmicas compiladas em Java. Eles se encontram pela ponte de Intent. Alterar um nome de herói pode afetar desenho, skill, identidade e texto, pois há seleções por nome/substrings.

**Estado temporal:** timers são segundos em floats no motor e milissegundos/nanos nos relógios Android. As conversões e dt limitado evitam saltos, mas não tornam performance garantida. Navegação e mapa têm relógios/estado independentes.

**Três representações de personagem:** Hero de catálogo vem da API; roster de combate é array fixo; HEROES do mundo define paletas/sprites. Não são uma única lista universal. Existe ainda a implementação PNG legado sem consumo atual.

**Persistências diferentes:** SQLite é catálogo; preferências de placar/sincronização são permanentes; Quiz/story linear são memória de Activity; unlocks e Eco são memória de processo; sessão do mundo também pode ir para Bundle. Uma declaração estática não equivale a banco de dados.

### 20.2 Limites e divergências que afetam a compreensão

1. Os comentários de referência HTML/Web descrevem origem histórica; a execução observada é nativa. A versão Web mencionada na documentação antiga não foi usada como fonte de verdade nesta análise.
2. O motor usa120/130HP e poderes específicos, enquanto pedidos/documentos antigos citavam100HP e tabela única. Esta documentação registra o motor atual.
3. `FighterSprites`, ArcReactorView, HeroEmblemView e ic_gauntlet permanecem sem chamadas ativas encontradas. Não atribuir a eles o visual atual apenas por seus comentários.
4. O ramo místico e o resgate da história narram benefícios; somente `story_choice_tech` tem tratamento mecânico explícito encontrado na batalha final. `story_choice_rescue` é enviado, mas não consumido.
5. `WorldBattleBridge` não transmite flags como Destino_A ou estabilizador. Texto que descreve uma intervenção futura não comprova implementação da cena em combate.
6. `WorldRules.objectDialogue` chama função `locationById`, mas não foi localizado seu instalador/registro nos arquivos de produção lidos. `WorldValues.call` devolve null quando não recebe Fn; isso pode deixar nomes/subtítulos vazios nessas falas. A viagem efetiva usa `world.byId` em finishDialogue. Não foi executado esse fluxo nesta tarefa.
7. `encounterAllowed` não rejeita completed por si, embora a conclusão tenha proteção contra duplicidade e o roteador de diálogos use repeat. Isso é relevante ao ler os testes.
8. O seletor de heróis da ilha usa COMBAT_HEROES e não unlocks do menu livre. A campanha linear usa apenas11 heróis. São comportamentos distintos confirmados.
9. MainActivity não reconstrói o histórico visual Jarvis de `jarvisHistory`; o contexto remoto e as bolhas não têm a mesma persistência.
10. Parser de Quiz valida forma/unicidade interna, não verdade factual e nem repetição semântica com o lote anterior. O prompt solicita essas qualidades, mas não as garante.
11. Chaves fora do versionamento ainda são incorporadas via BuildConfig no cliente. Não há backend próprio identificável para protegê-las em runtime.
12. Estados de Activity sem onSaveInstanceState não sobrevivem garantidamente a morte de processo. configChanges trata mudanças específicas, não todas as formas de destruição.

Esses registros ajudam manutenção e leitura; não constituem uma rodada de correções. Não houve alteração de gameplay, segurança, textos de tela ou arquitetura durante a documentação.

### 20.3 Testes locais: arquivo por arquivo

#### `app/src/test/java/com/example/projetomarvel/ExampleUnitTest.java`

Template JUnit com `addition_isCorrect`, assertEquals(4,2+2). Verifica infraestrutura básica de teste, não comportamento Marvel. Sem ele não perde funcionalidade do APK.

#### `app/src/test/java/com/example/projetomarvel/BattleEngineChecks.java`

Biblioteca de verificações e entrypoint `main`, Java puro. `HEROES` tem13; `engine`/`custom` criam motor com seed42; `advance` simula passos0,01s sem IA; `close` posiciona lados e `ready` zera recargas/stun. `ok` incrementa contador e lança AssertionError; `eq` compara com tolerância0,02.

Blocos: `basics` testa dano/guarda/movimento/facing; `common` poderes dos13 e sentido aranha; `specialRules` duração/recarga/passivas; `beamsAndStrikes` aviso/ar/exaustão; `rewind` snapshot; `scarletAllies` ciclo de aliados; `customs` seis poderes; `interactions` BOT nos lados e interações; `simulations` faz até9000 passos por herói/dificuldade verificando finitude, vida/limites e projeções. `main` chama todos e só imprime PASS se nenhum assert interromper.

Há expectativas históricas incompatíveis com o motor atual: Thor com bônus físico, Wolverine comum4 em vez6 e cura após5s em vez2; Unibeam25 em vez35; Thanos com meteoros e especial limitado em vez estalo; Escarlate com duas ondas/aliado antecipado em vez uma ao30; congelamento fracionário em vez ceil. São exemplos diretamente comparáveis entre arquivos. Não se deve deduzir aprovação atual da existência dessas asserções.

#### `app/src/test/java/com/example/projetomarvel/BattleEngineTest.java`

Wrapper JUnit com9 métodos que chamam os grupos de BattleEngineChecks: meleeMovementAndFacing, commonPowers, specialLifetimesAndPassives, reactionWindowsAndAir, exactTemporalSnapshot, scarletAllyLifecycle, allSixCustomPowers, interactionsAndBotDirections, aiAndArenaInvariants. Não redefine regras; herda divergências dos checks.

#### `app/src/test/java/com/example/projetomarvel/WorldGenericBattleTest.java`

Tem4 testes: dash para diante do adversário sem dano/empurrão; parede com oponente no ar; aproximação durante dash; tiro genérico8 e Doom original preservado como Skill.DOOM. `arena()` usa flag genericBot=true e seed42; `advance` desabilita IA. Atua no contrato específico introduzido para inimigos genéricos da ilha. Não testa render nem Android Intent real.

### 20.4 Testes instrumentados: arquivo por arquivo

#### `app/src/androidTest/java/com/example/projetomarvel/ExampleInstrumentedTest.java`

AndroidJUnit4, usa InstrumentationRegistry e confirma packageName `com.example.projetomarvel`. Exige dispositivo/emulador. Não verifica navegação ou jogo.

#### `app/src/androidTest/java/com/example/projetomarvel/VisitWorldFlowTest.java`

`setup` limpa activeSession/ecoMemory e lê fixtures do APK de testes. `open` cria ActivityScenario; `copy` faz ida/volta JSON para não alterar fixture; `find`/`input` percorrem árvore View; `resumedFight` busca Activity de combate pelo monitor de lifecycle.

Os5 testes principais são:

- `storyButtonOpensNativeCreation`: entra História, toca VISITAR MUNDO, confirma Activity/etapa inicial.
- `customFourStepsTouchCollisionCameraAndFreshSession`: valida nome, ausência Deadpool selecionável, aparência/cor/poder, toque no pad, deslocamento/câmera/colisão e sessão nova após exit.
- `nearNpcTouchOpensDialogueWithTwoChoices`: posiciona personagem perto do NPC, converte coordenadas e toca; confirma conversa e opções.
- `realBattleRequestsAndVictoryReturn`: usa cinco fixtures, abre luta real, acessa motor por reflection, força simulação de vitória e retorna ao mundo; confere tipo de adversário/custom/variante e conclusão.
- `defeatAndCancelKeepEncounterAvailable`: força derrota e depois cancelamento, verificando que o encontro continua disponível.

Os testes usam Espresso para click por texto, ActivityScenario e reflection de métodos/atributos privados da arena para controlar resultado. Logo dependem de nomes de campo/método e textos literais, não só interface pública estável. Há assertion de encounterAllowed=false após vitória que não corresponde a uma checagem explícita de completed nessa função atual; execução real deve resolver essa diferença. Não foram executados agora.

#### `app/src/androidTest/assets/world-device-fixtures.json`

Contém5 sessões preparadas: `custom-generic`, `hero-generic`, `hero-hero`, `hero-villain`, `hero-variant`. Usa o mesmo esquema da sessão da ilha, com flags suficientes para abrir encontros específicos. É asset do APK de testes, não carregado pelo aplicativo de produção em fluxo normal.

### 20.5 O que esta documentação validou

Foram conferidos nomes, caminhos, declarações e chamadas, XML/IDs, contratos de Intent, variáveis de estado, regra de dano/vida, dependências Gradle, dados do mundo e cobertura dos arquivos. A revisão do Markdown verifica referências e inclusão de todos os arquivos Java/layout/config relevantes. Não constitui teste de compilação, rede, tradução, precisão factual de IA, desempenho, multitouch ou comparação visual de render em dispositivo.

`VISITAR_MUNDO_ANDROID.md` registra builds e comparações passadas e limitações daquela entrega. Esses resultados são contexto histórico de um arquivo do projeto, não foram reproduzidos nem estendidos por esta tarefa.

## 21. Glossário

| Termo | Significado e exemplo real |
|---|---|
| Activity | Janela/controlador Android com ciclo de vida; MainActivity/FightActivity/VisitWorldActivity |
| AppCompatActivity | Base AndroidX da Activity principal, com suporte a FragmentManager e compatibilidade |
| Fragment | Componente de UI hospedado numa Activity; ScreenFragment infla superfície reutilizável |
| Intent | Mensagem de abertura/resultado de componente; leva nomes e dificuldade à arena |
| Extra | Par chave/valor num Intent, como story_battle/world_encounter |
| Bundle | Mapa tipado de argumentos/estado Android; screen do Fragment e sessão JSON no mundo |
| View | Elemento desenhável/interativo; Button, RecyclerView ou custom Arena |
| ViewGroup/Layout | View que organiza filhos; FrameLayout sobrepõe e LinearLayout distribui |
| LayoutInflater | Transforma XML de layout em objetos View; usado pelo Fragment/Adapter |
| findViewById | Procura View com ID dentro da hierarquia, por exemplo fight_hud |
| Listener | Callback registrado para evento; click, touch, texto, insets e layout |
| MotionEvent | Evento de toque com ação/pointer; ativa/solta controles de movimento |
| RecyclerView | Lista que reutiliza Views; catálogo pode crescer sem criar milhares de cards visíveis |
| Adapter/ViewHolder | Ligam coleção a linhas e guardam referências de subviews; HeroAdapter/Holder |
| XML | Formato declarativo de Manifest, layout e recursos |
| Resource/R | Recursos res recebem IDs gerados na classe R, como R.layout.item_hero |
| Drawable | Fundo/ícone desenhável; vetores XML e drawables de gradiente custom |
| Canvas | Superfície de comandos 2D; arena, personagens e pixel art |
| Paint/Path | Estilo do desenho e geometria de caminhos |
| Shader | Gradiente de cor; brilhos de efeitos e backgrounds |
| dp/sp | Unidades para tamanho de layout/texto; simulação usa coordenadas lógicas distintas |
| Context | Acesso a recursos/serviços e abertura de componentes; Activity é um Context |
| onCreate/onResume/onPause/onDestroy | Etapas de criação, retomada, pausa e descarte de componente |
| Handler/Looper | Agenda trabalho na fila de uma thread; usado na UI e relógios |
| ExecutorService | Pool/fila de trabalho fora da thread de interface |
| Callback/Consumer/Fn | Entrega resultado futuro ou comportamento armazenado; tradução/HTTP/regras |
| HttpURLConnection | Cliente HTTP utilizado diretamente nas integrações |
| REST/endpoint | Rotas de serviço HTTP, como /characters/ e generateContent |
| JSON | Estrutura textual de dados das APIs e da ilha |
| SQLiteOpenHelper | Gerencia banco/versionamento local; CatalogStore |
| SharedPreferences | Pares simples persistentes; offset/data de sync e placar |
| LruCache | Cache que remove entradas menos recentemente usadas; imagens/chunks/sprites |
| Manifest | Declara Activities, permissões, launcher e tema |
| Gradle/AGP | Sistema de build e plugin Android que compilam/empacotam o módulo |
| BuildConfig | Classe gerada com constantes de build, incluindo chaves configuradas localmente |
| AssetManager | Abre arquivos assets por caminho, como fontes e world-data.json |
| APK | Pacote instalável; existência em build não garante atualização com fontes atuais |
| Frame/dt | Atualização da simulação e intervalo em segundos, limitado a0,05 no motor |
| Cooldown | Tempo até nova ação; separado de duração da animação |
| Hitbox/contato | Área matemática testada para golpe; não é a forma exata da imagem |
| HP/stun/slow/frozen | Vida, impedimento temporário, lentidão e congelamento de Fighter |
| Flag | Estado booleano/valor nomeado da narrativa que libera regras/caminhos |
| Scope/closure | Escopo e função que captura contexto; operações nativas em WorldRules |
| Snapshot/rewind | Cópia temporal do motor para passiva de Doutor Estranho |
| Culling/depth | Pular desenho fora da câmera e ordenar camadas por profundidade |
| Teste local/instrumentado | Java no host versus execução com Android real/emulado |

## 22. Como o MarvelUniverse funciona internamente

O Android inicia MainActivity por meio do Manifest. Ela inicializa banco, tradução e perguntas locais, e mostra uma cena Canvas. Ao terminar, instala o layout principal e uma superfície de Fragment; o conteúdo de cada destino é montado em Java. A barra inferior altera a string de tela e solicita nova composição.

Na Home, a apresentação depende de uma coleção que começa no SQLite e cresce com a Comic Vine em executor. O cliente HTTP interpreta JSON e filtra editora, o cache preserva registros e o Adapter transforma a projeção filtrada/ordenada em cards. Toque guarda o mesmo Hero para uma tela com toolbar fixa. Tradução e imagem chegam por callbacks protegidos contra reutilização incorreta de Views.

Jarvis usa a mesma infraestrutura de tela, mas monta bolhas e composer. A pergunta segue resposta local ou chamada remota autorizada, com indicador de digitação e tempo mínimo. Quiz usa banco local ou geração remota estruturada; sua pontuação, erros e Joias continuam sob controle de MainActivity. A conclusão liga um boolean de processo, separado do reset do desafio.

História linear mantém outro estado: protagonista, criação, personalidade, escolhas e fase. Cada tela executa uma parte do switch; uma pré-batalha envia aparência/nome/poder à arena. O resultado volta por Intent, avança a campanha e na terceira vitória desbloqueia Doom. Sair de aba reinicia essa campanha conforme a regra atual.

Lutas livres compartilham a arena: o usuário escolhe dificuldade e dois personagens, vê pré-luta e abre FightActivity. Essa Activity traduz toques em comandos do BattleEngine. O motor atualiza posições, ataques, projéteis, defesa, passivas, especiais e HP. Renderers consultam esse estado para desenhar a próxima imagem. A fila Hit comunica feedback; o resultado do motor dispara overlay e retorno/retry. O XML define superfícies e o motor não conhece XML.

Visitar Mundo é outra Activity acessada da História. Carrega JSON/terreno, cria uma sessão com protagonista próprio e usa regras compiladas Java para narrar Fraturas. Inputs movem o jogador com colisão e câmera. O Canvas desenha só o necessário e reutiliza chunks/sprites. Proximidade oferece interação; decisões registram flags e confiança; o objetivo orienta a próxima região. Encontros passam pela ponte de Intent à mesma arena. Vitória concede o evento; derrota/cancelamento devolve o jogador à investigação. Pistas, aliados e decisão final determinam epílogo e um Eco temporário.

```text
Inicialização Android
 → MainActivity + XML base
 → evento do usuário
 → compositor/controlador Java
 → regra e atualização de estado
 → renderização de Views/Canvas ou trabalho HTTP assíncrono
 → callback/resultado
 → novo estado e próxima tela

Para lutas: Intent → FightActivity → BattleEngine → artes/HUD → resultado → chamador
Para ilha: JSON → WorldRules/WorldMap → WorldArt → interação → flags → ponte de luta
```

Para estudar uma mudança, siga a entrada que a dispara, o estado que ela altera, o método que consome esse estado e a superfície que o apresenta. As tabelas deste documento e o inventário abaixo localizam essas quatro partes sem exigir inferir comportamento pelo nome de um arquivo.

## 23. Inventário rastreável da base

O inventário complementar abaixo é derivado dos arquivos existentes nesta análise. Agrupa binários equivalentes e apresenta entradas de método/intervalos de responsabilidade para facilitar abrir a fonte. Não substitui as explicações anteriores nem significa que métodos sem chamada sejam ativos.


### 23.1 Fontes Java de produção

Caminhos relativos à raiz do projeto; os links abrem o arquivo correspondente. A contagem considera linhas físicas: métodos compactados em uma linha podem representar muito mais lógica do que seu tamanho sugere.

| Arquivo | Linhas físicas | Bytes UTF-8 | Análise |
|---|---:|---:|---|
| [ArcReactorView.java](app/src/main/java/com/example/projetomarvel/ArcReactorView.java) | 21 | 2724 | §9.19 |
| [AttackEffects.java](app/src/main/java/com/example/projetomarvel/AttackEffects.java) | 160 | 7794 | §9.16 |
| [BattleEngine.java](app/src/main/java/com/example/projetomarvel/BattleEngine.java) | 785 | 46185 | §9.12 |
| [BattleHubReference.java](app/src/main/java/com/example/projetomarvel/BattleHubReference.java) | 109 | 6641 | §9.10 |
| [CatalogStore.java](app/src/main/java/com/example/projetomarvel/CatalogStore.java) | 19 | 2177 | §9.4 |
| [CharacterPortraitView.java](app/src/main/java/com/example/projetomarvel/CharacterPortraitView.java) | 32 | 1238 | §9.15 |
| [CharacterTranslator.java](app/src/main/java/com/example/projetomarvel/CharacterTranslator.java) | 16 | 1273 | §9.5 |
| [CombatEffects.java](app/src/main/java/com/example/projetomarvel/CombatEffects.java) | 229 | 13588 | §9.17 |
| [ComicVineClient.java](app/src/main/java/com/example/projetomarvel/ComicVineClient.java) | 179 | 8463 | §9.3 |
| [FightActivity.java](app/src/main/java/com/example/projetomarvel/FightActivity.java) | 731 | 37995 | §9.11 |
| [FighterArtView.java](app/src/main/java/com/example/projetomarvel/FighterArtView.java) | 586 | 29324 | §9.13 |
| [FighterSprites.java](app/src/main/java/com/example/projetomarvel/FighterSprites.java) | 211 | 10022 | §9.14 |
| [GauntletView.java](app/src/main/java/com/example/projetomarvel/GauntletView.java) | 99 | 3674 | §9.20 |
| [GemArtView.java](app/src/main/java/com/example/projetomarvel/GemArtView.java) | 18 | 1621 | §9.21 |
| [GeminiClient.java](app/src/main/java/com/example/projetomarvel/GeminiClient.java) | 82 | 4120 | §9.6 |
| [HeroEmblemView.java](app/src/main/java/com/example/projetomarvel/HeroEmblemView.java) | 31 | 3462 | §9.23 |
| [HomeReference.java](app/src/main/java/com/example/projetomarvel/HomeReference.java) | 102 | 4713 | §9.2 |
| [MainActivity.java](app/src/main/java/com/example/projetomarvel/MainActivity.java) | 708 | 77758 | §9.1 |
| [QuizGenerator.java](app/src/main/java/com/example/projetomarvel/QuizGenerator.java) | 86 | 4986 | §9.7 |
| [ScreenFragment.java](app/src/main/java/com/example/projetomarvel/ScreenFragment.java) | 17 | 987 | §9.8 |
| [SplashCinematicView.java](app/src/main/java/com/example/projetomarvel/SplashCinematicView.java) | 63 | 3463 | §9.18 |
| [StoryArtView.java](app/src/main/java/com/example/projetomarvel/StoryArtView.java) | 18 | 1738 | §9.22 |
| [StoryMode.java](app/src/main/java/com/example/projetomarvel/StoryMode.java) | 232 | 31388 | §9.9 |
| [Typography.java](app/src/main/java/com/example/projetomarvel/Typography.java) | 20 | 1176 | §9.24 |
| [VisitWorldActivity.java](app/src/main/java/com/example/projetomarvel/VisitWorldActivity.java) | 106 | 40568 | §9.25 |
| [WorldArt.java](app/src/main/java/com/example/projetomarvel/WorldArt.java) | 38 | 8681 | §9.30 |
| [WorldBattleBridge.java](app/src/main/java/com/example/projetomarvel/WorldBattleBridge.java) | 11 | 1884 | §9.32 |
| [WorldCanvas.java](app/src/main/java/com/example/projetomarvel/WorldCanvas.java) | 15 | 2745 | §9.31 |
| [WorldEnemyArt.java](app/src/main/java/com/example/projetomarvel/WorldEnemyArt.java) | 28 | 2095 | §9.33 |
| [WorldJson.java](app/src/main/java/com/example/projetomarvel/WorldJson.java) | 18 | 2529 | §9.28 |
| [WorldMap.java](app/src/main/java/com/example/projetomarvel/WorldMap.java) | 22 | 3934 | §9.29 |
| [WorldRules.java](app/src/main/java/com/example/projetomarvel/WorldRules.java) | 2353 | 192505 | §9.26 |
| [WorldValues.java](app/src/main/java/com/example/projetomarvel/WorldValues.java) | 79 | 10147 | §9.27 |

### 23.2 Manifest, layouts, recursos XML e testes

| Caminho | Papel e análise |
|---|---|
| [app/src/main/AndroidManifest.xml](app/src/main/AndroidManifest.xml) | Componentes e permissões; §12 |
| [app/src/main/res/anim/screen_enter.xml](app/src/main/res/anim/screen_enter.xml) | Transição de Fragment; §11.1 |
| [app/src/main/res/anim/screen_exit.xml](app/src/main/res/anim/screen_exit.xml) | Transição de Fragment; §11.1 |
| [app/src/main/res/drawable/bg_fight_controls.xml](app/src/main/res/drawable/bg_fight_controls.xml) | Ícone, fundo ou vetor; §11.1 |
| [app/src/main/res/drawable/ic_gauntlet.xml](app/src/main/res/drawable/ic_gauntlet.xml) | Ícone, fundo ou vetor; §11.1 |
| [app/src/main/res/drawable/ic_gem_lock.xml](app/src/main/res/drawable/ic_gem_lock.xml) | Ícone, fundo ou vetor; §11.1 |
| [app/src/main/res/drawable/ic_launcher_background.xml](app/src/main/res/drawable/ic_launcher_background.xml) | Ícone, fundo ou vetor; §11.1 |
| [app/src/main/res/drawable/ic_launcher_foreground.xml](app/src/main/res/drawable/ic_launcher_foreground.xml) | Ícone, fundo ou vetor; §11.1 |
| [app/src/main/res/drawable/ic_nav_fight.xml](app/src/main/res/drawable/ic_nav_fight.xml) | Ícone, fundo ou vetor; §11.1 |
| [app/src/main/res/drawable/ic_nav_home.xml](app/src/main/res/drawable/ic_nav_home.xml) | Ícone, fundo ou vetor; §11.1 |
| [app/src/main/res/drawable/ic_nav_jarvis.xml](app/src/main/res/drawable/ic_nav_jarvis.xml) | Ícone, fundo ou vetor; §11.1 |
| [app/src/main/res/drawable/ic_nav_quiz.xml](app/src/main/res/drawable/ic_nav_quiz.xml) | Ícone, fundo ou vetor; §11.1 |
| [app/src/main/res/drawable/ic_nav_story.xml](app/src/main/res/drawable/ic_nav_story.xml) | Ícone, fundo ou vetor; §11.1 |
| [app/src/main/res/layout/activity_fight.xml](app/src/main/res/layout/activity_fight.xml) | Estrutura de tela ou card; §10 e §14 |
| [app/src/main/res/layout/activity_main.xml](app/src/main/res/layout/activity_main.xml) | Estrutura de tela ou card; §10 e §14 |
| [app/src/main/res/layout/activity_splash.xml](app/src/main/res/layout/activity_splash.xml) | Estrutura de tela ou card; §10 e §14 |
| [app/src/main/res/layout/activity_visit_world.xml](app/src/main/res/layout/activity_visit_world.xml) | Estrutura de tela ou card; §10 e §14 |
| [app/src/main/res/layout/fragment_screen.xml](app/src/main/res/layout/fragment_screen.xml) | Estrutura de tela ou card; §10 e §14 |
| [app/src/main/res/layout/item_hero.xml](app/src/main/res/layout/item_hero.xml) | Estrutura de tela ou card; §10 e §14 |
| [app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml](app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml) | Ícone adaptativo; §11.6 |
| [app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml](app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml) | Ícone adaptativo; §11.6 |
| [app/src/main/res/values-night/themes.xml](app/src/main/res/values-night/themes.xml) | Tema, texto ou cor; §11.6 |
| [app/src/main/res/values/colors.xml](app/src/main/res/values/colors.xml) | Tema, texto ou cor; §11.6 |
| [app/src/main/res/values/strings.xml](app/src/main/res/values/strings.xml) | Tema, texto ou cor; §11.6 |
| [app/src/main/res/values/themes.xml](app/src/main/res/values/themes.xml) | Tema, texto ou cor; §11.6 |
| [app/src/main/res/xml/backup_rules.xml](app/src/main/res/xml/backup_rules.xml) | Regras de backup; §12.1 |
| [app/src/main/res/xml/data_extraction_rules.xml](app/src/main/res/xml/data_extraction_rules.xml) | Regras de backup; §12.1 |
| [app/src/androidTest/assets/world-device-fixtures.json](app/src/androidTest/assets/world-device-fixtures.json) | Teste instrumentado ou fixture; §20.4 |
| [app/src/androidTest/java/com/example/projetomarvel/ExampleInstrumentedTest.java](app/src/androidTest/java/com/example/projetomarvel/ExampleInstrumentedTest.java) | Teste instrumentado ou fixture; §20.4 |
| [app/src/androidTest/java/com/example/projetomarvel/VisitWorldFlowTest.java](app/src/androidTest/java/com/example/projetomarvel/VisitWorldFlowTest.java) | Teste instrumentado ou fixture; §20.4 |
| [app/src/test/java/com/example/projetomarvel/BattleEngineChecks.java](app/src/test/java/com/example/projetomarvel/BattleEngineChecks.java) | Teste local/checks; §20.3 |
| [app/src/test/java/com/example/projetomarvel/BattleEngineTest.java](app/src/test/java/com/example/projetomarvel/BattleEngineTest.java) | Teste local/checks; §20.3 |
| [app/src/test/java/com/example/projetomarvel/ExampleUnitTest.java](app/src/test/java/com/example/projetomarvel/ExampleUnitTest.java) | Teste local/checks; §20.3 |
| [app/src/test/java/com/example/projetomarvel/WorldGenericBattleTest.java](app/src/test/java/com/example/projetomarvel/WorldGenericBattleTest.java) | Teste local/checks; §20.3 |

### 23.3 Configuração, dados e documentação preexistente

| Caminho | Papel e análise |
|---|---|
| [.gitignore](.gitignore) | Exclusões de versionamento; §13.6 |
| [app/.gitignore](app/.gitignore) | Saídas do módulo; §13.6 |
| [settings.gradle.kts](settings.gradle.kts) | Repositórios, nome e módulos; §13.1 |
| [build.gradle.kts](build.gradle.kts) | Plugin Android na raiz; §13.2 |
| [app/build.gradle.kts](app/build.gradle.kts) | SDK, applicationId, Java, BuildConfig e dependências; §13.3 |
| [gradle.properties](gradle.properties) | Propriedades do build; §13.5 |
| [gradle/libs.versions.toml](gradle/libs.versions.toml) | Catálogo de versões; §13.4 |
| [gradle/gradle-daemon-jvm.properties](gradle/gradle-daemon-jvm.properties) | Seleção da JVM do Gradle; §13.5 |
| [gradle/wrapper/gradle-wrapper.properties](gradle/wrapper/gradle-wrapper.properties) | Distribuição do wrapper; §13.5 |
| [gradle/wrapper/gradle-wrapper.jar](gradle/wrapper/gradle-wrapper.jar) | Bootstrap binário do wrapper; §13.5 |
| [gradlew](gradlew) | Launcher shell do wrapper; §13.5 |
| [gradlew.bat](gradlew.bat) | Launcher Windows do wrapper; §13.5 |
| [app/src/main/keepRules/rules.keep](app/src/main/keepRules/rules.keep) | Arquivo de regras presente, somente comentários; §13.5 |
| [app/src/main/assets/visit_world/world-data.json](app/src/main/assets/visit_world/world-data.json) | Dados estruturais e narrativos da ilha; §11.4 |
| [README.md](README.md) | Orientações e histórico do projeto; §13.6 e §20.5 |
| [VISITAR_MUNDO_ANDROID.md](VISITAR_MUNDO_ANDROID.md) | Registro anterior de entrega/validação do mundo; §20.5 |

`local.properties` é configuração local sensível: sua função e os nomes de propriedades consumidas são descritos em §13.3/§13.5; os valores não fazem parte deste inventário. `.idea/.gitignore`, `AndroidProjectSystem.xml`, `compiler.xml`, `deploymentTargetSelector.xml`, `gradle.xml`, `misc.xml` e `runConfigurations.xml` são configurações de IDE agrupadas em §13.6. `.idea/workspace.xml` guarda estado local do editor e não é regra do aplicativo.

### 23.4 Recursos binários e licenças

| Diretório/grupo | Arquivos e cobertura |
|---|---|
| `app/src/main/assets/fonts/` | `Barlow-Bold.ttf`, `Barlow-Medium.ttf`, `Barlow-Regular.ttf`, `Barlow-SemiBold.ttf`, `BebasNeue-Regular.ttf`, `Cinzel.ttf`, `OFL-barlow.txt`, `OFL-bebasneue.txt`, `OFL-cinzel.txt`, `OFL-rajdhani.txt`, `Rajdhani-Bold.ttf`, `Rajdhani-Medium.ttf`, `Rajdhani-SemiBold.ttf`; 9 TTF e 4 licenças; §11.3 |
| `app/src/main/res/drawable-nodpi/` | `fighter_black_panther.png`, `fighter_black_widow.png`, `fighter_captain_america.png`, `fighter_captain_marvel.png`, `fighter_doctor_doom.png`, `fighter_doctor_strange.png`, `fighter_hulk.png`, `fighter_iron_man.png`, `fighter_scarlet_witch.png`, `fighter_spider_man.png`, `fighter_thanos.png`, `fighter_thor.png`, `fighter_wolverine.png`; 13 imagens, associação por herói em §11.2 |
| `app/src/main/assets/visit_world/terrain/` | Todos os 150 arquivos `{x}-{y}.png`, x inteiro de 0 a 14 e y inteiro de 0 a 9. Grade, tamanho e acesso descritos em §11.5/WorldArt |
| `app/src/main/res/mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/` | `ic_launcher.webp` e `ic_launcher_round.webp` em cada uma das cinco densidades: 10 raster de launcher; §11.6 |

Não foram localizados assets de áudio. Classes compiladas, APKs, relatórios de execução, arquivos de cache e dependências baixadas pertencem aos diretórios gerados agrupados em §13.6; sua análise não substitui a das fontes.

### 23.5 Localização das funções compiladas de Fraturas

Cada entrada abaixo é o início físico de um instalador privado de `WorldRules.java`. O nome depois de `install_` identifica a função/valor registrado no Scope global. O método `run` despacha por esse nome. A interpretação e os efeitos de cada grupo estão em §9.26.

| Instalador | Linha em WorldRules.java |
|---|---:|
| `install_slotIdentity()` | 6 |
| `install_storyIdentity()` | 11 |
| `install_combatantAllowed()` | 23 |
| `install_heroById()` | 38 |
| `install_defaultDraft()` | 46 |
| `install_characterFromDraft()` | 50 |
| `install_npcCharacter()` | 58 |
| `install_clueCount()` | 72 |
| `install_fullAllies()` | 80 |
| `install_trust()` | 88 |
| `install_gesture()` | 95 |
| `install_collectClue()` | 110 |
| `install_refreshProgress()` | 122 |
| `install_recordChoice()` | 187 |
| `install_isVisible()` | 221 |
| `install_visibleNPCs()` | 230 |
| `install_encounterAllowed()` | 238 |
| `install_completeEncounter()` | 249 |
| `install_MISSIONS()` | 283 |
| `install_objective()` | 285 |
| `install_targetLocation()` | 338 |
| `install_resolveEnding()` | 347 |
| `install_endingSummary()` | 354 |
| `install_breachCast()` | 368 |
| `install_line()` | 384 |
| `install_talk()` | 389 |
| `install_opt()` | 395 |
| `install_choice()` | 404 |
| `install_done()` | 412 |
| `install_remember()` | 421 |
| `install_names()` | 429 |
| `install_protagonist()` | 431 |
| `install_gesturePrompt()` | 437 |
| `install_repeat()` | 453 |
| `install_dpDialogue()` | 485 |
| `install_dialogueFor()` | 533 |
| `install_narrativeDialogueFor()` | 544 |
| `install_blockedText()` | 743 |
| `install_narrativeScene()` | 749 |
| `install_encounterScript()` | 791 |
| `install_objectDialogue()` | 824 |
| `install_OUTLINE()` | 925 |
| `install_shade()` | 927 |
| `install_drawSprite()` | 938 |
| `install_NPC_PHASE()` | 1590 |
| `install_PAL()` | 1599 |
| `install_draw_tile()` | 1601 |
| `install_draw_prop()` | 1841 |
| `install_draw_object()` | 2302 |

### 23.6 Limite desta fotografia

O documento registra os arquivos acessíveis nesta data. Linhas, números e relações podem mudar após novas edições. Nenhum código do aplicativo foi alterado nesta tarefa. A conferência documental incluiu cobertura das 33 fontes Java de produção, seis layouts, recursos XML, testes e configurações listados; compilação e execução em dispositivo continuam fora do escopo desta análise estática.
