# Guia rápido de tradução — como funciona e como replicar

## 1. O que o MarvelUniverse utiliza

O projeto utiliza **Google ML Kit Translation** para traduzir os textos da Comic Vine de **inglês para português**. A tradução é feita no aparelho; os modelos necessários precisam estar disponíveis para funcionar sem conexão. [Visão geral oficial do ML Kit](https://developers.google.com/ml-kit).

Na integração existente, não é necessária uma chave de tradução, nem configuração de Azure ou do Gemini. A chave da Comic Vine serve para buscar personagens; ela não participa da tradução. O destino configurado é `PORTUGUESE`, sem uma configuração específica de variante brasileira.

A tradução não acontece automaticamente em todas as telas: somente os textos enviados ao tradutor são processados.

## 2. Onde está cada responsabilidade

| Parte | Responsabilidade no projeto |
|---|---|
| `CharacterTranslator.java` | Configura os idiomas, prepara o modelo, solicita a tradução e devolve o original em falhas |
| `MainActivity.translateText(...)` | Consulta o cache em memória antes de solicitar nova tradução |
| Cards e detalhes na `MainActivity` | Escolhem o texto e atualizam o componente quando o resultado chega |
| `MainActivity.displayName(...)` | Converte alguns nomes conhecidos por uma tabela manual de condições |
| `app/build.gradle.kts` | Declara a biblioteca ML Kit |
| `AndroidManifest.xml` | Declara acesso à internet |

No catálogo, são traduzidos resumos, descrições e textos de poderes. Identidades civis são exibidas como recebidas. Nomes como “Spider-Man” → “Homem-Aranha” seguem o mapeamento manual, separado do tradutor automático.

```text
Comic Vine → texto original → consulta ao cache de traduções
                                    ├── encontrado → exibe tradução
                                    └── ausente → CharacterTranslator
                                                   → prepara modelo
                                                   → traduz
                                                   → callback atualiza a tela

Falha no preparo ou na tradução → devolve o texto original
```

## 3. Preparação para outro projeto

No Gradle do módulo `app`, adicione a dependência já utilizada aqui e sincronize o projeto:

```kotlin
dependencies {
    implementation("com.google.mlkit:translate:17.0.3")
}
```

Essa sintaxe pertence ao arquivo de configuração `build.gradle.kts`; a implementação da funcionalidade continua sendo Java. Se utilizar `build.gradle` em Groovy, a linha equivalente é `implementation 'com.google.mlkit:translate:17.0.3'`. O repositório `google()` deve estar configurado nos repositórios de dependências.

No Manifest, dentro de `<manifest>` e fora de `<application>`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

A documentação oficial exige Android API 23 ou superior; este projeto usa `minSdk = 24`. O fluxo do SDK é preparar o modelo, traduzir e liberar o cliente quando terminar. [Guia oficial de integração Android](https://developers.google.com/ml-kit/language/translation/android).

## 4. Classe Java que você pode reaproveitar

Crie `CharacterTranslator.java` no package do novo aplicativo. O exemplo abaixo reproduz a lógica existente, com a formatação expandida. **Troque a declaração de package pelo package do seu projeto.**

```java
package com.example.projetomarvel;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public final class CharacterTranslator {

    public interface Callback {
        void onTranslated(String value);
    }

    private final Translator translator;

    public CharacterTranslator() {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.PORTUGUESE)
                .build();

        translator = Translation.getClient(options);
    }

    public void translate(String source, Callback callback) {
        if (source == null || source.trim().isEmpty()) {
            callback.onTranslated(source == null ? "" : source);
            return;
        }

        DownloadConditions conditions = new DownloadConditions.Builder()
                .build();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(unused ->
                        translator.translate(source)
                                .addOnSuccessListener(callback::onTranslated)
                                .addOnFailureListener(error ->
                                        callback.onTranslated(source)))
                .addOnFailureListener(error ->
                        callback.onTranslated(source));
    }

    public void close() {
        translator.close();
    }
}
```

### Como ler essa classe

- **Idiomas:** `ENGLISH` é a origem; `PORTUGUESE` é o destino. Para outro par conhecido, altere essas constantes. Essa classe não detecta o idioma automaticamente.
- **Modelo:** `downloadModelIfNeeded` prepara o recurso necessário; chamá-lo novamente não significa forçar outro download.
- **Tradução:** só é solicitada depois do preparo bem-sucedido.
- **Callback:** entrega o resultado posteriormente. Não existe um retorno imediato de texto traduzido.
- **Falha:** ambos os caminhos de erro devolvem o original, mantendo conteúdo disponível.
- **Encerramento:** `close()` libera o cliente; não apaga o cache de modelos do SDK.

No código atual, o download não exige Wi-Fi. Para exigir essa condição em outra implementação, acrescente `.requireWifi()` antes de `.build()` nas condições. Isso pode adiar o preparo quando não houver Wi-Fi. A documentação recomenda considerar rede e armazenamento: modelos ocupam aproximadamente 30 MB. [Orientações sobre modelos](https://developers.google.com/ml-kit/language/translation/android).

## 5. Como ligar a uma tela Java + XML

O XML só precisa ter um `TextView` normal. Depois de carregar seu layout e recuperar esse componente, utilize uma instância compartilhada do helper pela tela:

```java
// Atributo da Activity:
private CharacterTranslator translator;

// Dentro de onCreate(), depois de setContentView(...):
translator = new CharacterTranslator();
TextView description = findViewById(R.id.description);

String original = "A hero who protects the city.";
description.setText(original);

translator.translate(original, translated -> {
    if (!isFinishing() && !isDestroyed()) {
        description.setText(translated);
    }
});
```

Esse é um trecho para inserir na sua Activity: importe `android.widget.TextView` e declare `@+id/description` no XML, ou substitua pelo ID que já existir. A proteção de ciclo de vida do exemplo é uma orientação para a réplica, não uma reprodução literal de todos os callbacks atuais.

Mostre primeiro o original para não deixar a tela vazia. O callback permite trocar o texto quando estiver pronto, sem bloquear a interface. Não utilize espera com `sleep`, loop ou chamada bloqueante para obter a tradução.

Se essa Activity for a proprietária do tradutor, libere-o quando ela for destruída:

```java
@Override
protected void onDestroy() {
    if (translator != null) {
        translator.close();
    }
    super.onDestroy();
}
```

Não feche o cliente depois de cada tradução nem continue usando uma instância fechada. No MarvelUniverse atual, a liberação está condicionada a `isFinishing()`; para replicar, vincule a liberação ao ciclo de vida do objeto que realmente possui o cliente. Em um Fragment, também cuide para não atualizar uma View depois de `onDestroyView()`.

## 6. Cache, lista e nomes: os cuidados essenciais

**Existem três armazenamentos diferentes:**

| Armazenamento | O que guarda |
|---|---|
| SQLite do catálogo | Dados originais recebidos da Comic Vine |
| `translations`, um `HashMap` da MainActivity | Texto original → tradução, durante a vida dessa Activity |
| Modelos gerenciados pelo ML Kit | Recursos linguísticos necessários ao processamento local |

O cache de traduções atual não é gravado no SQLite. Ele só memoriza resultados diferentes do original; falhas podem ser tentadas novamente. Se mudar os idiomas, inclua origem e destino na chave do seu cache para não misturar resultados.

**No RecyclerView:** uma tradução pode chegar depois de o card ter sido reutilizado para outro personagem. O projeto marca o card com o ID do personagem e só aplica o resultado se o ID continuar igual. Repita essa proteção em listas; nas telas de detalhes, confira se o item e a tela ainda correspondem ao pedido.

**Nos textos:** envie conteúdo sem marcação HTML. Evite traduzir identidades civis automaticamente. Mantenha um mapeamento próprio para nomes cuja versão em português precisa ser exata. Textos fixos de botões e menus podem ficar em `strings.xml`; essa biblioteca não traduz automaticamente os recursos do aplicativo.

**Na qualidade:** a tradução é automática e pode errar termos ou contexto. O helper atual devolve apenas uma string; uma tradução igual ao original não permite distinguir, sozinha, sucesso de falha. Se precisar exibir esse estado, amplie o contrato para informar também o resultado da operação.

## 7. Roteiro rápido para repetir depois

1. Configure a dependência, o repositório Google e a permissão de internet.
2. Crie o helper e escolha os idiomas.
3. Instancie o tradutor na tela ou componente proprietário.
4. Mostre o original e solicite a tradução pelo callback.
5. Reaproveite traduções em cache e proteja callbacks contra troca de item/tela.
6. Libere o cliente ao encerrar seu proprietário.

Ao validar a réplica, confira o primeiro uso conectado, o uso sem internet com modelo já preparado, a falta de modelo sem conexão, textos vazios e a rolagem rápida da lista. São verificações sugeridas; não foram executadas nesta tarefa documental.

**Para explicar em uma apresentação:** “A tradução fica em uma classe separada da interface. Ela prepara um modelo de inglês para português no aparelho e devolve o resultado de forma assíncrona. A tela mostra o original enquanto espera, reaproveita traduções em memória e mantém o original quando ocorre uma falha.”

Este guia foi produzido a partir da implementação local e da documentação oficial consultada em 21 de outubro de 2026. Foi criado somente este Markdown; o código do aplicativo permaneceu inalterado.
