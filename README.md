# Index File Manager
This program is a command line index file manager allowing users to manage their file storage throught a B-Tree which makes for efficient storage and access including a self-balancing structure. On starting this program, you will be presented with a menu of several commands that allow you to perform various operations on an index file including create, insert, search, load, print, and extract. 

# Create
With the create command, you can create a new index file to store your key value pairs. When this command executes it creates a new B-Tree to manage tha records you want to store. This command must be done before any of the others, as an index file needs to be created before operations can be performed on it. If the index file you wish to create already exists, you will receive an errpr.  

# Insert
With the insert command, you can insert any value into any key of the index file. When this command is called it will create a new node in the B-Tree to store your key and value and place it within the B-Tree depending on the key. If the key you wish to insert a value into already exists in the B-Tree, you will receieve an error that the key already exists so you will have to pick a new one. 

# Search
With the search command, you can find which key any value is currently stored in. This command uses the B-Tree search operation to search through eaach subtree until your value is found and returns the corresponding key. If the value you are looking for does not exist in the index, it will tell you.

# Load
With the load command, you can load a series of key value pairs from a csv file into your index file. This command goes through your csv file and inserts each value into its corresponding key one at a time. If a value already exists at one of the keys you wish to insert, it will tell you that the key already exists and will skip to the next pair in the file. 

# Print
With the print command, you can view all records currently stored in the index file. This command uses a B-Tree function that gets all records from the B-Tree and stores them in a list which is then printed in order of the keys. 

# Extract
With the extract command you can put all the data from your index file into a csv file. This command goes through the index file/B-tree and uses a B-Tree function that puts all the records from the B-Tree into a list. It then writes each key value pair from the list into your csv file.

# The Index File - B-Tree Implementation
The index file uses a B-Tree structure to store all your key value pairs. B-Trees are self balancing and whenever a node gets too full of records it changes its structure so it can add the record while still being balanced, making access to each record efficient. 

# How to run/compile the program
To Compile: In order to compile the program perform the following commands in the terminal:
- javac Project3.java

To Run: 

Perform this command to view the command menu:
- java Project3

After this you will have a menu of commands you can perform based on what operation you want to do:
- java Project3 create INDEX_FILE: create an index file
- java Project3 insert INDEX_FILE KEY VALUE: insert a value at a key in an index file
- java Project3 search INDEX_FILE KEY: search for a value at a key in an index file
- java Project3 load INDEX_FILE CSV_FILE: load key value pairs from csv file into an index file
- java Project3 print INDEX_FILE: print the key value pairs from an index file
- java Project3 extract INDEX_FILE SAVE_FILE: save all key value pairs from index file into a save file




