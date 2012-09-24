package com.mohchi.example.web.framework;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.FilterInvocation;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class ExtendedRequestMappingHanderMapping extends RequestMappingHandlerMapping {
	
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
            if (FilterInvocation.class.equals(GenericTypeResolver.resolveTypeArgument(h.getClass(),
                    SecurityExpressionHandler.class))) {
                handler = h;
                break;
            }
        }

        if (handler == null) {
	        throw new IllegalStateException("No visible WebSecurityExpressionHandler instance " +
	        		"could be found in the application context");
        }
        super.afterPropertiesSet();
	}

	@Override
	protected RequestCondition<?> getCustomMethodCondition(Method method) {
		PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
		if (preAuthorize != null) {
			return new AccessExpressionRequestCondition(preAuthorize.value(), handler, true);
		}
		return new AccessExpressionRequestCondition(null, handler, true);
	}

	@Override
	protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
		PreAuthorize preAuthorize = handlerType.getAnnotation(PreAuthorize.class);
		if (preAuthorize != null) {
			return new AccessExpressionRequestCondition(preAuthorize.value(), handler, false);
		}
		return new AccessExpressionRequestCondition(null, handler, false);
	}

	@Override
	protected void handleMatch(RequestMappingInfo info, String lookupPath, HttpServletRequest request) {
		super.handleMatch(info, lookupPath, request);
		AccessExpressionRequestCondition c = (AccessExpressionRequestCondition) info.getCustomCondition();
		if (!c.isAuthorized(request)) {
			throw new AccessDeniedException("Authentication does not " +
					"match access expression '" + c.getExpression() + "'");
		}
	}

}
