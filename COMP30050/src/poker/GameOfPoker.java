package poker;

import java.io.IOException;
import java.util.ArrayList;

import twitter4j.TwitterException;

public class GameOfPoker implements Runnable{

	public String playerName = "";
	private DeckOfCards deck;
	public HumanPokerPlayer humanPlayer;
	private TwitterInteraction twitter;
	ArrayList<PokerPlayer> players = new ArrayList<PokerPlayer>(6);
	boolean playerWin = false;
	boolean playerLose = false;
	boolean continueGame = true;


	public GameOfPoker(String username, TwitterInteraction t, DeckOfCards d) throws InterruptedException{
		playerName = username;
		deck = d;
		humanPlayer = new HumanPokerPlayer(deck, t);
		twitter = t;
		players.add(humanPlayer);
		for(int i=0;i<5;i++){
			PokerPlayer computerPlayer = new AutomatedPokerPlayer(deck, twitter);
			players.add(computerPlayer);	
		}
	}

	public static final int PLAYER_POT_DEFAULT = 20;
	public static final int ROUND_NUMBER = 0;
	int ante = 1;

	//Runnable code segment
	@Override
	public void run() {
		try {
			while(!playerWin && !playerLose && continueGame && !(Thread.currentThread().isInterrupted())){
				HandOfPoker handOfPoker = new HandOfPoker(players,ante,deck,twitter);
				
				
				ArrayList<PokerPlayer> nextRoundPlayers = new ArrayList<PokerPlayer>();
				
				for(int i=0;i<players.size();i++){
					if(!(players.get(i).playerPot<=0)){
						nextRoundPlayers.add(players.get(i));
					}
					else{
						twitter.appendToCompoundTweet("---Player "+players.get(i).playerName+" is out of chips, and out of the game.---");
						//If the human is out of chips, leave the game.
						if(players.get(i).isHuman()){
							playerLose = true;
							twitter.appendToCompoundTweet("Sorry, you are out of the game. Goodbye and thanks for playing!");
							twitter.appendToCompoundTweet("To play again, Tweet with #FOAKDeal");
							twitter.postCompoundTweet();
							break;
						}
					}
				}
				
				players = nextRoundPlayers;
				
				if(players.size()==1 && !playerLose){
					//If the human player has won
					if(players.get(0).isHuman()){
						twitter.appendToCompoundTweet("You have beaten the bots and won the game! Congratulations!");
						twitter.appendToCompoundTweet("To play another game, Tweet with #FOAKDeal !");
						twitter.postCompoundTweet();
						playerWin = true;
					}
					//If the human player has lost.
					else{
						twitter.appendToCompoundTweet("Hard luck, you have lost the game.");
						twitter.appendToCompoundTweet("You can play again by Tweeting #FOAKDeal");
						twitter.postCompoundTweet();
						playerLose = true;
					}
				}
				
				if(TwitterStreamer.gamesOfPoker.containsKey(playerName)){
					if(TwitterStreamer.userHasQuit(playerName) == true){
						break;
					}
				}
			}
			if(playerWin || playerLose){
				TwitterStreamer.usersPlayingGames.remove(playerName);
				TwitterStreamer.gamesOfPoker.remove(playerName);
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
