## CS122B Team 3 Projects
Team member: Dayue Bai & Michael Wang

Project 5 > Task 3 > Usage of the parser:
* The parser we wrote is parser.py, In order to run it, go to the directory ```/Fabflix-website/target/``` and run the following command:

    ```python3 parser.py```
    
    in terminal to see the expected output for all 9 test cases(stored in 9 log files: log11-log15.txt and log21-log24.txt)

* The 9 log files under this directory follows the format: ```TS(space)TJ``` in each line. All of the files have 2642 lines in total. The function in the parser will read and split the files into lines first, and during iteration of each line, it splits the TS and TJ and add them to two lists. At the end of the iteration, the function will calculate the average TS and the average TJ and print those out for each file. 
* Then we fill out the html report by the data in the terminal output with their corresponding log files. 
