package com.coinxlab.logging;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class LoggerInterceptor extends HandlerInterceptorAdapter {

	private static Log log = LogFactory.getLog(LoggerInterceptor.class.getName());
	
	@Override
	public boolean preHandle(
	  HttpServletRequest request,
	  HttpServletResponse response, 
	  Object handler) throws Exception {
	     
	    log.info("Received request  : [" + request.getRequestURI() + "]" + "[" + request.getMethod()
	      + "]" + request.getRequestURI());
	     
	    return true;
	}
	
	@Override
	public void postHandle(
	  HttpServletRequest request, 
	  HttpServletResponse response,
	  Object handler, 
	  ModelAndView modelAndView) throws Exception {
	     
	    log.info("request processing done for [" + request.getRequestURI() + "]");
	}
	
	@Override
	public void afterCompletion(
	  HttpServletRequest request, HttpServletResponse response,Object handler, Exception ex) 
	  throws Exception {
	    if (ex != null){
	        ex.printStackTrace();
	    }
	    log.info("Request completed [" + request.getRequestURI() + "][exception: " + ex + "]");
	}
}
