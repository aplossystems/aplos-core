package com.aplos.core.component.parameter;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.ActionSource;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.PreRenderViewEvent;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.MetaRuleset;
import javax.faces.view.facelets.Metadata;
import javax.faces.view.facelets.MetadataTarget;
import javax.faces.view.facelets.TagAttribute;

import org.primefaces.context.RequestContext;

import com.aplos.common.utils.CommonUtil;

public class ParameterHandler extends ComponentHandler {

	private static final ActionParamMetaRule ACTION_PARAM_META_RULE = new ActionParamMetaRule();

	public static class ActionParamMetaRule extends MetaRule {

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.ajax4jsf.tag.SuggestionHandler.SuggestionMetaRule#applyRule(java
		 * .lang.String, com.sun.facelets.tag.TagAttribute,
		 * com.sun.facelets.tag.MetadataTarget)
		 */

		@Override
		public Metadata applyRule(String name, TagAttribute attribute,
				MetadataTarget meta) {
			if (meta.isTargetInstanceOf(Parameter.class)) {
				if ("assignTo".equals(name)) {
					return new AssignToValueExpressionMetadata(attribute);
				} else if ("converter".equals(name)) {
					if (attribute.isLiteral()) {
						return new LiteralConverterMetadata(
								attribute.getValue());
					} else {
						return new DynamicConverterMetadata(attribute);
					}

				}
			}

			return null;
		}

	}

	static final class LiteralConverterMetadata extends Metadata {

		private final String converterId;

		public LiteralConverterMetadata(String converterId) {
			this.converterId = converterId;
		}

		@Override
		public void applyMetadata(FaceletContext ctx, Object instance) {
			((Parameter) instance).setConverter(ctx.getFacesContext()
					.getApplication().createConverter(this.converterId));
		}
	}

	static final class DynamicConverterMetadata extends Metadata {

		private final TagAttribute attr;

		public DynamicConverterMetadata(TagAttribute attr) {
			this.attr = attr;
		}

		@Override
		public void applyMetadata(FaceletContext ctx, Object instance) {
			((Parameter) instance).setConverter((Converter) this.attr
					.getObject(ctx, Converter.class));
		}
	}

	static final class AssignToValueExpressionMetadata extends Metadata {

		private final TagAttribute attr;

		public AssignToValueExpressionMetadata(TagAttribute attr) {
			this.attr = attr;
		}

		@Override
		public void applyMetadata(FaceletContext ctx, Object instance) {
			((Parameter) instance).setAssignToExpression(attr
					.getValueExpression(ctx, Object.class));
		}
	}

	private TagAttribute assignTo;

	/**
	 * @param config
	 */
	public ParameterHandler(ComponentConfig config) {
		super(config);
		assignTo = getAttribute("assignTo");

		if (null != assignTo) {
			if (assignTo.isLiteral()) {
//				throw new TagAttributeException(this.tag, this.assignTo,
//						Messages.getMessage(Messages.MUST_BE_EXPRESSION_ERROR));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
	 * javax.faces.component.UIComponent)
	 */

	@Override
	public void onComponentCreated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		if (parent instanceof ActionSource) {
			if (assignTo != null) {
				Parameter al = (Parameter) c;
				((ActionSource) parent).addActionListener(al);
			}

			ctx.getFacesContext().getViewRoot().subscribeToEvent(PreRenderViewEvent.class, new AddContextParameterListener(ctx,(Parameter)c) );
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.ajax4jsf.tag.AjaxComponentHandler#createMetaRuleset(java.lang.Class)
	 */

	@Override
	@SuppressWarnings("rawtypes")
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset metaRules = super.createMetaRuleset(type);
		metaRules.addRule(ACTION_PARAM_META_RULE);
		return metaRules;
	}
    

    // ----------------------------------------------------------- Inner Classes


    public class AddContextParameterListener implements ComponentSystemEventListener, StateHolder {
    	private FaceletContext ctx;
    	private Parameter parameter;
        // --------------------------- Methods from ComponentSystemEventListener
    	
    	public AddContextParameterListener(FaceletContext ctx, Parameter parameter) {
			this.ctx = ctx;
			this.parameter = parameter;
		}


        // ------------------------------------------------ Methods from StateHolder


        public Object saveState(FacesContext context) {
            if (context == null) {
                throw new NullPointerException();
            }
            return null;
        }

        public void restoreState(FacesContext context, Object state) {
            if (context == null) {
                throw new NullPointerException();
            }
        }

        public boolean isTransient() {
            return true;
        }

        public void setTransient(boolean newTransientValue) {
            // no-op
        }


        public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
			FacesContext context = ctx.getFacesContext();
			ELContext elContext = context.getELContext();
			ValueExpression updateBinding = parameter.getAssignToExpression();
  	        if (updateBinding != null) {

  	  	      RequestContext requestContext = RequestContext.getCurrentInstance();
  	          Object bindingValue = updateBinding.getValue(elContext);
              if(requestContext != null) {
            	  if( parameter.isReadAndWrite() ) {
	            	  if( !CommonUtil.isNullOrEmpty( parameter.getName() )
	            			  && bindingValue != null && bindingValue != "" ) { 
	            		  requestContext.addCallbackParam("updateParameter_" + parameter.getName(), bindingValue );
	            	  }
            	  }
              }
  	        }
        }


    }

}
