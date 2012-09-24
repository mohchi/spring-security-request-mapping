package com.mohchi.example.web.framework;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

public class AccessExpressionRequestCondition extends AbstractRequestCondition<AccessExpressionRequestCondition> {

	private static final Logger logger = LoggerFactory.getLogger(AccessExpressionRequestCondition.class);

	private final Expression expression;
	private final SecurityExpressionHandler<FilterInvocation> handler;
	private final boolean method;
	private final String id;

	public AccessExpressionRequestCondition(String accessExpression,
			SecurityExpressionHandler<FilterInvocation> handler, boolean method) {
		if (accessExpression == null) {
			expression = null;
			id = AccessExpressionRequestCondition.class.toString();
		} else {
			expression = handler.getExpressionParser().parseExpression(accessExpression);
			id = AccessExpressionRequestCondition.class + "." + accessExpression;
		}
		this.handler = handler;
		this.method = method;
	}

	public Expression getExpression() {
		return expression;
	}

	@Override
	protected Collection<?> getContent() {
		if (expression == null) {
			return Collections.EMPTY_SET;
		}
		return Collections.singleton(expression.getExpressionString());
	}

	@Override
	protected String getToStringInfix() {
		return "";
	}

	/**
	 * This method doesn't actually combine the conditions. Instead, method-level
	 * conditions always override type-level conditions. Also, having a condition
	 * overrides not having a condition. For cases where the conditions are
	 * equivalent in terms of priority, {@code this}'s condition is favored.
	 */
	@Override
	public AccessExpressionRequestCondition combine(AccessExpressionRequestCondition other) {
		if (expression != null && method) {
			return this;
		}
		if (other.expression != null && other.method) {
			return other;
		}
		if (expression != null) {
			return this;
		}
		if (other.expression != null) {
			return other;
		}
		return this;
	}

	@Override
	public AccessExpressionRequestCondition getMatchingCondition(HttpServletRequest request) {
		return this;
	}

	/**
	 * Evaluates the expression using the authentication within the request
	 * and returns the result. The result is cached in the request for
	 * future reference.
	 * @param request
	 * @return
	 */
	protected boolean isAuthorized(HttpServletRequest request) {
		Boolean result = (Boolean) request.getAttribute(id);
		if (result == null) {
			if (expression == null) {
				result = true;
			} else {
				FilterInvocation f = new FilterInvocation(request, DUMMY_RESPONSE, DUMMY_FILTER_CHAIN);
				EvaluationContext c = handler.createEvaluationContext(SecurityContextHolder.getContext().getAuthentication(), f);
				result = ExpressionUtils.evaluateAsBoolean(expression, c);
			}
			request.setAttribute(id, result);
			if (logger.isDebugEnabled()) {
				if (expression != null) {
					logger.debug("Evaluated access expression '" + expression.getExpressionString()
							+ "' for request '" + request.getServletPath() + "' to " + result);
				}
			}
		}
		return result;
	}

	/**
	 * <p>Returns a negative number when {@code this} has greater priority than
	 * {@code other}, a positive number when {@code other} has a greater priority,
	 * and {@code 0} when their priorities are equal. A greater priority is assigned
	 * to a condition when its expression is not {@code null} and its
	 * {@link #isAuthorized} method returns {@code true} for the given request while:
	 * <ul><li>the other's {@link #isAuthorized} method returns {@code false}</li>
	 * <li>the other's expression is {@code null}</li></ul></p>
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(AccessExpressionRequestCondition other, HttpServletRequest request) {
		boolean result = isAuthorized(request);
		boolean otherResult = other.isAuthorized(request);

		if (result && otherResult) {
			int score = other.expression != null ? 1 : 0;
			score -= expression != null ? 1 : 0;
			return score;
		} else if (result) {
			return -1;
		} else if (otherResult) {
			return 1;
		}
		return 0;
	}

	private static final FilterChain DUMMY_FILTER_CHAIN = new FilterChain() {

		@Override
		public void doFilter(ServletRequest arg0, ServletResponse arg1) throws IOException, ServletException {
			throw new UnsupportedOperationException();
		}

	};

	private static final HttpServletResponse DUMMY_RESPONSE = new HttpServletResponse() {

		@Override
		public void flushBuffer() throws IOException {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getBufferSize() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getCharacterEncoding() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getContentType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Locale getLocale() {
			throw new UnsupportedOperationException();
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			throw new UnsupportedOperationException();
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isCommitted() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void reset() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void resetBuffer() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setBufferSize(int arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setCharacterEncoding(String arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setContentLength(int arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setContentType(String arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setLocale(Locale arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addCookie(Cookie arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addDateHeader(String arg0, long arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addHeader(String arg0, String arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addIntHeader(String arg0, int arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsHeader(String arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String encodeRedirectURL(String arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String encodeRedirectUrl(String arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String encodeURL(String arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String encodeUrl(String arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getHeader(String arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<String> getHeaderNames() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<String> getHeaders(String arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getStatus() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void sendError(int arg0) throws IOException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void sendError(int arg0, String arg1) throws IOException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void sendRedirect(String arg0) throws IOException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setDateHeader(String arg0, long arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setHeader(String arg0, String arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setIntHeader(String arg0, int arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setStatus(int arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setStatus(int arg0, String arg1) {
			throw new UnsupportedOperationException();
		}

	};

}
