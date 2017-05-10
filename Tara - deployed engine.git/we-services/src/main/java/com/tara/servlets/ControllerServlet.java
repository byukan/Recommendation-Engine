package com.tara.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tara.services.TaraResource;

public class ControllerServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		System.out.println("get");

		System.out.println(request.getParameter("userId"));
		request.getRequestDispatcher("/feedback.jsp")
				.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		System.out.println(request.getParameter("userId"));

		if (new TaraResource()
				.rateCourse(request.getParameter("userId"),
						request.getParameter("courseId"),
						Float.parseFloat(request.getParameter("score")),
						request.getParameter("feedback")).toString()
				.contains("success")) {

			request.getRequestDispatcher("/success.jsp").forward(request,
					response);
		} else {
			request.getRequestDispatcher("/fail.jsp")
					.forward(request, response);
		}

	}

}
