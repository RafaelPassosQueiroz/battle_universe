# Atualização nativa — protótipo V3

Implementada na branch `feat/new-layout`, sobre o aplicativo existente. O HTML/CSS/JS foi usado como referência de desenho e animação; o APK executa Java, Views e Canvas Android. Não há WebView, interpretador JavaScript, sprites PNG de personagens nem dependências novas de rede.

## O que mudou

- Os 13 lutadores usam as mesmas formas, cores, pivôs, acessórios e poses do V3 final. Isso inclui o cabelo curto de Hulk/Estranho e a abertura pequena da máscara do Capitão abaixo dos olhos.
- `FighterArtView` é a fachada comum dos previews, Splash, História e combate. `NativeFighterArt`, `FighterPose` e `FighterVisual` separam desenho, poses e estado de apresentação.
- Personalizáveis mantêm pele, cabelo, porte, estilo, cor e os seis poderes. Doom Bots recebem corpo articulado, máscara metálica e reator na mesma linguagem gráfica.
- `AsgardScene` reproduz os planos, palácio, ponte, luzes e partículas do cenário aprovado. `BattleCamera` acompanha os dois combatentes com os coeficientes, limites e zoom do protótipo.
- A arena tem 2.600 unidades, com pontos iniciais equivalentes aos da web. O alcance anterior de escudo/Mjölnir foi explicitado em uma constante para não crescer acidentalmente com a arena.
- HUD, minimapa, anúncios, pausa, resultado, revanche e retorno à seleção foram atualizados. Os efeitos de combate usam as trajetórias e colisões do motor existente, com a apresentação do protótipo.
- O Modo Lutas tem menu, arquivo dos 13, seleção em duas etapas, preview, passivas, dificuldade e pré-luta. Não existe “Como jogar” nem espaço reservado para ele. Conforme confirmado, os 13 estão liberados apenas em Lutas; os flags e a progressão de Quiz/História permanecem intactos.
- Menus continuam em retrato; FightActivity continua em paisagem. Encerrar a luta devolve a orientação à Activity anterior, incluindo a exploração em paisagem.

## Preservação

`tools/verify_native_scope.py` compara o código com a revisão anterior `efd18021884f5021b7c67dd075b0ca30457c4dd3` e verifica:

- O bloco Java que cria, posiciona e conecta os controles e os atributos XML de seu contêiner permanecem idênticos.
- Home, Jarvis, Quiz e as cinco abas não foram alterados fora dos pontos de integração específicos de Lutas.
- StoryMode, WorldBattleBridge, WorldArt/Canvas/Map/Rules/Json/Values permanecem iguais. No Visite Mundo, apenas os retratos da grade Marvel de escolha mudaram; a exploração e a apresentação de encontro continuam pixeladas.
- BattleEngine só mudou nas dimensões, posições iniciais e constante explícita de alcance. Dano, CPU, dificuldades, status, cooldowns, passivas e regras de conclusão são os mesmos.
- Os três modos continuam abrindo a mesma FightActivity e o mesmo sistema visual.

As alterações que já existiam em `settings.gradle.kts` e `.idea` não foram modificadas por este trabalho.

## Referências e conferência

O código da pasta `version3` foi a referência principal. Os dois vídeos mais recentes foram examinados por amostras ao longo de toda a duração: o primeiro (215,27 s) mostra vários confrontos e poderes; o segundo (53 s) mostra arquivo de lutadores, seleção e luta entre Capitão e Homem de Ferro. Os desenhos foram transcritos para operações nativas; `tools/port_approved_art.py` permite repetir essa transcrição durante desenvolvimento, sem integrar Python/JS ao APK.

Validação:

- Compilação e geração de APK debug offline pelo Gradle.
- 17 testes JUnit, incluindo câmera, inimigos genéricos e integrações do motor.
- 477 cenários de comparação com as regras anteriores, em ambos os lados, para ataques, especiais, passivas, aliados, rewind e personalizáveis. A fixture `combat-baseline.tsv` foi produzida com o motor original da revisão acima. As expectativas antigas, que ainda descreviam regras anteriores às já existentes na branch, foram substituídas por essa comparação reproduzível.
- 39 simulações de CPU (13 personagens × três dificuldades), com vida e posições limitadas.
- Renderização com o Canvas/Skia nativo do Android, através do Layoutlib já instalado no Android Studio: elenco completo, 286 combinações de ação/direção, seis poderes personalizados, Doom Bot, Asgard e formas de efeitos.

As imagens em `build/visual-reference` são renderizações nativas e folhas de análise dos vídeos. Não são capturas de um APK executando em telefone. Não havia aparelho ADB nem imagem de emulador disponível; interação touch, desempenho e transições entre Activities ainda precisam de conferência em dispositivo. O APK foi gerado e os testes locais passaram.

## Testar

Abra o projeto nesta branch e execute `app` no Android Studio, ou utilize `app/build/outputs/apk/debug/app-debug.apk`.

Fluxos para conferir em aparelho:

1. Splash curta → Home; verificar as abas antigas.
2. Lutas → selecionar jogador/CPU → dificuldade → pré-luta → combate horizontal.
3. Andar até as bordas, afastar/aproximar lutadores, pular, defender, usar poderes/especiais e pausar.
4. Vitória/derrota → revanche, nova seleção e retorno a Lutas.
5. História com herói Marvel e com personagem personalizado → batalha → continuar história.
6. Visite Mundo → exploração pixelada → encontro → batalha → retorno ao mesmo mundo.

Comandos de desenvolvimento:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug --offline
python tools/verify_native_scope.py
.\tools\render-native-preview.ps1
```

Neste ambiente foi necessário usar um diretório temporário curto para os sockets locais do Java: `JAVA_TOOL_OPTIONS=-Djdk.net.unixdomain.tmpdir=C:\Users\Public -Djava.io.tmpdir=C:\Users\Public`. O JDK do Android Studio foi validado; a cópia portátil de [OpenJDK da Microsoft](https://learn.microsoft.com/en-us/java/openjdk/download-major-urls) utilizada durante diagnóstico ficou somente em `build/tooling`, fora do aplicativo. Nenhuma configuração global de Java/Android Studio foi alterada.
