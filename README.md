# Moive Theater Seating Challenge

## Contributors
- Jinbean Park: https://github.com/JinbeanPark

## Project Description

The program is for assigning seats within a moive theater with maximizing both customer satisfaction and customer safety. 

### Satisfaction
In order to maximize the customer satisfaction, 

1. Grouping 
    
    The program prioritizes assigning seats for group rather than assigning seats individually. 
    
2. Viewport in middle

    The program starts assigning seats from the middle of both row and column in order to provide customers the best angle of view. 
    
3. Unsatisfiable case 

    However, if there are no available seats for group, 
the program assings people in group separately one by one.

### Safety

1. Odd/even row scan (from middle for viewport satisfaction)

    For the customer safety, the program assigns seats by jumping the adjacent rows. For example, if there are rows A -> B -> C -> D -> E -> F -> G -> H -> I -> J, the program tracks rows and assigns seats by the following order: E(row=4) -> G(6) -> C(2) -> I(8) -> A(0). 

2. Unsafe case 
    
    However, if there are no available seats with the public safety condition, the program assigns seats to the left rows by the following order: F(row=5) -> D(3) -> H(7) -> B(1) -> J(9).\

### Input file spec

For the input description, the program is able to receive the paths of input file multiple times from the command in console, and the input file consists of multiple reservation requests line by line with the format comprised of a reservation identifier, followed by a space, and then the number of seats requested (eg. R002 4).

### Output

For the ouput description, The program put the seating assignments for each request to the output file with the format comprised of the reservation number followed by a space, and then a comma-delimited list of the assigned seats (eg R001 I1,I2).

## Assumption

1. Priortize satisfaction over safety

    For the customer satisfaction, assigning consecutive seats for group is more important than assigning separated seats in the middle of row and column.

2. Unsafe case handling
 
    If there are no available seats with public safety, customers should be assigned to seats in adjacent rows even though it could not keep public safety anymore.

3. Group split

    If there is no enough available seat to assign for every number of requested seats, the program assigns seats as much as available seats and stopped assigning seats for left people in the group if there is no more available seats.

## How to run the project with test file.
(1) Download the zipfile on the following link: https://github.com/JinbeanPark/Moive-Theater-Seating \
(2) Unzip the zipfile and compile the Solution.java by typing the command "javac Solution.java" in the terminal.\
(3) After compiling a java program, run the java program by typing the command "java assignSeats.Solution" in the src directory.\
(4) In order to type the path of test file in a console, type the path of input.txt "Path of input file/input.txt" in the console.\
(5) The default of directory for output file is set to "/Users/jinbean/Desktop/Programming Languages/MovieTheaterSeating/output.txt", so the user should replace the above output file directory with the directoy the user want to save the output file.