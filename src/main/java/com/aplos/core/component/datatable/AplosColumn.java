package com.aplos.core.component.datatable;

import javax.el.MethodExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.component.FacesComponent;

import org.primefaces.component.column.Column;

@ResourceDependencies({

})
@FacesComponent("com.aplos.core.component.datatable.AplosColumn")
public class AplosColumn extends Column {


	public static final String COMPONENT_TYPE = "com.aplos.core.component.datatable.AplosColumn";
	public static final String COMPONENT_FAMILY = "com.aplos.core.component.datatable";

	protected enum PropertyKeys {
		action
		,actionAllowed;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public AplosColumn() {
		setRendererType(null);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public MethodExpression getAction() {
		return (MethodExpression) getStateHelper().eval(PropertyKeys.action, null);
	}
	public void setAction(MethodExpression _methodExpression) {
		getStateHelper().put(PropertyKeys.action, _methodExpression);
		handleAttribute("methodExpression", _methodExpression);
	}

	public boolean isActionAllowed() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.actionAllowed, true);
	}
	public void setActionAllowed(boolean _actionAllowed) {
		getStateHelper().put(PropertyKeys.actionAllowed, _actionAllowed);
		handleAttribute("actionAllowed", _actionAllowed);
	}
}