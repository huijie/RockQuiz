package user;

public interface User {
	
	public void addQuizTaken(String quizName, String quizId);
	public void addQuizCreated(String quizName);
	public void addAchievement(String name);
	
}