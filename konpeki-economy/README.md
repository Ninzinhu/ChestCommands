# KonpekiEconomy

[![Build Status](https://github.com/Ninzinhu/KonpekiEconomy/actions/workflows/build.yml/badge.svg)](https://github.com/Ninzinhu/KonpekiEconomy/actions/workflows/build.yml)

## Doações

Se você gostou do plugin e quer apoiar o desenvolvimento, considere fazer uma doação!  
[![Doar via Email](https://img.shields.io/badge/Doar%20via%20Email-joaovevasconcelos%40gmail.com-blue?style=for-the-badge&logo=gmail)](mailto:joaovevasconcelos@gmail.com)

Um plugin de economia poderoso para servidores Hytale, fornecendo gerenciamento abrangente de saldo de jogadores, transações e ferramentas administrativas.

## Funcionalidades

- **Gerenciamento de Saldo de Jogadores**: Verificar, adicionar, remover e definir saldos de jogadores.
- **Sistema de Transações**: Pagamentos seguros entre jogadores.
- **Saldos Top**: Ver os jogadores mais ricos no servidor.
- **Comandos Admin**: Ferramentas poderosas para administradores gerenciarem economias.
- **Suporte a Banco de Dados**: Escolher entre SQLite (padrão) ou MySQL para armazenamento de dados.
- **Configuração**: Configurações altamente personalizáveis via YAML.
- **Multi-threaded**: Execução assíncrona de comandos para melhor performance.

## Instalação

### Pré-requisitos

- Servidor Hytale

### Baixando o Plugin

1. Vá para a página de [Releases](https://github.com/Ninzinhu/KonpekiEconomy/releases) deste repositório.
2. Baixe a versão mais recente do arquivo JAR (ex: `konpeki-economy-1.0.jar`).

### Instalando no Servidor Hytale

1. Pare seu servidor Hytale.
2. Copie o arquivo JAR baixado para a pasta `plugins` do seu servidor Hytale.
3. Inicie o servidor. O plugin será carregado automaticamente e gerará arquivos de configuração.

## Configuração

Após a primeira execução, um arquivo `config.yml` será criado na pasta de dados do plugin. Edite-o para personalizar:

- Configurações de banco de dados (SQLite ou MySQL)
- Símbolo da moeda e formatação
- Permissões de comandos
- Limites de saldo

Exemplo de configuração:
```yaml
database:
  type: sqlite  # ou mysql
  host: localhost
  port: 3306
  database: economy
  username: user
  password: pass

currency:
  symbol: "$"
  decimal_places: 2

commands:
  balance: true
  pay: true
  baltop: true
  eco: true
```

## Uso

### Comandos de Jogador

- `/balance` - Verificar seu saldo atual
- `/pay <jogador> <quantia>` - Enviar dinheiro para outro jogador
- `/baltop` - Ver saldos dos jogadores top

### Comandos Admin

- `/eco add <jogador> <quantia>` - Adicionar dinheiro ao saldo de um jogador
- `/eco remove <jogador> <quantia>` - Remover dinheiro do saldo de um jogador
- `/eco set <jogador> <quantia>` - Definir o saldo de um jogador
- `/eco reload` - Recarregar a configuração

## API

KonpekiEconomy fornece uma API simples para outros plugins:

```java
import org.konpekiestudios.konpekistudios.hytale.plugins.KonpekiEcoAPI.Service;

KonpekiEcoAPI api = Service.getInstance();
float saldo = api.getBalance(playerId);
api.addBalance(playerId, 100.0f);
```

## Contribuição

Contribuições são bem-vindas! Por favor:

1. Fork o repositório
2. Crie uma branch de feature
3. Commit suas mudanças
4. Push para a branch
5. Crie um Pull Request

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## Suporte

Se você encontrar problemas ou tiver perguntas:

- Abra uma issue no GitHub
- Verifique os fóruns da comunidade Hytale
- Junte-se ao nosso servidor Discord (link TBD)

## Tradução para Inglês

Para traduzir este README para o inglês, você pode usar ferramentas online como o Google Translate ou copiar o conteúdo e traduzir manualmente. Uma versão em inglês pode ser encontrada [aqui](https://translate.google.com/?sl=pt&tl=en&text=...&op=translate) (substitua o texto).

---

Feito com ❤️ para a comunidade Hytale
