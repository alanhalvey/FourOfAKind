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

		System.out.println("created gameofpoker object successfully");
	}

	public static final int PLAYER_POT_DEFAULT = 100;
	public static final int ROUND_NUMBER = 0;
	int ante = 1;

/*
	public static void main(String[] args) throws InterruptedException {
		DeckOfCards deck = new DeckOfCards();
		OutputTerminal console = new OutputTerminal();


		ArrayList<PokerPlayer> players = new ArrayList<PokerPlayer>(6);
		PokerPlayer humanPlayer = new HumanPokerPlayer(deck);
		players.add(humanPlayer);

		for(int i=0;i<5;i++){
			PokerPlayer computerPlayer = new AutomatedPokerPlayer(deck, twitter);
			players.add(computerPlayer);			
		}

		//HandOfPoker(players, ante, console)





	}
*/
	@Override
	public void run() {
		System.out.println("getting into run");
		
		try {
			while(!playerWin && !playerLose && continueGame){
				HandOfPoker hop = new HandOfPoker(players,ante,deck,twitter);
			}
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
