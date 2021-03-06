/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2013 Jing Pu, Yang Zhao, You Yuan, Huijie Yu 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to 
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or 
 * sell copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 ******************************************************************************/
package quiz;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import user.Account;

/**
 * Servlet implementation class QuizDisplayServlet
 */
@WebServlet("/QuestionProcessServlet")
public class QuestionProcessServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public QuestionProcessServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		String userName = (String) session.getAttribute("guest");
		if (userName == null || userName.equals("guest")) {
			response.sendRedirect("index.html");
			return;
		}

		// get quizName questionIndex currentScore from session
		String quizName = (String) session.getAttribute("quizName");
		Integer questionIndex = (Integer) session.getAttribute("questionIndex");
		Integer currentScore = (Integer) session.getAttribute("currentScore");
		boolean comingFromLastQuestionPage = false;
		MyQuiz quiz;

		if (request.getParameter("quizName") != null) {
			// if there is no such attribute, we are just coming into this
			// QuizDisplay page and there should be quizName in HTTP request
			clearSessionAttr(session);
			quizName = request.getParameter("quizName");
			quiz = new MyQuiz(quizName);
			if (quiz.isOnePage()) {
				// redirect to quizDisplayOnePage.jsp and never return to this
				// page
				RequestDispatcher dispatch = request
						.getRequestDispatcher("quizDisplayOnePage.jsp");
				dispatch.forward(request, response);
				return;
			}
			if (quiz.isRandom())
				quiz.shuffleQuestionListAndSave();
			session.setAttribute("quizName", quizName);
			// set questionIndex to be zero (first question)
			questionIndex = 0;
			session.setAttribute("questionIndex", questionIndex);
			// set currentScore
			currentScore = 0;
			session.setAttribute("currentScore", currentScore);
			// set start time
			session.setAttribute("quizStartTime", new Date().getTime());
		} else {
			// otherwise we are coming from last question
			comingFromLastQuestionPage = true;
			quiz = new MyQuiz(quizName);
		}

		assert quiz != null;

		// then get the question list
		List<QuestionBase> questionList = quiz.getQuestionList();

		if (comingFromLastQuestionPage) {
			/*
			 * Do answer Checking here
			 */

			// Need to use factory to preprocess answer string
			QuestionBase lastQuestion = questionList.get(questionIndex - 1);
			String answer = lastQuestion.getUserAnswer(request);
			currentScore += lastQuestion.getScore(answer);
			session.setAttribute("currentScore", currentScore);
		}

		// write html
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		if (questionIndex != questionList.size()) {
			/*
			 * Display the question
			 */
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<meta charset=\"UTF-8\">");
			out.println("<title>Question " + (questionIndex + 1) + "</title>");

			// print reference to import CSS and JS
			out.println("<link href=\"CSS/page_style.css\" rel=\"stylesheet\" type=\"text/css\" />");
			out.println("<link href=\"friendpage_style.css\" rel=\"stylesheet\" type=\"text/css\" />");
			out.println("<link href=\"CSS/quiz_display.css\" rel=\"stylesheet\" type=\"text/css\" />");
			out.println(QuestionFactory.printReference());

			out.println("<script type=\"text/javascript\">");
			out.println("$(document).ready(function() {");
			out.println("$('#questionRead').submit(function() {");
			if (quiz.isImmCorrection()) {
				out.println("$.ajax({");
				out.println("type: \"GET\",");
				out.println("url: \"GetScoreServlet\",");
				out.println("data: $(\"#questionRead\").serialize(), ");
				out.println("success: function(data)");
				out.println("{");
				out.println("alert(data);");
				out.println("// $('#questionRead').submit() won't work here cause it will call this function recursively and never return");
				out.println("document.forms[0].submit(); // hack here: assume forms[0] is #questionRead");
				out.println("}");
				out.println("});");
				out.println("return false;");
			} else {
				out.println("return true;");
			}
			out.println("});");
			out.println("});");
			out.println("</script>");
			out.println("</head>");
			out.println("<body>");

			out.println("<div id=\"wrapper\">");
			out.println("<div id=\"inner\">");
			out.println("<div id=\"header\">");
			out.println("<h1>" + quizName + "</h1>");
			out.println("<h3  style=\"font-size:12px\"> Question "
					+ (questionIndex + 1) + " | "
					+ quiz.getQuestionList().size() + "</h3>");
			out.println("<div id=\"nav\">");
			out.println("<h2><a href=\"quiz_summary.jsp?quizName=" + quizName
					+ "\">Quit</a> | <a href=\"Logout\">Log out</a></h2>");
			out.println("</div>"); // div_nav
			out.println("</div>"); // div_header
			out.println("<div id=\"body\">");
			out.println("<div class=\"inner\">");

			QuestionBase question = questionList.get(questionIndex);
			out.println(question.printReadHtml());

			out.println("</div>"); // div_inner
			out.println("</div>"); // div_body
			out.println("</div>"); // div_inner
			out.println("</div>"); // div_wrapper
			out.println("</body>");
			out.println("</html>");

			// set questionIndex to the next question
			questionIndex++;
			session.setAttribute("questionIndex", questionIndex);
		} else {
			/*
			 * already finished all questions, save the quizEvent
			 */
			long startTime = (Long) session.getAttribute("quizStartTime");
			long timeElapsed = new Date().getTime() - startTime;
			String quizId = quiz.saveQuizEvent(userName, timeElapsed,
					currentScore);

			/*
			 * save quiz event and achievement to user database
			 */
			boolean newAchieve1 = false;
			boolean newAchieve2 = false;
			Account user = new Account(userName);
			if (quizName != null) {
				user.addQuizTaken(quizName, quizId);
				if (user.countHistory("t") == 10) {
					newAchieve1 = true;
					user.addAchievement("a4", quizName);
				}

				if (!user.containsAchievement("a5") && currentScore >= quiz.getBestScore()) {
					newAchieve2 = true;
					user.addAchievement("a5", quizName);
				}
			}

			// print to result page
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<script type=\"text/javascript\" src=\"challenge-msg.js\"></script>");
			out.println("<meta charset=\"UTF-8\">");
			out.println("<title>Quiz Results</title>");
			out.println("<link href=\"CSS/style.css\" rel=\"stylesheet\" type=\"text/css\" >");

			out.println("</head>");
			if (newAchieve1 && newAchieve2) {
				out.println("<body onload=\"javascript:Auto_both()\"");
			} else if (newAchieve1 && !newAchieve2) {
				out.println("<body onload=\"javascript:Auto_1()\"");
			} else if (!newAchieve1 && newAchieve2) {
				out.println("<body onload=\"javascript:Auto_2()\"");
			} else {
				out.println("<body>");
			}

			out.println("<h1>Quiz Results</h1>");
			out.println("<p>Score: " + quiz.getScore(quizId) + "/"
					+ quiz.getMaxScore() + "</p>");

			out.println("<p>Time: " + quiz.getTimeElapsed(quizId) + "s </p>");

			// *** add challenge button
			out.println("<input name='' type='button' value='Challenge my friends!'onclick='AddElement()'>");
			out.println("<form action='ChallengeLetterSent' target='hidFrame' method='post' id='letter'>");
			out.println("<input type='hidden' name='quizName' value="
					+ quizName + ">");
			out.println("<div id='msg'></div>");
			out.println("</form>");
			out.println("<iframe name='hidFrame' style='display: none'></iframe>");

			out.println("<p><a href=\"" + quiz.getSummaryPage()
					+ "\">Go back to summary page.</p>");
			out.println("</body>");
			out.println("</html>");

			// remove all the session attributes defined in this servlet
			clearSessionAttr(session);
		}

	}

	private void clearSessionAttr(HttpSession session) {
		session.removeAttribute("quizName");
		session.removeAttribute("questionIndex");
		session.removeAttribute("currentScore");
		session.removeAttribute("quizStartTime");
	}
}
