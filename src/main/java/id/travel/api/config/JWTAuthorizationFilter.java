package id.travel.api.config;

import java.io.IOException;
import java.util.ArrayList;import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.warungikan.db.model.User;

import id.travel.api.service.IUserService;
import io.jsonwebtoken.Jwts;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
	
	private UserDetailsService userService;
	
    public JWTAuthorizationFilter(AuthenticationManager authManager, UserDetailsService userService) {
        super(authManager);
        this.userService = userService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(SecurityConstants.HEADER_STRING);
        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.HEADER_STRING);
        if (token != null) {
            // parse the token.
            String user = Jwts.parser()
                    .setSigningKey(SecurityConstants.SECRET)
                    .parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                    .getBody()
                    .getSubject();
            if (user != null) {
            	UserDetails springUserDetails = userService.loadUserByUsername(user);
            	if (springUserDetails.getAuthorities() == null) throw new InsufficientAuthenticationException("User has no roles assigned");
                return new UsernamePasswordAuthenticationToken(user, null, springUserDetails.getAuthorities());
            }
            return null;
        }
        return null;
    }
}