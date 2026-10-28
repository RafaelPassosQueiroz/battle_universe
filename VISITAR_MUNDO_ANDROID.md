# VISITAR MUNDO — implementação Android

Projeto atualizado: `C:\Users\rafaelqueiroz-ieg\AndroidStudioProjects\ProjetoMarvel`.

## Acesso

Story Mode → VISITAR MUNDO → Nome → Aparência → Cor → Poder → ilha.

O modo usa Java, XML e Canvas Android em Landscape. A implementação Web em `C:\Users\rafaelqueiroz-ieg\code\js_marvel` foi utilizada como referência e permaneceu intacta. Não há WebView ou execução de JavaScript no modo nativo.

## Conteúdo e integração

- 29 locais, campanha Fraturas, escolhas, pistas, relacionamentos, desbloqueios e três finais provenientes do Web.
- Geometria, cores e animações dos sprites e elementos ambientais reproduzidas a partir dos comandos de desenho originais; terreno original armazenado em imagens.
- Caracterização com quatro etapas, protagonista personalizado e os 13 combatentes oficiais.
- Deadpool permanece como NPC interativo e não participa das lutas nem da seleção de protagonistas de combate, conforme esclarecimento do usuário.
- Movimento em quatro direções, câmera, colisões, interação manual por proximidade, diário e mapa.
- Controle em cruz com células de 64dp para toque.
- Encontros narrativos encaminham os personagens para o FightActivity existente. Vitória conclui o encontro; derrota ou cancelamento retornam sem concluir o evento.
- Personagem genérico: bola de energia com 8 de dano; dash com recarga de 10 segundos, sem dano ou empurrão, limitado pelo adversário e pelas paredes.
- Saída inicia uma nova caracterização na próxima visita; a sessão ativa mantém posição e progresso durante o retorno da batalha.

## Limite das alterações existentes

Foram modificados apenas AndroidManifest.xml, StoryMode.java, FightActivity.java, BattleEngine.java e CombatEffects.java para registrar/acessar o modo e integrar suas batalhas. Os módulos World*, VisitWorldActivity, layout, assets e testes foram adicionados. Nenhum arquivo existente foi removido. Home, Jarvis, Quiz, a versão Web e o projeto antigo em Downloads não foram editados.

## Validação executada

- APK debug e APK dos testes Android compilados com sucesso.
- 4 testes de combate genérico aprovados: dano, recarga, parede, aproximação e ausência de empurrão/dano no dash.
- 1.890 cenários narrativos comparados com o Web: diálogos, escolhas, progressão e finais coincidem.
- Colisões de água e portões bloqueados/liberados verificadas.
- 4.040 quadros de sprites e 1.536 quadros de construções/ambiente comparados com os comandos de desenho Web.
- Auditoria de arquivos: cinco arquivos existentes alterados, nenhum removido.

Essas comparações verificam os dados e comandos de desenho; não substituem uma inspeção do resultado visual e do desempenho em um celular.

## Validação pendente em Android

Não havia aparelho conectado nem imagem de emulador instalada. Os testes instrumentados VisitWorldFlowTest foram compilados, mas não executados. Eles cobrem acesso pelo Story Mode, caracterização, controle por toque, câmera, colisões, interação, batalha com protagonista personalizado/herói, adversário herói/vilão/genérico, variante, vitória, derrota, cancelamento, desbloqueio e nova sessão.

Com um Android conectado e depuração USB autorizada, execute `connectedDebugAndroidTest` no projeto e confira também a aparência em Landscape.

A suíte antiga de combate apresenta sete falhas reproduzidas também no código anterior ao patch, relacionadas a expectativas antigas de valores/regras de Thanos, Thor, Wolverine, Super Força, Feiticeira e Unibeam. Não foram alterados esses comportamentos para fazer os testes antigos passarem.

## Arquivos de entrega

- APK: `app\build\outputs\apk\debug\app-debug.apk`.
- Testes de dispositivo: `app\src\androidTest\java\com\example\projetomarvel\VisitWorldFlowTest.java`.
- Testes de combate: `app\src\test\java\com\example\projetomarvel\WorldGenericBattleTest.java`.

