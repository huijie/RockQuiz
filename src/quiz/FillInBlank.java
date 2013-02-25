/**
 * 
 */
package quiz;

import java.sql.Connection;
import java.sql.SQLException;

import database.MyDB;

/**
 * @author yang
 *
 */
public class FillInBlank extends QuestionBase {


	public FillInBlank(String questionId) {
		// TODO Auto-generated constructor stub
		questionType = "Fill_In_Blank";
		queryStmt = "SELECT * FROM Fill_In_Blank_Pool WHERE question_id = " + questionId;
		Connection con = MyDB.getConnection();
		try {
			stmt = con.createStatement();
			stmt.executeQuery("USE c_cs108_yzhao3");
			rs = stmt.executeQuery(queryStmt);
			while(rs.next()) {
				questionId = rs.getString(1);
				creatorId = rs.getString(2);
				typeIntro = rs.getString(3);
				questionDescription = rs.getString(4);
				answer = rs.getString(5);
				maxScore = rs.getString(6);
				tagString = rs.getString(7);
				correctRatio = rs.getString(8);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see quiz.QuestionBase#printHTML()
	 */
	@Override
	public String printHTML() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveToDatabase() {
		// TODO Auto-generated method stub
		queryStmt = "INSERT INTO " + FIB_Table + "VALUES (\"" +
					questionId + "\", \"" +
					creatorId + "\", \"" +
					typeIntro + "\", \"" +
					questionDescription  + "\", \"" +
					answer + "\", \"" +
					maxScore + "\", \"" +
					tagString + "\", \"" +
					correctRatio + "\")";
		
		try {
		rs = stmt.executeQuery(queryStmt);
		} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see quiz.QuestionBase#getScore(java.lang.String)
	 */
	@Override
	public String getScore(String userInput) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
