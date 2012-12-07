spring-security-request-mapping
===============================

Spring MVC Integration with Spring Security's @PreAuthorize Annotation
----------------------------------------------------------------------

Read the introductory post on our blog: http://ow.ly/dXcNC

This is a sample web project with a few extended Spring MVC classes demonstrating
how to integrate Spring Security's @PreAuthorize annotation into Spring MVC's
request routing mechanism:

    @RequestMapping("/")
    @PreAuthorize("isAuthenticated()")
    public String authenticatedHomePage() {
    	return "authenticatedHomePage";
    }
    
    @RequestMapping("/")
    public String homePage() {
    	return "homePage";
    }

Normally, the code above would not work in Spring MVC because there's a duplicate mapping.
Using this project, Spring MVC will route a request for "/" to `authenticatedHomePage()` if
the user is authenticated. Otherwise it will go to `homePage()`.

Within an expression, you can reference `hasPermission()`, `authentication`, `principal`,
and, depending on the `SecurityExpressionHandler` in use, `request`. You can also reference
any path variables defined in the `@RequestMapping` annotation:

    @RequestMapping("/secure/{name}")
    @PreAuthorized("authentication.name == #name")
    public String securePage() {
    	return "securePage";
    }

Finally, if a handler is matched for a request based on the `@RequestMapping` specification
but fails the security expression (and there are no other suitable handlers),
an `AccessDeniedException` is thrown for Spring Security's `ExceptionTranslationFilter`
to deal with as it sees fit.
