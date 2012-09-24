package com.mohchi.example.web.framework;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.OperatorOverloader;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypeComparator;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.TypedValue;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class ExtendedRequestMappingHanderMapping extends RequestMappingHandlerMapping {

	private static final String PATTERN_ATTR_PREFIX = ExtendedRequestMappingHanderMapping.class + ".";
	
	private SecurityExpressionHandler<FilterInvocation> handler;

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void afterPropertiesSet() {
		// code borrowed from AbstractAuthorizeTag
		ApplicationContext appContext = WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext());
		Map<String, SecurityExpressionHandler> handlers = appContext
				.getBeansOfType(SecurityExpressionHandler.class);

		for (SecurityExpressionHandler h : handlers.values()) {
			if (FilterInvocation.class.equals(GenericTypeResolver
					.resolveTypeArgument(h.getClass(), SecurityExpressionHandler.class))) {
				handler = h;
				break;
			}
		}

		if (handler == null) {
			throw new IllegalStateException("No visible WebSecurityExpressionHandler instance "
					+ "could be found in the application context");
		}
		super.afterPropertiesSet();
	}

	/**
	 * Stores the matching pattern into the request so it can be later used by the
	 * {@code PathVariableLookupEvaluationContext} if necessary.
	 */
	@Override
	protected RequestMappingInfo getMatchingMapping(RequestMappingInfo info, HttpServletRequest request) {
		RequestMappingInfo matchingMapping = super.getMatchingMapping(info, request);
		if (matchingMapping != null) {
			Set<String> patterns = matchingMapping.getPatternsCondition().getPatterns();
			if (patterns.size() != 1) {
				throw new IllegalStateException("Expected 1 matching pattern for request: " + request.getServletPath());
			}
			String pattern = patterns.iterator().next();
			AccessExpressionRequestCondition cond = (AccessExpressionRequestCondition) info.getCustomCondition();
			request.setAttribute(PATTERN_ATTR_PREFIX + cond.getId(), pattern);
		}
		return matchingMapping;
	}

	@Override
	protected RequestCondition<?> getCustomMethodCondition(Method method) {
		AccessExpressionRequestCondition condition;
		RequestMappingSecurityExpressionHandler rmHandler = new RequestMappingSecurityExpressionHandler(handler);
		PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
		if (preAuthorize != null) {
			condition = new AccessExpressionRequestCondition(preAuthorize.value(), rmHandler, true);
		} else {
			condition = new AccessExpressionRequestCondition(null, rmHandler, true);
		}
		rmHandler.setConditionId(condition.getId());
		return condition;
	}

	@Override
	protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
		AccessExpressionRequestCondition condition;
		RequestMappingSecurityExpressionHandler rmHandler = new RequestMappingSecurityExpressionHandler(handler);
		PreAuthorize preAuthorize = handlerType.getAnnotation(PreAuthorize.class);
		if (preAuthorize != null) {
			condition = new AccessExpressionRequestCondition(preAuthorize.value(), rmHandler, false);
		} else {
			condition = new AccessExpressionRequestCondition(null, rmHandler, false);
		}
		rmHandler.setConditionId(condition.getId());
		return condition;
	}

	@Override
	protected void handleMatch(RequestMappingInfo info, String lookupPath, HttpServletRequest request) {
		super.handleMatch(info, lookupPath, request);
		AccessExpressionRequestCondition c = (AccessExpressionRequestCondition) info.getCustomCondition();
		if (!c.isAuthorized(request)) {
			throw new AccessDeniedException("Authentication does not " +
					"match access expression '" + c.getExpression().getExpressionString() + "'");
		}
	}

	private final class RequestMappingSecurityExpressionHandler implements SecurityExpressionHandler<FilterInvocation> {

		private final SecurityExpressionHandler<FilterInvocation> delegate;
		private String conditionId;

		private RequestMappingSecurityExpressionHandler(SecurityExpressionHandler<FilterInvocation> delegate) {
			this.delegate = delegate;
		}

		private void setConditionId(String conditionId) {
			this.conditionId = conditionId;
		}

		@Override
		public ExpressionParser getExpressionParser() {
			return delegate.getExpressionParser();
		}

		@Override
		public EvaluationContext createEvaluationContext(Authentication authentication, FilterInvocation invocation) {
			EvaluationContext context = delegate.createEvaluationContext(authentication, invocation);
			return new PathVariableLookupEvaluationContext(conditionId, invocation.getRequest(), context);
		}

	}

	private final class PathVariableLookupEvaluationContext implements EvaluationContext {

		private final String conditionId;
		private final HttpServletRequest request;
		private final EvaluationContext delegate;
		
		private PathVariableLookupEvaluationContext(String conditionId,
				HttpServletRequest request, EvaluationContext delegate) {
			this.conditionId = conditionId;
			this.request = request;
			this.delegate = delegate;
		}

		@Override
		public TypedValue getRootObject() {
			return delegate.getRootObject();
		}

		@Override
		public List<ConstructorResolver> getConstructorResolvers() {
			return delegate.getConstructorResolvers();
		}

		@Override
		public List<MethodResolver> getMethodResolvers() {
			return delegate.getMethodResolvers();
		}

		@Override
		public List<PropertyAccessor> getPropertyAccessors() {
			return delegate.getPropertyAccessors();
		}

		@Override
		public TypeLocator getTypeLocator() {
			return delegate.getTypeLocator();
		}

		@Override
		public TypeConverter getTypeConverter() {
			return delegate.getTypeConverter();
		}

		@Override
		public TypeComparator getTypeComparator() {
			return delegate.getTypeComparator();
		}

		@Override
		public OperatorOverloader getOperatorOverloader() {
			return delegate.getOperatorOverloader();
		}

		@Override
		public BeanResolver getBeanResolver() {
			return delegate.getBeanResolver();
		}

		@Override
		public void setVariable(String name, Object value) {
			delegate.setVariable(name, value);
		}

		@Override
		public Object lookupVariable(String name) {
			Object result = delegate.lookupVariable(name);
			if (result == null) {
				String pattern = (String) request.getAttribute(PATTERN_ATTR_PREFIX + conditionId);
				if (pattern != null) {
					Map<String, String> pathVariables = getPathMatcher().extractUriTemplateVariables(pattern, request.getServletPath());
					result = pathVariables.get(name);
				}
			}
			return result;
		}

	}

}
