package com.aplos.core.component.autocomplete;


import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.event.PhaseId;
import javax.faces.render.FacesRenderer;

import org.primefaces.component.column.Column;
import org.primefaces.event.AutoCompleteEvent;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

@FacesRenderer(componentFamily="com.aplos.core.component.AutoComplete",rendererType="com.aplos.core.component.autocomplete.AutoCompleteRenderer")
public class AutoCompleteRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        AutoComplete autoComplete = (AutoComplete) component;

        if(autoComplete.isDisabled() || autoComplete.isReadonly()) {
            return;
        }

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = autoComplete.getClientId(context);
        String valueParam = autoComplete.getVar() == null ? clientId + "_input" : clientId + "_hinput";
        String submittedValue = params.get(valueParam);

        if(submittedValue != null) {
            autoComplete.setSubmittedValue(submittedValue);
        }

        decodeBehaviors(context, autoComplete);

        //WrappedAutoComplete event
        String query = params.get(clientId + "_query");
        if(query != null) {
            AutoCompleteEvent autoCompleteEvent = new AutoCompleteEvent(autoComplete, query);
            autoCompleteEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            autoComplete.queueEvent(autoCompleteEvent);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        AutoComplete autoComplete = (AutoComplete) component;
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String query = params.get(autoComplete.getClientId(context) + "_query");

        if(query != null) {
            encodeResults(context, component, query);
        }
        else {
            encodeMarkup(context, autoComplete);
            encodeScript(context, autoComplete);
        }
    }

    public void encodeResults(FacesContext context, UIComponent component, String query) throws IOException {
        AutoComplete ac = (AutoComplete) component;
        List results = ac.getSuggestions();
        int maxResults = ac.getMaxResults();

        if(maxResults != Integer.MAX_VALUE && results.size() > maxResults) {
            results = results.subList(0, ac.getMaxResults());
        }

        encodeSuggestions(context, ac, results);
    }

    protected void encodeMarkup(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ac.getClientId(context);
        Object value = ac.getValue();
        String styleClass = ac.getStyleClass();
        styleClass = styleClass == null ? AutoComplete.STYLE_CLASS : AutoComplete.STYLE_CLASS + " " + styleClass;

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if(ac.getStyle() != null) {
            writer.writeAttribute("style", ac.getStyle(), null);
        }

        encodeInput(context, ac, clientId, value);

        if(ac.getVar() != null) {
            encodeHiddenInput(context, ac, clientId, value);
        }

        if(ac.isDropdown()) {
            encodeDropDown(context, ac);
        }

        encodePanel(context, ac);

        writer.endElement("span");
    }

    protected void encodeInput(FacesContext context, AutoComplete ac, String clientId, Object value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean disabled = ac.isDisabled();
        writer.startElement("input", null);
        writer.writeAttribute("id", clientId + "_input", null);
        writer.writeAttribute("name", clientId + "_input", null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);
        if(value != null) {
            if(ac.getVar() == null) {
                writer.writeAttribute("value", ComponentUtils.getValueToRender(context, ac), null);
            } else {
            	if( ac.isHideSelectedValue() ) {
	                writer.writeAttribute("value", "", null);
            	} else {
	                context.getExternalContext().getRequestMap().put(ac.getVar(), value);
	                writer.writeAttribute("value", ac.getItemLabel(), null);
            	}
            }
        }

        if(disabled) {
			writer.writeAttribute("disabled", "disabled", null);
		}
        if(ac.isReadonly()) {
			writer.writeAttribute("readonly", "readonly", null);
		}

        renderPassThruAttributes(context, ac, HTML.INPUT_TEXT_ATTRS);

        writer.endElement("input");
    }

    protected void encodeHiddenInput(FacesContext context, AutoComplete ac, String clientId, Object value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("input", null);
        writer.writeAttribute("id", clientId + "_hinput", null);
        writer.writeAttribute("name", clientId + "_hinput", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("autocomplete", "off", null);
        if (value != null) {
            writer.writeAttribute("value", ComponentUtils.getStringValueToRender(context, ac, ac.getItemValue()), null);
        }
        writer.endElement("input");

        context.getExternalContext().getRequestMap().remove(ac.getVar());	//clean
    }

    protected void encodeDropDown(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("button", ac);
        writer.writeAttribute("class", "ui-button ui-widget ui-state-default ui-corner-right ui-button-icon-only", null);
        writer.writeAttribute("type", "button", null);

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-button-icon-primary ui-icon ui-icon-triangle-1-s", null);
        writer.endElement("span");

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-button-text", null);
        writer.write("&nbsp;");
        writer.endElement("span");


        writer.endElement("button");
    }

    protected void encodePanel(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = ac.getPanelStyleClass();
        styleClass = styleClass == null ? AutoComplete.PANEL_CLASS : AutoComplete.PANEL_CLASS + " " + styleClass;

        writer.startElement("div", null);
        writer.writeAttribute("id", ac.getClientId(context) + "_panel", null);
        writer.writeAttribute("class", styleClass, null);

        if(ac.getPanelStyle() != null) {
            writer.writeAttribute("style", ac.getPanelStyle(), null);
        }

        writer.endElement("div");
    }

    protected void encodeSuggestions(FacesContext context, AutoComplete ac, List items) throws IOException {
        context.getResponseWriter();
        boolean customContent = ac.getColums().size() > 0;
        Converter converter = getConverter(context, ac);

        if(customContent) {
            encodeSuggestionsAsTable(context, ac, items, converter);
        } else {
            encodeSuggestionsAsList(context, ac, items, converter);
        }
    }

    protected void encodeSuggestionsAsTable(FacesContext context, AutoComplete ac, List items, Converter converter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = ac.getVar();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        boolean pojo = var != null;

        writer.startElement("table", ac);
        writer.writeAttribute("class", AutoComplete.TABLE_CLASS, null);
        writer.startElement("tbody", ac);

        for(Object item : items) {
            writer.startElement("tr", null);
            writer.writeAttribute("class", AutoComplete.ROW_CLASS, null);

            if(pojo) {
                requestMap.put(var, item);
                String value = converter == null ? (String) ac.getItemValue() : converter.getAsString(context, ac, ac.getItemValue());
                writer.writeAttribute("data-item-value", value, null);
                writer.writeAttribute("data-item-label", ac.getItemLabel(), null);
            }

            for(Column column : ac.getColums()) {
                if(column.isRendered()) {
                    writer.startElement("td", null);
                    if(column.getStyle() != null) {
						writer.writeAttribute("style", item, null);
					}
                    if(column.getStyleClass() != null) {
						writer.writeAttribute("class", column.getStyleClass(), null);
					}

                    column.encodeAll(context);

                    writer.endElement("td");
                }
            }

            writer.endElement("tr");
        }

        writer.startElement("tbody", ac);
        writer.endElement("table");
    }

    protected void encodeSuggestionsAsList(FacesContext context, AutoComplete ac, List items, Converter converter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = ac.getVar();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        boolean pojo = var != null;

        writer.startElement("ul", ac);
        writer.writeAttribute("class", AutoComplete.LIST_CLASS, null);

        for(Object item : items) {
            writer.startElement("li", null);
            writer.writeAttribute("class", AutoComplete.ITEM_CLASS, null);

            if(pojo) {
                requestMap.put(var, item);
                String value = converter == null ? (String) ac.getItemValue() : converter.getAsString(context, ac, ac.getItemValue());
                writer.writeAttribute("data-item-value", value, null);
                writer.writeAttribute("data-item-label", ac.getItemLabel(), null);

                writer.writeText(ac.getItemLabel(), null);
            }
            else {
                writer.writeAttribute("data-item-label", item, null);

                writer.writeText(item, null);
            }

            writer.endElement("li");
        }

        writer.endElement("ul");

        if(pojo) {
            requestMap.remove(var);
        }
    }

    protected void encodeScript(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ac.getClientId(context);

        startScript(writer, clientId);

        writer.write("$(function(){");

        writer.write("AplosComponents.cw('AutoComplete','" + ac.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");

        //Configuration
        if(ac.getMinQueryLength() != 1) {
			writer.write(",minLength:" + ac.getMinQueryLength());
		}
        if(ac.getQueryDelay() != 300) {
			writer.write(",delay:" + ac.getQueryDelay());
		}
        if(ac.isForceSelection()) {
			writer.write(",forceSelection:true");
		}
        if(!ac.isGlobal()) {
			writer.write(",global:false");
		}
        if(ac.getScrollHeight() != Integer.MAX_VALUE) {
			writer.write(",scrollHeight:" + ac.getScrollHeight());
		}

        //Client side callbacks
        if(ac.getOnstart() != null) {
			writer.write(",onstart:function(request) {" + ac.getOnstart() + ";}");
		}
        if(ac.getOncomplete() != null) {
			writer.write(",oncomplete:function(response) {" + ac.getOncomplete() + ";}");
		}

        //Effects
        String effect = ac.getEffect();
        if(effect != null) {
            writer.write(",effect:'" + effect + "'");
            writer.write(",effectDuration:" + ac.getEffectDuration());
        }

        //Behaviors
        encodeClientBehaviors(context, ac);

//        writer.write(",behaviors:{itemSelect:function(){alert('hello');}}");

        writer.write(",theme:false");

        writer.write("});});");

        endScript(writer);
    }



    /**
     * Non-obstrusive way to apply client behaviors.
     * Behaviors are rendered as options to the client side widget and applied by widget to necessary dom element
     */
    @Override
    protected void encodeClientBehaviors(FacesContext context, ClientBehaviorHolder component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        //ClientBehaviors
        Map<String,List<ClientBehavior>> behaviorEvents = component.getClientBehaviors();

        if(!behaviorEvents.isEmpty()) {
            String clientId = ((UIComponent) component).getClientId(context);
            List<ClientBehaviorContext.Parameter> params = Collections.emptyList();

            writer.write(",behaviors:{");

            for(Iterator<String> eventIterator = behaviorEvents.keySet().iterator(); eventIterator.hasNext();) {
                String event = eventIterator.next();
                String domEvent = event;

                if(event.equalsIgnoreCase("valueChange")) {
					domEvent = "change";
				} else if(event.equalsIgnoreCase("action")) {
					domEvent = "click";
				}

                writer.write(domEvent + ":");

                writer.write("function(event) {");
                for(Iterator<ClientBehavior> behaviorIter = behaviorEvents.get(event).iterator(); behaviorIter.hasNext();) {
                    ClientBehavior behavior = behaviorIter.next();
                    ClientBehaviorContext cbc = ClientBehaviorContext.createClientBehaviorContext(context, (UIComponent) component, event, clientId, params);
                    String script = behavior.getScript(cbc);    //could be null if disabled

                    script = script.replace( "PrimeFaces", "AplosComponents" );
                    if(script != null) {
                        writer.write(script);
                    }
                }
                writer.write("}");

                if(eventIterator.hasNext()) {
                    writer.write(",");
                }
            }

            writer.write("}");
        }
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
