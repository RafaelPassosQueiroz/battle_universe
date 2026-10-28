# Marvel Universe

Aplicativo Android nativo em Java, XML e AndroidX. A interface usa a estrutura do protótipo: splash, Home, Jarvis, Quiz e batalhas em landscape.

## Chave da Comic Vine

A chave não deve ser commitada nem enviada no chat. Acrescente-a ao arquivo local `local.properties` (já ignorado pelo Git):

```properties
COMIC_VINE_API_KEY=sua_chave
```

O Gradle disponibiliza o valor por `BuildConfig`; o app consulta os IDs da editora Marvel, busca os registros em lotes de `/characters`, confere `publisher.name` e salva o catálogo em SQLite. O cache abre primeiro e é sincronizado em segundo plano quando tem mais de 24 horas. Descrições e poderes são buscados sob demanda na tela de detalhes. Sem a chave, o app ainda abre, mas não sincroniza o catálogo.

Abra o projeto no Android Studio, sincronize o Gradle e execute o app em um dispositivo ou emulador Android. A arena solicita orientação landscape; as telas de navegação ficam em portrait.
