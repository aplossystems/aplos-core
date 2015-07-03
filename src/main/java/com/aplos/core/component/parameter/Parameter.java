package com.aplos.core.component.parameter;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import com.aplos.common.utils.ApplicationUtil;

@FacesComponent("com.aplos.core.component.parameter.Parameter")
public class Parameter extends UIParameter implements ActionListener {

    public static final String COMPONENT_TYPE = "com.aplos.core.component.Parameter";

    public static final String COMPONENT_FAMILY = UIParameter.COMPONENT_FAMILY;

    private static final String ASSIGN_TO = "assignTo";

    private static final String READ_AND_WRITE = "readAndWrite";

    /** ********************************************************* */

    /**
     * Converter for update value with this parameter
     */
    private Converter converter = null;

    /**
     * ********************************************************
     */


    protected enum Properties {
        noEscape
    }

	public boolean isReadAndWrite() {
		return (java.lang.Boolean) getStateHelper().eval(READ_AND_WRITE, false);
	}
	public void setReadAndWrite(boolean _isReadAndWrite) {
		getStateHelper().put(READ_AND_WRITE, _isReadAndWrite);
	}

    public boolean isNoEscape() {
        Boolean value = (Boolean) getStateHelper().eval(Properties.noEscape, false);
        return value;
    }

    public void setNoEscape(boolean noEscape) {
        getStateHelper().put(Properties.noEscape, noEscape);
    }

    public void setAssignToExpression(ValueExpression ve) {
        setValueExpression(ASSIGN_TO, ve);
    }

    public ValueExpression getAssignToExpression() {
        return getValueExpression(ASSIGN_TO);
    }

    public void setConverter(Converter converter) {
        clearInitialState();

        this.converter = converter;
    }

    public Converter getConverter() {
        return converter;
    }

    @Override
	public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
        FacesContext context = getFacesContext();
        ELContext elContext = context.getELContext();
        ValueExpression updateBinding = getAssignToExpression();

        if (updateBinding != null && (!updateBinding.isReadOnly(elContext))) {
            String requestValue = context.getExternalContext().getRequestParameterMap().get(getName());

            Object convertedValue = requestValue;

            if (requestValue != null) {
                Class<?> type = updateBinding.getType(elContext);
                Converter converter = createConverter(context, type);

                if (null != converter) {
                    convertedValue = converter.getAsObject(context, this, requestValue);
                }
            }

            if (null != convertedValue) {
                updateBinding.setValue(elContext, convertedValue);
            }
        }
    }
    
    @Override
    public void decode(FacesContext context) {
    	super.decode(context);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.component.UIParameter#getName()
     */

    @Override
	public String getName() {
        String name = super.getName();

        // If name not set - use clientId. be Careful !
        if (null == name) {
            name = getClientId(FacesContext.getCurrentInstance());
        }

        return name;
    }

    @Override
	public Object getValue() {
        Object value = super.getValue();

        // TODO - perform conversion if converter is present.
        if (null != value) {
            Class<?> type = value.getClass();
            FacesContext context = getFacesContext();
            Converter converter = createConverter(context, type);

            if (null != converter) {
                value = converter.getAsString(context, this, value);
            }
        }

        return value;
    }

    /** ********************************************************* */

    /**
     * @param context Faces Context
     * @param type    Type of class
     * @return converter
     * @throws FacesException if something goes wrong
     */
    public Converter createConverter(FacesContext context, Class<?> type) throws FacesException {
        Converter converter = getConverter();

        if (converter == null && type != null && !type.equals(String.class)
                && !type.equals(Object.class)) {
            try {
                converter = context.getApplication().createConverter(type);
            } catch (Exception e) {
            	ApplicationUtil.getAplosContextListener().handleError( e );
            }
        }

        return converter;
    }
}

