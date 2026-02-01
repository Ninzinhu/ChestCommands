# ChestCommands (Hytale)

ChestCommands é um sistema de menus configuráveis (estilo "Chest Commands") projetado para Hytale. O projeto é dividido em core (engine‑agnóstico), API e adapter Hytale para abrir UIs reais no servidor. O objetivo é permitir criar menus via YAML/JSON sem editar código de outros plugins — inclusive integrouor com plugins de economia sem precisar alterar seus fontes.

Este README explica:
- visão geral da arquitetura
- como instalar/compilar
- como criar menus por `config.yml` e `menus/*.yml`
- exemplos de placeholders e actions
- integração com plugins de economia (sem alterar o source deles)
- como funciona o CI (GitHub Actions) e como obter o JAR gerado
- troubleshooting rápido

Sumário
-------
- Arquitetura
- Instalação e build
- Configuração de menus (exemplos)
- Placeholders e actions suportadas
- Integração com plugins externos (economy)
- Adapter Hytale: como ligar ao servidor
- GitHub Actions e artefatos / releases
- Troubleshooting

Arquitetura (alto nível)
-----------------------
O projeto é dividido conceitualmente em camadas:

- `api` — interfaces públicas (ex.: `ChestCommandsAPI`, `CommandDispatcher`).
- `core` — lógica agnóstica de engine: parsing de menus, modelos, registries de ações/condições.
- `hytale` — adapter Hytale (integração com a API do servidor). Aqui vivem o `HytaleMenuRenderer`, o dispatcher reflexivo e o `ChestCommandsPlugin` que orquestra a inicialização.

Princípios importantes
- O core não conhece Hytale — o adapter faz a ponte.
- Integração com outros plugins é feita por duas estratégias:
  - `ServiceLoader` (interface `EconomyService`) — recomendado se o outro plugin puder expor um provider.
  - `CommandDispatcher` reflexivo — chama comandos do servidor (ex.: `/wallet`) sem alterar o outro plugin.

Instalação e build (local)
-------------------------
Requisitos:
- Java 17
- Maven 3.6+

Passos:
1. Certifique-se de ter uma cópia de `libs/HytaleServer.jar` dentro do diretório `libs/` (se necessário pelo build). O POM já referencia `libs/HytaleServer.jar` como dependência system-scope.
2. Na raiz do projeto execute:

```powershell
cd "D:\Programação\ChestCommands"
mvn -B -DskipTests package
```

3. O JAR será gerado em `target/`. Se usar o GitHub Actions, o workflow está configurado para instalar `libs/HytaleServer.jar` no runner, compilar e subir o JAR como artefato e também criar uma release pré-lançamento e anexar o JAR.

Configuração (menus via `config.yml`)
------------------------------------
O projeto suporta menus configurados em `config.yml` (o loader do `PluginMenuConfig` procura o arquivo na pasta de dados do plugin). Exemplo mínimo:

```yaml
ChestCommands:
  rows: 5
  tables: 10

menus:
  wallet:
    command: /wallet
    title: "Carteira de {player}"
    rows: 5
    items:
      "13":
        material: CHEST
        name: "§aSaldo: {balance}"
        lore:
          - "§7Banco: {bank}"
          - "§7Última: {last_transaction}"
        action: command:wallet info {player}
      "22":
        material: EMERALD
        name: "§eFazer transação"
        lore:
          - "Clique para iniciar"
        action: command:wallet send {player} <target> <amount>
```

Explicação:
- `menus` é um mapa de menus nomeados. Cada menu tem `command`, `title`, `rows` e `items`.
- `items` usa a chave do slot (ex.: "13") e define `material`, `name`, `lore` e `action`.
- `action` é uma string interpretada pelo `ActionExecutor`/`ConfigMenuAction`. Prefixos comuns: `command:`, `service:`, `wallet:send` (custom).

Placeholders suportados (exemplos)
- `{player}` — nome do jogador
- `{balance}` — saldo (se `EconomyService` presente)
- `{bank}` — saldo em banco (se `EconomyService` presente)
- `{last_transaction}` — última transação (se `EconomyService` presente)

Actions (comportamento padrão)
- command:<cmd> — o adapter executará o comando usando `CommandDispatcher` como jogador ou console; útil para chamar comandos expostos por outros plugins (ex.: `/wallet info {player}`).
- service:<Interface>#method — experimental: o `ActionExecutor` pode usar ServiceLoader/reflection para invocar serviços registrados.
- wallet:send — exemplo de ação custom que seu adapter pode mapear para abrir prompts e chamar a `EconomyService.transfer`.

Integração com plugins de economia (sem alterar o source deles)
--------------------------------------------------------------
Dois caminhos:

1) Sem tocar o plugin de economia (recomendado para seu caso):
   - Use `action: command:...` no menu para executar o comando público do plugin de economia (por exemplo `/wallet info <player>`).
   - O `CommandDispatcher` (implementado reflexivamente em `ReflectiveCommandDispatcher`) tentará executar o comando no servidor sem modificar o plugin de economia.
   - Exemplo: `action: command:/wallet info {player}` no item do menu.

