# External sorting

## Generate dummy txt
To generate dummy text file use `generate_file.py` script with number of desired lines.
For example to generate file `output.txt` with 5000 lines of random words execute the following.
```
 ./generate_file.py -o output.txt 5000
```

## Sort file
First you need to build application.
```
gradle build
```
This command will generate executable jar file in `src/build/libs`.
After that you can run application with the following command:
```
java -jar build/libs/sorter.jar input.txt output.tx 1024
```
You have to manually define desirable block size assuming available memory for application.
