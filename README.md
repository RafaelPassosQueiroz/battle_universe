________________pt-BR________________

# ⚡ Marvel Universe

<p align="center">
  <img src="https://img.shields.io/badge/Android-Native-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/Java-11+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/Comic_Vine-API-E62429?style=for-the-badge" alt="Comic Vine API">
  <img src="https://img.shields.io/badge/Gemini-AI-4285F4?style=for-the-badge&logo=googlegemini&logoColor=white" alt="Gemini">
</p>

## 📌 Sobre o Projeto

O **Marvel Universe** é um aplicativo Android nativo desenvolvido em Java que reúne diferentes experiências inspiradas no universo Marvel em uma única aplicação.

O projeto combina catálogo de personagens, inteligência artificial, quiz, batalhas 2D, campanha narrativa e exploração de um mundo em estilo RPG.

---

## 🚀 Principais Funcionalidades

### 🦸 Catálogo de Personagens

O aplicativo utiliza a **Comic Vine API** para buscar personagens da Marvel e apresentar informações como:

- nome;
- identidade;
- descrição;
- poderes;
- imagem do personagem.

Os dados são armazenados localmente utilizando **SQLite**, permitindo que o catálogo seja carregado antes de uma nova sincronização com a API.

---

### 🤖 JARVIS

O aplicativo possui um assistente inspirado no **JARVIS**, integrado ao **Google Gemini**.

O usuário pode fazer perguntas relacionadas ao universo Marvel e receber respostas contextualizadas sobre personagens, histórias, poderes, filmes, quadrinhos, artefatos e outros elementos.

---

### 💎 Quiz das Joias do Infinito

O modo Quiz possui **24 perguntas divididas em 6 etapas**, com dificuldade progressiva.

Cada etapa representa uma Joia do Infinito:

- Espaço;
- Mente;
- Realidade;
- Poder;
- Tempo;
- Alma.

As perguntas podem ser geradas dinamicamente utilizando Gemini.

Ao conquistar as seis Joias, o jogador desbloqueia **Thanos** para o modo de batalha.

---

### ⚔️ Batalhas

O aplicativo possui um sistema próprio de combate 2D com personagens da Marvel.

Entre as mecânicas estão:

- movimentação;
- salto;
- soco;
- chute;
- ataques à distância;
- defesa;
- habilidades especiais;
- diferentes níveis de dificuldade;
- barras de vida;
- efeitos visuais de combate.

Entre os personagens disponíveis estão Homem-Aranha, Homem de Ferro, Wolverine, Capitão América, Thor, Hulk, Pantera Negra, Doutor Estranho, Viúva Negra, Feiticeira Escarlate, Capitã Marvel, Dr. Doom e Thanos.

---

### 📖 Story Mode

O **Modo História** apresenta uma campanha narrativa dividida em etapas e batalhas.

O jogador pode escolher um personagem existente ou criar seu próprio protagonista, definindo características como:

- nome;
- aparência;
- cor;
- poder;
- personalidade.

As escolhas realizadas influenciam a progressão da narrativa.

A conclusão da campanha permite desbloquear **Dr. Doom** para o modo de batalha.

---

### 🌎 Visitar Mundo

O modo **Visitar Mundo** transforma o projeto em uma experiência de exploração no estilo RPG.

O jogador pode:

- criar seu próprio personagem ou utilizar um herói;
- explorar diferentes regiões do mapa;
- movimentar-se livremente pelo cenário;
- conversar com NPCs;
- tomar decisões em diálogos;
- encontrar personagens e inimigos;
- iniciar batalhas diretamente durante a exploração;
- acompanhar objetivos e acontecimentos da história.

O mapa utiliza terrenos divididos em tiles e integra o sistema de exploração ao mesmo mecanismo de combate utilizado nas batalhas.

---

## 🔌 Integrações

### Comic Vine API

Responsável pelos dados do catálogo de personagens Marvel.

### Google Gemini

Utilizado em funcionalidades de Inteligência Artificial, incluindo:

- assistente JARVIS;
- geração das perguntas do Quiz.

### Google ML Kit

