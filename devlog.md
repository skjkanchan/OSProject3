# May 10 8:49pm
a. This is my first addition to the devlog for project 3. As of now, I have watched the project over video and I just created my github repository. Now I need to think about coding the project itself. Look at the project 3 document, I'm not really sure where to start just because its a command line program and there is no demo code. 

b. This project session I plan to do some research on creating a command line program. I plan to create the necessary files I need and begin my code. 

# May 10 9:03pm
a. I am continuing this project session. I decided to setup a main project3 file with a class and main function, and the command line arguments will be the commands. This way the program will run like the previous 2 projects. I also looked at the what the program is doing itself, which is using a B Tree for indexing. I understand this concept as I recently learned about this in Databases in addition to OS so logically I think I understand what it's doing.
b. This project session I plan to write the code that takes in commands and sends the program to the corresponding function. 

# May 10 9:44pm
a. I am still in the same project session, but I have made more progress. My code is now able to accept a command from the commandline and go to the correspodning function to carry out the command. As far as the project itself, I have no new thoughts about it.
b. This part of my project session, I plan to learn a little more about how the index file works and write the code for the create command. I'm also a little confused on how the blocks work so I will look into that more as well. 


# May 10 10:36pm
a. I am finishing up this project session. I created the create command if statement and started on the create function. I also coded the function to print the usage menu. Next session I plan on making good progress on most if not all functions. 

# May 11 12:28am
a. I am now starting a new project session. Since last time, I re-read the project 3 document and realized that an index file represents a B-Tree, so in order to finish my create function I need to create a class for a B-Tree. 
b. This project session I plan on finishing the create function, and with that the B-Tree class and any other classes needed for the create command to work.

# May 11 1:09am
a. I am still in the same project session. I just finished the Header class which will be needed for the B-Tree. I'm understanding a little better how the B-Tree/Index file works.
b. Now, I plan on finishing the B-Tree class.

# May 11 2:20am
a. I am now ending this project session. I was able to create a header class for the index file as well as a node class. Inside the node class, I wrote toByte and fromByte methods to convert integers into 8-bit ints with big endian byte orders, and convert them back and put them in a Node. Next project section I can hopefully start on the B-Tree class itself now that I have a lot of its parts coded. 

# May 11 10:53am
a. I am now starting a new project session. Going into this session, I now have a better understanding of how the file implemenattion works. I understand now the logic of creating a B-Tree when creating a file. This means I will need to finish the B-Tree implementation before I do the command functions because all B-Tree operations are necessary for those functions. Other than that I dont have any new thoughts.
b. This project session I plan to finish the B-Tree class and use that to finish the create function. 


# May 11 1:10pm
a. I am still in the same project session, but now I have made lots of progress on my B-Tree implementation. I created the B-Tree class and added some operations like insert and search. Now that my B-Tree is basically complete I can go on to work on the command functions.
b. For the next part of the project session, I plan to work on the command functions, specifically create, insert, and search. 

# May 11 1:21pm
Updating the devlog because the create file function is complete, so now index file/BTree can be created from the command line.

# May 11 1:31pm
Updating the devlog because the insert function is complete, so now a key and value can be inserted into an index file.

# May 11 1:49pm
Updating the devlog because the search function is complete, so now the file can be searched for a specific key and the corresponding value is printed correctly. 

# May 11 2:02pm
Updating the devlog because the load function is complete, so key/value pairs from a csv file can be inserted into an index file. 

# May 11 2:41pm
Updating the devlog because the print function is complete, so the contents of the index file can be printer. In order to do this, I also had to modify the B-Tree class by adding a few methods to get all the values in the tree.

# May 11 2:43pm
I am now ending this project session. During this session I was able to finish the functions for create, insert, search, load, and print. I also updated the B-Tree class with the necessary methods as needed by the functions like a get all records method for the print function. Next session I plan on finishing the project by doing the extract function and then watching over the project overview video again to make sure the smaller details r done and correct. 

# May 11 3:30pm 
a. I am now starting a new project session. I don't really have any new thoughts about the project, but I am almost done and hopefully I can finish during this session.
b. This session I plan to finish the extract function, then re-read the project doc and re-watch the project overview video to make sure my project works as its supposed to and that any output matches what is shown in the video. 

# May 11 3:51pm
Updating the devlog because the extract function is now complete, so the contents of an index file can be extracted and written to a csv file.

# May 11 5:43pm
I am basically done with my project, but I realized that for some reason only my devlog.md has been pushing to github, but not my Project3.java so I'm going to try to push that to github right now and see if its working.

# May 11 5:48pm
I tried pushing again and it still didn't work so I checked my github and on there it has my old version of the file which is called project3.java from the very beginning which I changed to Project3.java so it matches my class name. I then tried staging "project3.java" for commit and i'm going to try pushing that to see if my changes update. 

# May 11 5:52pm
I pushed and it worked with "project3.java" even though on vscode my file is called "Project3.java". So now all my changes are in github. Now I'm going to continue editing my code so that the outputs match the ones in the project overview video.

# May 11 6:12pm
I am now running into an issue with insert because it's saying a key already exists when I insert into a new file. I'm looking into this right now. 

# May 11 6:31pm
I fixed the issue with insert, it was because my line currVal != 0=null was wrong since currVal is never null so I changed the null to -1 and it works. I also ended up changing some output and i'm still working on some.

# May 11 6:39pm
I am now ending this project session. I think I am basically done with the project as all the output is normal, the hexdump seems to be correct, and all the functions/commands are working as they do in the project overview video. All I have left to do in the next project session is to complete the readme, clean up my files by deleting the unnecessary ones I generated while testing, and then turn in my project. 

# May 11 8:40pm
a. I am now starting this project session. Since last session, I dont have any new thoughts about the project itself since I am basically done with it. Everything seemed to be working correctly. 
b. This project session I plan on writing the read me and then submitting the project. 

# May 11 9:44pm
I am now ending this final project session. I finished my ReadMe and finished testing my program one more time and everything compiles and works as intended. I will now make a final commit and submit this project.


