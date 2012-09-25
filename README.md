spring-security-request-mapping
===============================

Spring MVC Integration with Spring Security's @PreAuthorize Annotation
----------------------------------------------------------------------

Read the introductory post on our blog: http://ow.ly/dXcNC

This is a sample web project with a few extended Spring MVC classes that integrate
Spring Security's @PreAuthorize annotation into Spring MVC's request routing mechanism:

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
Using this project, Spring MVC will route a request for "/" to authenticatedHomePage() if
the user is authenticated. Otherwise it will go to homePage().

Like other security expressions, you can reference `hasPermission()`, `authentication`, and
`principal` in an expression. You can also reference the `request` and any path variables
defined in the @RequestMapping:

    @RequestMapping("/secure/{name}")
    @PreAuthorized("authentication.name == #name")
    public String securePage() {
    	return "securePage";
    }
