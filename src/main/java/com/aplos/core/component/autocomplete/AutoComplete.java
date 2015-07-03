package com.aplos.core.component.autocomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

import org.primefaces.component.column.Column;
import org.primefaces.event.SelectEvent;
import org.primefaces.util.Constants;

import com.aplos.common.beans.AplosAbstractBean;

@FacesComponent("com.aplos.core.component.autocomplete.AutoComplete")
@ResourceDependencies({
	@ResourceDependency(library="components", name="components.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="components", name="components.js")
})
public class AutoComplete extends HtmlInputText implements org.primefaces.component.api.Widget {
	public static final String COMPONENT_TYPE = "com.aplos.core.component.autocomplete.AutoComplete";
	public static final String COMPONENT_FAMILY = "com.aplos.core.component.AutoComplete";
	private static final String DEFAULT_RENDERER = "com.aplos.core.component.autocomplete.AutoCompleteRenderer";
	private static final String OPTIMIZED_PACKAGE = "com.aplos.core.component.";

	protected enum PropertyKeys {

		widgetVar
		,completeMethod
		,var
		,itemLabel
		,itemValue
		,maxResults
		,minQueryLength
		,queryDelay
		,forceSelection
		,hideSelectedValue
		,onstart
		,oncomplete
		,global
		,scrollHeight
		,effect
		,effectDuration
		,dropdown
		,panelStyle
		,panelStyleClass;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		@Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public AutoComplete() {
		setRendererType(DEFAULT_RENDERER);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public java.lang.String getWidgetVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
	}
	public void setWidgetVar(java.lang.String _widgetVar) {
		getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
		handleAttribute("widgetVar", _widgetVar);
	}

	public javax.el.MethodExpression getCompleteMethod() {
		return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.completeMethod, null);
	}
	public void setCompleteMethod(javax.el.MethodExpression _completeMethod) {
		getStateHelper().put(PropertyKeys.completeMethod, _completeMethod);
		handleAttribute("completeMethod", _completeMethod);
	}

	public java.lang.String getVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.var, null);
	}
	public void setVar(java.lang.String _var) {
		getStateHelper().put(PropertyKeys.var, _var);
		handleAttribute("var", _var);
	}

	public java.lang.String getItemLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.itemLabel, null);
	}
	public void setItemLabel(java.lang.String _itemLabel) {
		getStateHelper().put(PropertyKeys.itemLabel, _itemLabel);
		handleAttribute("itemLabel", _itemLabel);
	}

	public java.lang.Object getItemValue() {
		return getStateHelper().eval(PropertyKeys.itemValue, null);
	}
	public void setItemValue(java.lang.Object _itemValue) {
		getStateHelper().put(PropertyKeys.itemValue, _itemValue);
		handleAttribute("itemValue", _itemValue);
	}

	public int getMaxResults() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.maxResults, java.lang.Integer.MAX_VALUE);
	}
	public void setMaxResults(int _maxResults) {
		getStateHelper().put(PropertyKeys.maxResults, _maxResults);
		handleAttribute("maxResults", _maxResults);
	}

	public int getMinQueryLength() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.minQueryLength, 1);
	}
	public void setMinQueryLength(int _minQueryLength) {
		getStateHelper().put(PropertyKeys.minQueryLength, _minQueryLength);
		handleAttribute("minQueryLength", _minQueryLength);
	}

	public int getQueryDelay() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.queryDelay, 300);
	}
	public void setQueryDelay(int _queryDelay) {
		getStateHelper().put(PropertyKeys.queryDelay, _queryDelay);
		handleAttribute("queryDelay", _queryDelay);
	}

	public boolean isForceSelection() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.forceSelection, false);
	}
	public void setForceSelection(boolean _forceSelection) {
		getStateHelper().put(PropertyKeys.forceSelection, _forceSelection);
		handleAttribute("forceSelection", _forceSelection);
	}

	public java.lang.String getOnstart() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onstart, null);
	}
	public void setOnstart(java.lang.String _onstart) {
		getStateHelper().put(PropertyKeys.onstart, _onstart);
		handleAttribute("onstart", _onstart);
	}

	public java.lang.String getOncomplete() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.oncomplete, null);
	}
	public void setOncomplete(java.lang.String _oncomplete) {
		getStateHelper().put(PropertyKeys.oncomplete, _oncomplete);
		handleAttribute("oncomplete", _oncomplete);
	}

	public boolean isGlobal() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.global, true);
	}
	public void setGlobal(boolean _global) {
		getStateHelper().put(PropertyKeys.global, _global);
		handleAttribute("global", _global);
	}

	public int getScrollHeight() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.scrollHeight, java.lang.Integer.MAX_VALUE);
	}
	public void setScrollHeight(int _scrollHeight) {
		getStateHelper().put(PropertyKeys.scrollHeight, _scrollHeight);
		handleAttribute("scrollHeight", _scrollHeight);
	}

	public java.lang.String getEffect() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, null);
	}
	public void setEffect(java.lang.String _effect) {
		getStateHelper().put(PropertyKeys.effect, _effect);
		handleAttribute("effect", _effect);
	}

	public int getEffectDuration() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.effectDuration, 400);
	}
	public void setEffectDuration(int _effectDuration) {
		getStateHelper().put(PropertyKeys.effectDuration, _effectDuration);
		handleAttribute("effectDuration", _effectDuration);
	}

	public boolean isDropdown() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dropdown, false);
	}
	public void setDropdown(boolean _dropdown) {
		getStateHelper().put(PropertyKeys.dropdown, _dropdown);
		handleAttribute("dropdown", _dropdown);
	}

	public boolean isHideSelectedValue() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.hideSelectedValue, false);
	}
	public void setHideSelectedValue(boolean _hideSelectedValue) {
		getStateHelper().put(PropertyKeys.hideSelectedValue, _hideSelectedValue);
		handleAttribute("hideSelectedValue", _hideSelectedValue);
	}

	public java.lang.String getPanelStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.panelStyle, null);
	}
	public void setPanelStyle(java.lang.String _panelStyle) {
		getStateHelper().put(PropertyKeys.panelStyle, _panelStyle);
		handleAttribute("panelStyle", _panelStyle);
	}

	public java.lang.String getPanelStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.panelStyleClass, null);
	}
	public void setPanelStyleClass(java.lang.String _panelStyleClass) {
		getStateHelper().put(PropertyKeys.panelStyleClass, _panelStyleClass);
		handleAttribute("panelStyleClass", _panelStyleClass);
	}


    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("blur","change","valueChange","click","dblclick","focus","keydown","keypress","keyup","mousedown","mousemove","mouseout","mouseover","mouseup","select", "itemSelect"));

    private final Map<String,AjaxBehaviorEvent> customEvents = new HashMap<String,AjaxBehaviorEvent>();

    public final static String STYLE_CLASS = "ui-autocomplete";
    public final static String INPUT_CLASS = "ui-autocomplete-input ui-inputfield ui-widget ui-state-default ui-corner-all";
    public final static String INPUT_WITH_DROPDOWN_CLASS = "ui-autocomplete-input ui-inputfield ui-widget ui-state-default ui-corner-left";
    public final static String DROPDOWN_ICON_CLASS = "ui-autocomplete-dropdown ui-state-default ui-corner-right";
    public final static String PANEL_CLASS = "ui-autocomplete-panel ui-widget-content ui-corner-all ui-helper-hidden";
    public final static String LIST_CLASS = "ui-autocomplete-items ui-autocomplete-list ui-widget-content ui-widget ui-corner-all ui-helper-reset";
    public final static String TABLE_CLASS = "ui-autocomplete-items ui-autocomplete-table ui-widget-content ui-widget ui-corner-all ui-helper-reset";
    public final static String ITEM_CLASS = "ui-autocomplete-item ui-autocomplete-list-item ui-corner-all";
    public final static String ROW_CLASS = "ui-autocomplete-item ui-autocomplete-row ui-widget-content";

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        String eventName = context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);

        if(eventName != null && eventName.equals("itemSelect") && event instanceof AjaxBehaviorEvent) {
            customEvents.put("itemSelect", (AjaxBehaviorEvent) event);
            event.setPhaseId( PhaseId.INVOKE_APPLICATION );
        } else {
            super.queueEvent(event);
        }
    }

    private List suggestions = null;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getCompleteMethod();

		if(me != null && event instanceof org.primefaces.event.AutoCompleteEvent) {

            if(getSuggestions() == null) {
            	generateSuggestions(((org.primefaces.event.AutoCompleteEvent) event).getQuery());
            }

            if(getSuggestions() == null) {
                setSuggestions( new ArrayList() );
            }

            facesContext.renderResponse();
		}
	}
    
    public Map<String,AjaxBehaviorEvent> getCustomEvents() {
    	return customEvents;
    }

    public void generateSuggestions( String query ) {
    	FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getCompleteMethod();
    	setSuggestions( (List) me.invoke(facesContext.getELContext(), new Object[] { query }) );
    }

    public List getSuggestions() {
    	return suggestions;
    }

    public void setSuggestions(List<? extends AplosAbstractBean> suggestions) {
		this.suggestions = suggestions;
	}

    @Override
    public void validate(FacesContext context) {
        super.validate(context);

        if(isValid()) {
            for(Iterator<String> customEventIter = customEvents.keySet().iterator(); customEventIter.hasNext();) {
                AjaxBehaviorEvent behaviorEvent = customEvents.get(customEventIter.next());
                SelectEvent selectEvent = new SelectEvent(this, behaviorEvent.getBehavior(), getValue());
                selectEvent.setPhaseId( behaviorEvent.getPhaseId() );

                if(behaviorEvent.getPhaseId().equals(PhaseId.APPLY_REQUEST_VALUES)) {
                    selectEvent.setPhaseId(PhaseId.PROCESS_VALIDATIONS);
                }

                super.queueEvent(selectEvent);
            }
        }
    }

    public List<Column> getColums() {
        List<Column> columns = new ArrayList<Column>();

        for(UIComponent kid : this.getChildren()) {
            if(kid instanceof Column) {
				columns.add((Column) kid);
			}
        }

        return columns;
    }

	@Override
	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
	@Override
	public String resolveWidgetVar() {
		FacesContext context = FacesContext.getCurrentInstance();
		String userWidgetVar = (String) getAttributes().get("widgetVar");

		if(userWidgetVar != null) {
			return userWidgetVar;
		} else {
			return "widget_" + getClientId(context).replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
		}
	}

	public void handleAttribute(String name, Object value) {
		@SuppressWarnings("unchecked")
		List<String> setAttributes = (List<String>) this.getAttributes().get("javax.faces.component.UIComponentBase.attributesThatAreSet");
		if(setAttributes == null) {
			String cname = this.getClass().getName();
			if(cname != null && cname.startsWith(OPTIMIZED_PACKAGE)) {
				setAttributes = new ArrayList<String>(6);
				this.getAttributes().put("javax.faces.component.UIComponentBase.attributesThatAreSet", setAttributes);
			}
		}
		if(setAttributes != null) {
			if(value == null) {
				ValueExpression ve = getValueExpression(name);
				if(ve == null) {
					setAttributes.remove(name);
				} else if(!setAttributes.contains(name)) {
					setAttributes.add(name);
				}
			}
		}
	}
}
