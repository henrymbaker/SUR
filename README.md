**CSCI 330 Winter 2023**

Preston Duffield, Harry Zheng, Henry Baker.

---

## Description

SUR, a single user relational database managment system. Offer relational database facilities to a host language (Java) on a personal computer. This project was done on a different GitHub account that I used for school.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

:warning: **Make sure not to commit `.class` or any other similar files.** Add files to the gitignore if they are test/non-source.

## Setting Up a Local Environment

1. Clone the repository locally, into a `local/directory`. Note this can be any directory.
2. In a command prompt/terminal navigate to your `local/directory`.
3. Run `javac -cp src -d bin src/*.java`
   This will compile the code and create `.class` files in the `local/directory/bin` from all the `.java` files in `local/directory/src`.
4. Run `java -cp bin Main input.txt`
   This will run the program with an input file `input.txt` as an argument.
