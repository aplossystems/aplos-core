package com.aplos.core.utils;

import java.io.IOException;

import javax.faces.context.FacesContext;

public class WidgetBuilder {
    protected FacesContext context;
    protected boolean endFunction = false;
    protected String resourcePath = null;
        
    public WidgetBuilder(FacesContext context) {
    	this.context = context;
    }
    
    protected WidgetBuilder init(String widgetClass, String widgetVar, String id, String resourcePath, boolean endFunction) throws IOException {
    	this.resourcePath = resourcePath;
    	this.endFunction = endFunction;
    	
        context.getResponseWriter().write("AplosComponents.cw(\"");
        context.getResponseWriter().write(widgetClass);
        context.getResponseWriter().write("\",\"");
        context.getResponseWriter().write(widgetVar);
        context.getResponseWriter().write("\",{");
        context.getResponseWriter().write("id:\"");
        context.getResponseWriter().write(id);
        if (widgetVar == null) {
        	context.getResponseWriter().write("\"");
        } else {
	        context.getResponseWriter().write("\",widgetVar:\"");
	        context.getResponseWriter().write(widgetVar);
	        context.getResponseWriter().write("\"");
        }

        return this;
    }


    public WidgetBuilder initWithDomReady(String widgetClass, String widgetVar, String id) throws IOException {

    	this.renderScriptBlock(id);
    	context.getResponseWriter().write("$(function(){");
    	this.init(widgetClass, widgetVar, id, null, true);
        
        return this;
    }
    
    private void renderScriptBlock(String id) throws IOException {
        context.getResponseWriter().startElement("script", null);
        context.getResponseWriter().writeAttribute("id", id + "_s", null);
        context.getResponseWriter().writeAttribute("type", "text/javascript", null);
    }

    public WidgetBuilder attr(String name, String value) throws IOException {
        if (value != null) {
            context.getResponseWriter().write(",");
            context.getResponseWriter().write(name);
            context.getResponseWriter().write(":\"");
        	context.getResponseWriter().write(value);
            context.getResponseWriter().write("\"");
        }

        return this;
    }

    public WidgetBuilder attr(String name, Boolean value) throws IOException {
        if (value != null) {
            context.getResponseWriter().write(",");
            context.getResponseWriter().write(name);
            context.getResponseWriter().write(":");
            context.getResponseWriter().write(Boolean.toString(value));
        }
        
        return this;
    }
    
    public WidgetBuilder attr(String name, Number value) throws IOException {
        if (value != null) {
            context.getResponseWriter().write(",");
            context.getResponseWriter().write(name);
            context.getResponseWriter().write(":");
        	context.getResponseWriter().write(value.toString());
        }
        
        return this;
    }
        
    public WidgetBuilder attr(String name, String value, String defaultValue) throws IOException {
        if(value != null && !value.equals(defaultValue)) {
            context.getResponseWriter().write(",");
	        context.getResponseWriter().write(name);
	        context.getResponseWriter().write(":\"");
	        context.getResponseWriter().write(value);
	        context.getResponseWriter().write("\"");
        }
        
        return this;
    }
    
    public WidgetBuilder attr(String name, double value, double defaultValue) throws IOException {
        if(value != defaultValue) {
            context.getResponseWriter().write(",");
	        context.getResponseWriter().write(name);
	        context.getResponseWriter().write(":");
	        context.getResponseWriter().write(Double.toString(value));
        }
        
        return this;
    }
    
    public WidgetBuilder attr(String name, int value, int defaultValue) throws IOException {
        if(value != defaultValue) {
            context.getResponseWriter().write(",");
	        context.getResponseWriter().write(name);
	        context.getResponseWriter().write(":");
	        context.getResponseWriter().write(Integer.toString(value));
        }
        
        return this;
    }
        
    public WidgetBuilder attr(String name, boolean value, boolean defaultValue) throws IOException {
        if(value != defaultValue) {
            context.getResponseWriter().write(",");
	        context.getResponseWriter().write(name);
	        context.getResponseWriter().write(":");
	        context.getResponseWriter().write(Boolean.toString(value));
        }
        
        return this;
    }
    
    public WidgetBuilder callback(String name, String signature, String callback) throws IOException {
        if(callback != null) {
            context.getResponseWriter().write(",");
	        context.getResponseWriter().write(name);
	        context.getResponseWriter().write(":");
	        context.getResponseWriter().write(signature);
	        context.getResponseWriter().write("{");
	        context.getResponseWriter().write(callback);
	        context.getResponseWriter().write("}");
        }
        
        return this;
    }
    
    public WidgetBuilder callback(String name, String callback) throws IOException {
        if(callback != null) {
            context.getResponseWriter().write(",");
	        context.getResponseWriter().write(name);
	        context.getResponseWriter().write(":");
	        context.getResponseWriter().write(callback);
        }
        
        return this;
    }
    
    public void finish() throws IOException {
        context.getResponseWriter().write("}");
        
        if(this.resourcePath != null) {
            context.getResponseWriter().write(",\"");
	        context.getResponseWriter().write(this.resourcePath);
	        context.getResponseWriter().write("\"");
        } 
        
        context.getResponseWriter().write(");");
        
        if(endFunction) {
            context.getResponseWriter().write("});");
        }
        
        context.getResponseWriter().endElement("script");
    }
}