Utilizado para auxiliar na tradução de conteúdos apresentados pelo aplicativo.

---

## 🗃️ Armazenamento Local

O projeto utiliza **SQLite** para armazenar localmente informações do catálogo.

Isso permite:

```text
Comic Vine API
      ↓
Sincronização
      ↓
SQLite
      ↓
Catálogo no aplicativo
```

O cache local permite que dados já obtidos sejam apresentados antes de uma nova consulta à API.

---

## 🏗️ Estrutura do Projeto

```text
ProjetoMarvel/
│
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/example/projetomarvel/
│       │   ├── res/
│       │   └── assets/
│       │       └── visit_world/
│       │
│       ├── test/
│       └── androidTest/
│
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

Entre os principais componentes estão:

```text
MainActivity
FightActivity
BattleEngine
StoryMode
VisitWorldActivity
ComicVineClient
GeminiClient
QuizGenerator
CatalogStore
```

---

## 🛠️ Tecnologias

- Java
- Android SDK
- AndroidX
- XML
- Gradle / Kotlin DSL
- SQLite
- RecyclerView
- Canvas API
- Comic Vine API
- Google Gemini API
- Google ML Kit Translation
- JSON
- REST APIs

---

## ⚙️ Como Executar

### Pré-requisitos

- Android Studio;
- Android SDK;
- dispositivo Android ou emulador;
- conexão com a internet para os recursos online.

Clone ou abra o projeto no Android Studio.

Configure as chaves no arquivo local:

```text
local.properties
```

Exemplo:

```properties
COMIC_VINE_API_KEY=sua_chave_comic_vine
GEMINI_API_KEY=sua_chave_gemini
```

Depois:

1. sincronize o Gradle;
2. selecione um dispositivo ou emulador;
3. execute o módulo `app`.

O aplicativo suporta Android a partir da **API 24**.

---

## 🔐 Segurança

As chaves das APIs não devem ser versionadas no GitHub.

O projeto lê as credenciais através de `local.properties`, que deve permanecer fora do controle de versão.

Nunca publique:

- `COMIC_VINE_API_KEY`;
- `GEMINI_API_KEY`;
- tokens;
- arquivos contendo credenciais.

---

## 🎯 Objetivo

O projeto demonstra conceitos de:

- desenvolvimento Android nativo;
- integração com APIs REST;
- consumo de Inteligência Artificial;
- armazenamento local com SQLite;
- desenvolvimento de jogos e sistemas de combate;
- construção de interfaces customizadas;
- gerenciamento de estados;
- criação de experiências interativas e narrativas.

---

## ⚠️ Contexto

Projeto desenvolvido com finalidade **acadêmica e de portfólio**.

Este é um projeto não oficial e não possui vínculo com a Marvel. Personagens, nomes e propriedades relacionadas pertencem aos seus respectivos detentores.

<hr>

________________en-US________________

# ⚡ Marvel Universe

<p align="center">
  <img src="https://img.shields.io/badge/Android-Native-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/Java-11+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/Comic_Vine-API-E62429?style=for-the-badge" alt="Comic Vine API">
  <img src="https://img.shields.io/badge/Gemini-AI-4285F4?style=for-the-badge&logo=googlegemini&logoColor=white" alt="Gemini">
</p>

## 📌 About the Project

**Marvel Universe** is a native Android application developed in Java that combines multiple Marvel-inspired experiences in a single application.

The project includes a character catalog, Artificial Intelligence, quizzes, 2D battles, a story campaign, and an explorable RPG-style world.

---

## 🚀 Main Features

### 🦸 Character Catalog

The application uses the **Comic Vine API** to retrieve Marvel characters and information such as:

- name;
- identity;
- description;
- powers;
- character image.

Data is cached locally using **SQLite**, allowing previously retrieved characters to be displayed before a new API synchronization.

---

### 🤖 JARVIS

The application includes a **JARVIS-inspired assistant** integrated with **Google Gemini**.

Users can ask questions about the Marvel universe, including characters, stories, powers, movies, comics, artifacts, and other topics.

---

### 💎 Infinity Stones Quiz

The Quiz mode contains **24 questions divided into 6 progressively harder stages**.

Each stage represents an Infinity Stone:

- Space;
- Mind;
- Reality;
- Power;
- Time;
- Soul.

Questions can be dynamically generated using Gemini.

Collecting all six Stones unlocks **Thanos** as a playable battle character.

---

### ⚔️ Battles

The application includes its own 2D combat system featuring Marvel characters.

Combat mechanics include:

- movement;
- jumping;
- punches;
- kicks;
- ranged attacks;
- blocking;
- special abilities;
- difficulty levels;
- health bars;
- visual combat effects.

Available characters include Spider-Man, Iron Man, Wolverine, Captain America, Thor, Hulk, Black Panther, Doctor Strange, Black Widow, Scarlet Witch, Captain Marvel, Doctor Doom, and Thanos.

---

### 📖 Story Mode

**Story Mode** provides a narrative campaign divided into multiple stages and battles.

Players can select an existing character or create their own protagonist by defining:

- name;
- appearance;
- color;
- power;
- personality.

Player decisions influence the progression of the story.

Completing the campaign unlocks **Doctor Doom** for the battle mode.

---

### 🌎 Visit World

The **Visit World** mode expands the project into an RPG-style exploration experience.

Players can:

- create a custom character or select a hero;
- explore different map regions;
- freely move through the environment;
- interact with NPCs;
- make dialogue choices;
- encounter characters and enemies;
- enter battles directly from the world;
- follow objectives and story events.

The world uses tile-based terrain and integrates exploration with the same combat system used by the battle mode.

---

## 🔌 Integrations

### Comic Vine API

Provides Marvel character data for the catalog.

### Google Gemini

Used for Artificial Intelligence features including:

- JARVIS assistant;
- Quiz question generation.

### Google ML Kit

Used to assist with content translation inside the application.

---

## 🗃️ Local Storage

The application uses **SQLite** to store character catalog information locally.

```text
Comic Vine API
      ↓
