package com.aplos.core.component.autocomplete;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.ErrorEmailSender;
import com.aplos.common.utils.JSFUtil;

@FacesConverter(value="autoCompleteBeanConverter")
public class AutoCompleteBeanConverter implements Converter {

		@Override
		public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String obj) {
			AutoComplete autoComplete = (AutoComplete) uiComponent;

			Map<String, Object> viewMap = facesContext.getViewRoot().getViewMap();
			@SuppressWarnings("unchecked")
			List<? extends AplosAbstractBean> aplosAbstractBeanList = (List<? extends AplosAbstractBean>) viewMap.get( autoComplete.getClientId() + "_suggestions" );
			viewMap.put( autoComplete.getClientId() + "_suggestions", null );
			Map<String,AjaxBehaviorEvent> customEvents = autoComplete.getCustomEvents();
			boolean itemSelected = false;
			/*
			 * Only process if the object has been selected, otherwise the obj property won't be an index 
			 * but the string of the autocomplete.
			 */
            for(Iterator<String> customEventIter = customEvents.keySet().iterator(); customEventIter.hasNext();) {
            	String customEventKey = customEventIter.next();
            	if( customEventKey == "itemSelect" ) {
            		itemSelected = true;
            	}
            }

            if( itemSelected ) {
				if( aplosAbstractBeanList != null ) {
					try {
						if( obj == null ) {
							return null;
						} else {
							return aplosAbstractBeanList.get( Integer.parseInt( obj ) );
						}
	
					} catch ( Exception ex ) {
						//  There's an error been sent back saying that the obj is out of bounds of the
						//  array, this should give more information if this happens again 8 Nov 2010
						StringBuffer strBuf = new StringBuffer();
						if (obj != null) {
							strBuf.append( obj.toString() + " " );
						}
						for ( int i = 0, n = aplosAbstractBeanList.size(); i < n; i++ ) {
							strBuf.append( aplosAbstractBeanList.get( i ).getId() + " " );
						}
						ErrorEmailSender.sendErrorEmail( JSFUtil.getRequest(), ApplicationUtil.getAplosContextListener(), ex, strBuf.toString() );
						if ( aplosAbstractBeanList.size() > 0 ) {
							return aplosAbstractBeanList.get( 0 );
						}
					}
				}
				return null;
            } else {
            	return null;
            }
		}

		public List<? extends AplosAbstractBean> getSuggestions( FacesContext facesContext, AutoComplete autoComplete ) {
			if ( autoComplete != null ) {
				HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
				@SuppressWarnings("unchecked")
				List<? extends AplosAbstractBean> aplosAbstractBeanList = (List<? extends AplosAbstractBean>) request.getAttribute( autoComplete.getClientId() + "_suggestions" );

				if ( aplosAbstractBeanList == null ) {
					aplosAbstractBeanList = autoComplete.getSuggestions();
					Map<String, Object> viewMap = facesContext.getViewRoot().getViewMap();
					viewMap.put( autoComplete.getClientId() + "_suggestions", aplosAbstractBeanList );
					request.setAttribute( autoComplete.getClientId() + "_suggestions", aplosAbstractBeanList );
				}

				return aplosAbstractBeanList;
			} else {
				return null;
			}
		}

		@Override
		public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object obj) {
			List<? extends AplosAbstractBean> aplosAbstractBeanList = getSuggestions( facesContext, (AutoComplete) uiComponent );
			if( aplosAbstractBeanList != null && obj != null ) {
					for( int i = 0, n = aplosAbstractBeanList.size(); i < n; i++ ) {
						if( obj.equals( aplosAbstractBeanList.get( i ) ) ) {
							return String.valueOf( i );
						}
					}
			}

			return null;
		}
	}



