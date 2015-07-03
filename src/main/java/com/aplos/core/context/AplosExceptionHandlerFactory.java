package com.aplos.core.context;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class AplosExceptionHandlerFactory extends ExceptionHandlerFactory {

	  private ExceptionHandlerFactory parent;

	  // this injection handles jsf
	  public AplosExceptionHandlerFactory(ExceptionHandlerFactory parent) {
	    this.parent = parent;
	  }

	  //create your own ExceptionHandler
	  @Override
	  public ExceptionHandler getExceptionHandler() {
	    ExceptionHandler result =
	        new AplosExceptionHandler(parent.getExceptionHandler());
	    return result;
	  }
	}