Synchronization
      ↓
SQLite
      ↓
Application Catalog
```

The local cache allows previously retrieved data to be displayed before performing another API request.

---

## 🏗️ Project Structure

```text
ProjetoMarvel/
│
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/example/projetomarvel/
│       │   ├── res/
│       │   └── assets/
│       │       └── visit_world/
│       │
│       ├── test/
│       └── androidTest/
│
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

Main components include:

```text
MainActivity
FightActivity
BattleEngine
StoryMode
VisitWorldActivity
ComicVineClient
GeminiClient
QuizGenerator
CatalogStore
```

---

## 🛠️ Technologies

- Java
- Android SDK
- AndroidX
- XML
- Gradle / Kotlin DSL
- SQLite
- RecyclerView
- Canvas API
- Comic Vine API
- Google Gemini API
- Google ML Kit Translation
- JSON
- REST APIs

---

## ⚙️ Running the Project

### Requirements

- Android Studio;
- Android SDK;
- Android device or emulator;
- internet connection for online features.

Clone or open the project using Android Studio.

Configure the API keys in:

```text
local.properties
```

Example:

```properties
COMIC_VINE_API_KEY=your_comic_vine_key
GEMINI_API_KEY=your_gemini_key
```

Then:

1. synchronize Gradle;
2. select an Android device or emulator;
3. run the `app` module.

The application supports Android devices starting from **API 24**.

---

## 🔐 Security

API keys must not be committed to GitHub.

The project reads credentials from `local.properties`, which should remain outside version control.

Never publish:

- `COMIC_VINE_API_KEY`;
- `GEMINI_API_KEY`;
- access tokens;
- files containing credentials.

---

## 🎯 Objective

The project demonstrates practical concepts related to:

- native Android development;
- REST API integration;
- Artificial Intelligence integration;
- local persistence with SQLite;
- game and combat system development;
- custom interface development;
- state management;
- interactive and narrative experiences.

---

## ⚠️ Context

This project was developed for **academic and portfolio purposes**.

It is an unofficial project and is not affiliated with Marvel. Characters, names, and related properties belong to their respective owners.
