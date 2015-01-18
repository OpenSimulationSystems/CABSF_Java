package prisonersdilemma;

public abstract class Player {
	private Player currentCompetitor;
	private int totalScore;
	private int playerNumber;

	public int getPlayerNumber() {
		return playerNumber;
	}

	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}

	public void intializeGame(Player player) {
		this.currentCompetitor = player;
	}

	public DECISION decide() {
		if (playerNumber == 0)
			return DECISION.COOPERATE;
		else
			// Player 1
			return DECISION.DEFECT;
	}

	public void updateScore(int newScore) {
		totalScore += newScore;
		System.out.println("[Game Administrator] Player " + String.valueOf(playerNumber) + " got new score: "
				+ String.valueOf(newScore) + " New Total Score: "
				+ String.valueOf(totalScore));
	}

	public int getTotalScore() {
		return totalScore;
	}
}
