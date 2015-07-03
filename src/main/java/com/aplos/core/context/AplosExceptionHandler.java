package com.aplos.core.context;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import com.aplos.common.utils.ApplicationUtil;

public class AplosExceptionHandler extends ExceptionHandlerWrapper {

	  private ExceptionHandler wrapped;

	  public AplosExceptionHandler(ExceptionHandler wrapped) {
	    this.wrapped = wrapped;
	  }

	  @Override
	  public ExceptionHandler getWrapped() {
	    return wrapped;
	  }

	  @Override
	  public void handle() throws FacesException {
	    //Iterate over all unhandeled exceptions
	    Iterator i = getUnhandledExceptionQueuedEvents().iterator();
	    while (i.hasNext()) {
	      ExceptionQueuedEvent event = (ExceptionQueuedEvent) i.next();
	      ExceptionQueuedEventContext context =
	        (ExceptionQueuedEventContext)event.getSource();

	      //obtain throwable object
	      Throwable t = context.getException();

	      //here you do what ever you want with exception
	      try{
	      //log error
	    	  FacesContext facesContext = context.getContext();
	    	  if( facesContext.getViewRoot() != null ) {
	    	  String viewId = facesContext.getViewRoot().getViewId();
				if (viewId.equals("/common/issueReported.xhtml") || viewId.equals("/common/pageNotFound.xhtml") ) {
					/*
					 * This is to protect from infinite redirects if the error page
					 * itself has an error; in this case, revert to the default
					 * error handling, which should provide extra context
					 * information to debug the issue
					 */
//					log.error("Redirected back to ourselves, there must be a problem with the error.xhtml page", ex);
					super.handle(); // normal, ugly error
																// page used to
																// diagnose the
																// issue
					return; // return early, to prevent infinite redirects back to
							// ourselves
				}
	    	  }

				// squirrel the exception away in the session so it's maintained
				// across the redirect boundary
	    	  	
	    	  	boolean errorHandled = false;
	    	  	Throwable currentCause = t;
	    	    while( !errorHandled && currentCause != null ) {
	    	    	if( currentCause.getMessage() != null && (currentCause.getMessage().contains( "code: 403" ) || currentCause.getMessage().contains( "code: 404" )) ) {
						ApplicationUtil.getAplosContextListener().handlePageNotFound(t);
						errorHandled = true;
					}
	    	    	currentCause = currentCause.getCause();
	    	  	}
	      
	      		if( !errorHandled ) {
					ApplicationUtil.getAplosContextListener().handleError(t);
				}
				facesContext.responseComplete();
	        //redirect to error view etc....
	      }finally{
	        //after exception is handeled, remove it from queue
	        i.remove();
	      }
	    }
	    //let the parent handle the rest
	    getWrapped().handle();
	  }
	}
