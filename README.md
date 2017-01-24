# api
[![Build Status](https://travis-ci.org/optly/api.svg?branch=master)](https://travis-ci.org/optly/api)

Optly web api

## Getting Started

### Migrations

Before you build and run the application,
make sure **postgres** is installed and running.

Start the postgres docker container:
```
docker run -e POSTGRES_PASSWORD=mysecretpassword -p 15432:5432 -d postgres
```

Migrate the database to latest schema version:
```
lein migrate
```

### Build

Once you have cloned the repo and have installed
leiningen.

Run:

```
  lein ring server
```

In order to launch locally.


### Testing

We use leiningen for testing.

Run:

```
  lein test
```


## License

Copyright © 2017 John McConnell

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
