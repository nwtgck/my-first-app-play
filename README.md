# My First WebSocket in Play

## Run the server

```bash
sbt run
```

## How to connect WebSocket

### simple echo WS

```bash
$ wscat -c localhost:9000/echo-ws
```

### one-group chat

```bash
$ wscat -c localhost:9000/dynamic-ws
```