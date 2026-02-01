HytaleServer API (extraída do JAR)
=================================

Observação inicial
------------------
Este documento foi gerado a partir do índice presente em `libs/HytaleServer.jar` e contém uma lista inicial de classes e pacotes relevantes, além de exemplos de como chamar métodos por reflexão quando a API não estiver disponível via compilação direta.

Importante: para obter assinaturas 100% corretas (construtores e métodos públicos), rode `javap` sobre as FQCNs listadas na seção "Como extrair assinaturas" abaixo — o JDK/`javap` precisa estar no PATH.

Resumo das classes / pacotes encontrados
---------------------------------------
(Nomes retirados de `libs/hytale_entries.txt` — converta `/` para `.` e remova `.class` para obter FQCN)

- com.hypixel.hytale.server.core.HytaleServer
- com.hypixel.hytale.server.core.HytaleServerConfig
- com.hypixel.hytale.server.core.plugin.JavaPlugin
- com.hypixel.hytale.server.core.plugin.JavaPluginInit
- com.hypixel.hytale.server.core.plugin.pending.PendingLoadJavaPlugin
- com.hypixel.hytale.server.core.command.system.CommandManager

UI / Inventory / Item classes
- com.hypixel.hytale.server.core.ui.* (pacote com builders, ItemGridSlot, Value, LocalizableString etc.)
- com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
- com.hypixel.hytale.server.core.ui.builder.UIEventBuilder
- com.hypixel.hytale.server.core.ui.ItemGridSlot

- com.hypixel.hytale.server.core.inventory.Inventory
- com.hypixel.hytale.server.core.inventory.ItemContext
- com.hypixel.hytale.server.core.inventory.ItemStack
- com.hypixel.hytale.server.core.inventory.container.ItemContainer
- com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer
- com.hypixel.hytale.server.core.inventory.container.ItemStackItemContainer
- com.hypixel.hytale.server.core.inventory.container.InternalContainerUtilItemStack

Unverse / Entity
- com.hypixel.hytale.server.core.universe.world.storage.EntityStore
- (vários sistemas: EntityStore$NetworkIdSystem, EntityStore$UUIDSystem)

Outras áreas relevantes
- com.hypixel.hytale.server.core/io handlers
- com.hypixel.hytale.server.core/asset
- com.hypixel.hytale.server.core/event/events/player (PlayerReadyEvent, PlayerChatEvent, etc.)

O que nos interessa para o ChestCommands (objetivos)
- Encontrar a classe que representa o jogador (player/entity) e o(s) método(s) que enviam mensagens ao jogador e abrem UI.
- Encontrar a classe de janela/container (ItemContainer, ItemContainerWindow, InventoryWindow ou similar) e o(s) construtor(es) públicos.
- Encontrar se existe um `CommandManager` público para registrar comandos programaticamente.

Heurística e chamadas reflexivas sugeridas
----------------------------------------
Se você não puder compilar contra a API do servidor, use reflexão (como o plugin já faz) — siga esses passos:

1) Descubra o objeto "player" em tempo de execução (normalmente a origem do evento de comando ou referência de jogador). Use `Object player` no código e detecte métodos por reflexão.

2) Tentar enviar mensagem ao jogador (várias assinaturas possíveis, tente em ordem):
- `sendMessage(String)`
- `sendChatMessage(String)`
- `sendSystemMessage(String)`
- `sendMessage(String, MessageType)`  (variações podem existir)

Exemplo reflexivo (pseudocódigo Java):

```
try {
    Method m = player.getClass().getMethod("sendChatMessage", String.class);
    m.invoke(player, "Olá jogador");
} catch (NoSuchMethodException e) {
    try { /* tentar sendMessage */ } catch(...) {}
}
```

3) Criar/instanciar uma janela/container
- Candidate FQCNs (tente por reflexão):
  - `com.hypixel.hytale.server.core.inventory.container.ItemContainer`
  - `com.hypixel.hytale.server.core.ui.Window`
  - `com.hypixel.hytale.server.core.ui.ItemContainerWindow`
  - `com.hypixel.hytale.server.core.inventory.Inventory`

- Construtores a tentar:
  - `ctor(String title, int rows)`
  - `ctor(String title)`
  - `default ctor()`

4) Abrir a janela para o jogador
- Candidate player methods:
  - `openUI(Window)`
  - `openWindow(Window)`
  - `openInventory(Inventory)`
  - `openContainer(ItemContainer)`
  - `showWindow(Window)`

Use reflexão para tentar essas assinaturas (tentar `foundWindowClass` e `Object.class` como parâmetro type).

5) Preencher slots
- Tente um método `setItem(int slot, ItemStack)` ou `set(int, ItemStack)` no objeto window/container.
- Criar um ItemStack por reflexão: candidate class `com.hypixel.hytale.server.core.inventory.ItemStack` com construtores `(String material, int amount)` ou `(Resource, int)`.

Comandos e registro de comandos
--------------------------------
- Há `com.hypixel.hytale.server.core.command.system.CommandManager` no JAR.
- Para registrar comandos, preferimos usar a API pública do servidor se disponível; se não, registre via reflexão:
  - localizar `CommandManager` (provavelmente acessível via `HytaleServer.getCommandManager()` ou `HytaleServer.getServer().getCommandManager()`)
  - procurar método `registerCommand(String name, CommandHandler handler)` ou `register(CommandDefinition)` — use javap para achar a assinatura exata.

Como extrair assinaturas (passo a passo)
---------------------------------------
Você já gerou `libs/hytale_entries.txt` com `jar tf` — bom. Para obter assinaturas públicas, execute `javap` (venha do JDK) sobre as FQCNs encontradas. Exemplos (substitua FQCNs pelos que apareceram no índice):

```powershell
# exemplo: converta o caminho com/../.class para FQCN: com.hypixel.hytale.server.core.inventory.ItemStack
javap -classpath ".\libs\HytaleServer.jar" -public com.hypixel.hytale.server.core.inventory.ItemStack > .\docs\ItemStack-signature.txt
Get-Content .\docs\ItemStack-signature.txt
```

Repita para `EntityStore`, `ItemContainer`, `HytaleServer`, `CommandManager`, etc.

Exemplo de uso reflexivo já implementado (resumo do plugin)
---------------------------------------------------------
- `HytaleMenuRenderer` tenta localizar classes de janela e métodos de abertura por reflexão. Ele:
  - tenta vários nomes de classes e construtores;
  - preenche itens se `setItem(int, Object)` está disponível;
  - tenta métodos de player `openUI/openWindow/openInventory` etc.;
  - envia mensagens por reflexão como fallback.

Próximos passos recomendados
---------------------------
1) Instale JDK (se `javap` não estiver no PATH) ou adicione `javap` ao PATH. Execute os `javap` para as classes-chave e cole as saídas aqui ou anexe os arquivos `.txt`.
2) Eu gerarei `docs/HytaleServer-API.md` com assinaturas extraídas e ajustarei o plugin para usar as APIs corretas por reflexão (ou compilação direta, se preferir adicionar o JAR como provided dependency no POM).
3) Testes: após compilar o plugin e colocá-lo em `mods/`, execute o comando `/testchestui` (será implementado) e cole logs do servidor.

Observações finais
-----------------
- O índice do JAR mostra que as classes que precisamos estão presentes (inventory, ui, plugin, command). Precisamos apenas das assinaturas exatas para chamadas robustas.
- Se preferir, eu posso atualizar o plugin para forçar os FQCNs já listados aqui e tentar compilar — se der erro de versão/assinatura, corrijo iterativamente.

---
Gerado em: $(Get-Date)