2) Integração mais robusta (opcional, requer pequena alteração no plugin de economia):
   - Implementar a interface `org.konpekiestudios.chestcommands.hooks.EconomyService` no plugin de economia e registrar como provider do Java `ServiceLoader` (arquivo `META-INF/services/org.konpekiestudios.chestcommands.hooks.EconomyService` contendo a FQN da implementação).
   - O ChestCommands carregará automaticamente a implementação via ServiceLoader e preencherá placeholders `{balance}`, `{bank}`, `{last_transaction}`.

Adapters / Hytale
-----------------
O projeto inclui um adapter reflexivo para Hytale (`ReflectiveCommandDispatcher`, `PlayerReflector`) que: 
- executa comandos no servidor por reflexão (evitando necessidade de alterar outros plugins);
- envia mensagens ao jogador via métodos reflexivos quando a UI não puder ser construída (fallback textual).

Para integrar ao servidor Hytale real, no `onEnable` do seu módulo adapter faça algo como:

```java
// PSEUDO
CommandDispatcher dispatcher = new ReflectiveCommandDispatcher();
ChestCommandsPlugin cc = new ChestCommandsPlugin();
cc.onEnable(dataFolder, (cmdName, playerConsumer) -> {
    // registra o comando no servidor; quando executado, chame playerConsumer.accept(hytalePlayerObject)
    server.registerCommand(cmdName, hplayer -> playerConsumer.accept(hplayer));
}, dispatcher);
```

O adapter pode também implementar `HytaleMenuRenderer.open(EntityStore player, Menu menu)` usando as APIs do JAR do servidor para abrir uma janela gráfica real.

GitHub Actions (CI) — build e artefatos
--------------------------------------
O workflow `.github/workflows/build.yml` do repositório agora:
- instala `libs/HytaleServer.jar` no repositório Maven local do runner (se existir em `libs/`);
- executa `mvn -B -DskipTests package`;
- cria uma Release (pré-release) com a tag fornecida via `workflow_dispatch` (ou gera uma tag `pre-release-<run_id>`);
- localiza o JAR em `target/` e anexa o arquivo ao release com o nome `ChestCommands-<tag>.jar`;
- faz upload também do artefato para acesso via Actions UI.

Como disparar uma release com tag customizada:
- Vá em Actions → selecione workflow → Run workflow → no campo `release_tag` informe `v1.0.0-pre` ou o nome desejado.

Troubleshooting
---------------
- Erro de compilação por Hytale API: confirme `libs/HytaleServer.jar` está presente ou que o POM aponta corretamente. O workflow instala esse JAR no runner, mas localmente você precisa tê-lo em `libs/`.
- Release step 403: se o passo criar release falhar com 403, verifique as permissões do GITHUB_TOKEN (em repositórios organizacionais pode haver restrições). Alternativa: configure um PAT com `repo` scope e atualize o workflow para usar o secret.
- Asset não encontrado (ENOENT): o workflow agora busca o primeiro `target/*.jar` e usa esse caminho; se o JAR tiver nome inesperado, verifique output do `target/` no log de build.

Desenvolvendo novos menus e actions
----------------------------------
- Para ações customizadas (por exemplo, `wallet:send`), implemente um `ActionHandler` no adapter que saiba tratar `wallet:send` (abrir prompt, coletar destino e valor, e chamar `EconomyService.transfer(...)` via ServiceLoader ou enviar/dispatch de comando).
- Para condições (por exemplo `permission`), registre condicionalmente no startup com `ConditionRegistry.register("permission", ctx -> new HasPermissionCondition(ctx.getValue()));`.

Contribuindo
------------
- Abra um PR para features ou correções. O CI valida build e upload do JAR.
- Se você desenvolver um adapter específico (Hytale), mantenha o core engine-agnóstico e registre a lógica de UI no adapter.

Arquivo de referência rápida
---------------------------
- Config loader: `org.konpekiestudios.chestcommands.config.PluginMenuConfig`
- Adapter: `org.konpekiestudios.chestcommands.hytale` (ReflectiveCommandDispatcher, HytaleMenuRenderer)
- API: `org.konpekiestudios.chestcommands.hooks.EconomyService`, `org.konpekiestudios.chestcommands.api.CommandDispatcher`

Licença
-------
Escolha e adicione aqui a licença que preferir (MIT, Apache-2.0 etc.).

---

Se quiser, eu posso:
- gerar exemplos adicionais de `menus/*.yml` com mais tipos de ações (abrir submenus, dar item, executar comando remoto),
- implementar `HytaleMenuRenderer.open(...)` usando classes reais do JAR (se quiser que eu incorpore a UI real ao adapter), ou
- criar um pequeno guia em vídeo (passos textuais) para ligar o `ChestCommandsPlugin` ao seu servidor Hytale local e testar `/wallet` com o plugin de economia.

Diga qual desses extras você quer que eu faça a seguir.
