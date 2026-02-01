# ChestCommands (Hytale)

Este repositório contém o plugin ChestCommands e um índice local da API do Hytale contida em `libs/HytaleServer.jar`.

Para pesquisar por classes/pacotes expostos pelo JAR local, use o script:

```powershell
python tools/find_hytale_class.py <query>
# exemplo:
python tools/find_hytale_class.py Player

# para saída JSON:
python tools/find_hytale_class.py Player --json
```

Os arquivos importantes:
- `libs/HytaleServer.jar` - JAR do servidor Hytale (conteúdo indexado)
- `hytale_entries.txt` - listagem extraída do JAR (linhas com caminhos)
- `docs/hytale-classes.json` - resumo/index rápido
- `docs/HytaleAPI-index.md` - índice legível de pacotes úteis
- `tools/find_hytale_class.py` - script de busca

Nota: o repositório contém stubs temporários para compilar em CI; remova-os quando você tiver a dependência oficial do Maven ou usar o JAR real em tempo de execução.
