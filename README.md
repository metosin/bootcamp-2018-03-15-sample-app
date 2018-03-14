# bootcamp-2018-03-15

Sample app for bootcamp 2018-03-15

## Usage

Start PostgreSQL in docker:

```bash
cd devops
docker-compose up
```

Start frontend compilation:

```bash
lein dev
```

Start backend REPL in another shell:

```bash
lein repl
```

In repl:

```clj
(reset)
```

Application is at http://localhost:3000 and swagger at http://localhost:3000/swagger 

## License

Copyright © 2018 Metosin Ltd

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
