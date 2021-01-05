# Minesweeper-client
The minesweeper-client is a command line interface application to play Minesweeper.
It includes the `MinesweeperClient` Java library to interact with Minesweeper RESTful API.

## Command line arguments
```
$ java -jar minesweeper-client.jar
usage: minesweeper-client
 -f,--flag <cell>       Flags a <cell> = <game_id,row,col>
 -g,--get <id>          Gets game <id>
 -h,--host <arg>        Hostname of Minesweeper API server
 -l,--list              List user games
 -m,--mark <cell>       Marks a <cell> = <game_id,row,col>
 -n,--new <size>        Creates a new game of <size> = <row, cols, mines>
 -p,--port <arg>        Port of Minesweeper API server
 -pw,--password <arg>   Password
 -s,--swipe <cell>      Swipes a <cell> = <game_id,row,col>
 -u,--username <arg>    User name
```

## Some examples

### Creating a new game
Create a game for a user of size 7x7 with 5 mines.
```
$ java -jar minesweeper-client.jar -h localhost -p 4001 -u user -pw pass -n 7,7,5
Game: 6
Play time: 0 seconds
| # | # | # | # | # | # | # |
| # | # | # | # | # | # | # |
| # | # | # | # | # | # | # |
| # | # | # | # | # | # | # |
| # | # | # | # | # | # | # |
| # | # | # | # | # | # | # |
| # | # | # | # | # | # | # |
Status: undecided
Mines count: 5
```
### Listing user games
Let's get the games of the user:
```
java -jar minesweeper-client.jar -h localhost -p 4001 -u user -pw pass -l
[1, 4, 5, 6, 7, 8]
```
Here we can see that the user have 6 games, with ids 1, 4, 5, 6, 7, and 8.

### Swiping a cell
Let's swipe cell 9,9 of the game 6 above.
```
$ java -jar minesweeper-client.jar -h localhost -p 4001 -u user -pw pass -s 6,0,0
Game: 6
Play time: 7 seconds
|   |   |   |   |   |   |   |
| 1 | 1 |   | 1 | 1 | 1 |   |
| # | 2 | 1 | 1 | # | 1 |   |
| # | # | # | # | # | 1 |   |
| # | # | # | # | # | 1 |   |
| # | # | # | # | # | 1 |   |
| # | # | # | # | # | 1 |   |
Status: undecided
Mines count: 5
```
Here we can see that we have been playing for 7 seconds, and the swipe was propagated revealing a large portion of the board.
Cleared cells that has mines around show the count of them as a hint.

