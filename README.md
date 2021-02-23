# executor
Typeboot executor to perform migrations


### managing specifications
Checkout https://github.com/typeboot/typeboot-spec if you want to use specifications to generate
metadata and target mutation files.

### build & run
```
./run.sh $(pwd)/jdbc/.jdbc.yaml
./run.sh $(pwd)/cassandra/.cassandra.yaml
```

### Running it from IDE
```
com.typeboot.main(arrayOf(".db.yaml"))
```