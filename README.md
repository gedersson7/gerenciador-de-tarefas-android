# 📱 NexTask - Gestor de Tarefas Inteligente

![Kotlin](https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Material Design](https://img.shields.io/badge/Material_Design_3-757575?style=for-the-badge&logo=material-design&logoColor=white)

O **NexTask** é uma aplicação Android nativa desenvolvida como projeto prático para o curso de **Análise e Desenvolvimento de Sistemas (ADS) na Imepac**. O objetivo principal foi construir um ecossistema completo com operações de CRUD avançadas, persistência de dados na nuvem, paginação eficiente e regras estritas de segurança.

---

## 🚀 Requisitos Técnicos Atendidos (Funcionalidades)

* **Segurança e Isolamento de Dados (Multi-tenant):** O banco de dados Cloud Firestore está protegido por regras de acesso (`request.auth != null`). Cada tarefa criada é vinculada ao ID único do utilizador autenticado, garantindo total privacidade.
* **Operações de CRUD Completo:**
  * **Insert:** Registo de utilizadores (Firebase Authentication) e inserção de novas tarefas com categorias específicas (Firestore).
  * **Read:** Consultas simples e múltiplas com filtros dinâmicos por categoria.
  * **Update:** Atualização de títulos, descrições, categorias e alteração de estado (pendente para concluída).
  * **Delete:** Eliminação permanente de tarefas diretamente no histórico.
* **Busca Paginada de Múltiplos Registos:** Implementação de paginação real na listagem de tarefas. A aplicação faz a leitura em lotes de 5 documentos por vez (`limit(5)`) utilizando os cursores `startAfter` e `endBefore`, otimizando a memória e o consumo de dados.
* **Busca de Registo Único com Atualização:** Ao selecionar uma tarefa, o sistema realiza uma busca direcionada por ID no Firestore, carregando os detalhes específicos para permitir a edição imediata (Update).
* **Ordenação Dinâmica:** Utilização de Índices Compostos no Firebase para ordenar as listas em tempo real (Ordem Alfabética A-Z e Mais Recentes Primeiro) através de um `PopupMenu`.

---

## 🗺️ Estrutura da Aplicação (11 Telas)

A navegação foi projetada utilizando uma interface moderna em blocos (Dashboard), eliminando a necessidade de menus laterais complexos e melhorando a experiência do utilizador:

1. **FormLogin:** Autenticação e entrada de utilizadores.
2. **FormCadastro:** Registo de novos perfis integrado ao Firebase Auth.
3. **TelaEsqueciSenha:** Fluxo de recuperação de acesso à conta.
4. **TelaPrincipal (Dashboard):** Menu central interativo em blocos.
5. **FormNovaTarefa:** Efetua o cadastro de novas atividades (*Insert*).
6. **TelaListaTarefas:** Exibição paginada das atividades pendentes (*Read* múltiplo).
7. **TelaTarefasConcluidas:** Histórico de atividades finalizadas com ordenação própria.
8. **TelaDetalhesTarefa:** Visualização detalhada e edição da tarefa (*Read* único e *Update*).
9. **TelaCategorias:** Filtros rápidos por tipo de atividade.
10. **TelaPerfil:** Consulta e exibição dos dados do utilizador ativo.
11. **TelaSobre:** Informações institucionais e versão da aplicação.

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Kotlin
* **IDE:** Android Studio
* **Backend as a Service (BaaS):** Firebase (Authentication & Cloud Firestore)
* **Interface (UI/UX):** Material Design 3, ConstraintLayout, RecyclerView, PopupMenu.

---

## ⚙️ Como Executar o Projeto

1. Clone o repositório:
   ```bash
   git clone [https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git](https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git)
