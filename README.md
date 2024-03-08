# Terminal-Based-CardGame-Simulator

## Overview
This Java card game project is a pair project developed during the second year, first semester at university.

It is a multiplayer card game developed in Java. The game involves N players and N decks of cards, with each player holding a hand of 4 cards. Players follow specific strategies for drawing and discarding cards to win the game. The game can be won immediately if a player is dealt four cards of the same value or after playing by collecting four cards of the same value from their deck.

## Project Structure

    Card.java: Represents a single card with a denomination.
    CardDeck.java: Represents a deck of cards with methods for drawing and discarding cards.
    Player.java: Represents a player in the game with methods for drawing, discarding, and winning the game.
    GameSetup.java: Sets up the game by creating players, decks, and distributing cards.
    CardGame.java: Main class with the main method and user input handling.

## How to Play

    1. Run the CardGame.java file to start the game.
    2. Enter the number of players and the location of the pack file containing card values.
    3. Follow the on-screen instructions to play the game.
    4. The game ends when a player wins or when multiple winners are detected.
    ***The user can create new text file for the card deck or use the given files from res folder. If you want to use given files, please see the details below

## Requirements

    Java 8 or higher

## Installation

    Clone the repository to your local machine.
    Compile the Java files using javac *.java.
    Run the game using java CardGame.

## Given Pack File (res) Details

    A number: Only one winner is possible.
        two.txt: Two players, only one winner.
        three.txt: Three players, only one winner.
        four.txt: Four players, only one winner.
    mw: Multiple winners possible.
        three_mw.txt: Three players, 2 winners possible.
        three_mw.txt: Three players, 3 winners possible.
        four_mw.txt: Four players, 4 winners possible.
    iw: Immediate winner.
        two_iw.txt: Two players, immediate winner.
        three_iw.txt: Three players, immediate winner.
        four_iw.txt: Four players, immediate winner.
        five_iw.txt: Five players, immediate winner.
        three_miw: Three players, multiple immediate winners.
    nw: No winner possible.
        two_nw.txt: Two players, no winner possible.
        three_nw.txt: Three players, no winner possible.
        four_nw.txt: Four players, no winner possible.
        five_nw.txt: Five players, no winner possible.
