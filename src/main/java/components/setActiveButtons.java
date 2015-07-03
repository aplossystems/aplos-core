package components;
import javax.el.ExpressionFactory;
import javax.faces.component.UINamingContainer;

import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

public class setActiveButtons extends UINamingContainer {
	
	public String getJsFriendlyClientId() {
		String clientId = getClientId();
		return clientId.replace( ":", "_" );
	}

	public AplosBean getAssociatedBean() {
		
		/**
		 * Have to find the associated bean where its used (here) not in encode begin, otherwise when we execute
		 * the action the associated bean has been set to null, this evaluates to false and the action doesnt fire 
		 */
		
		EditPage editPage = (EditPage) getAttributes().get( AplosScopedBindings.EDIT_PAGE );
		if( editPage == null ) {
			editPage = (EditPage) JSFUtil.resolveVariable( AplosScopedBindings.BACKING_PAGE );
		} 
		AplosBean associatedBean = null;
		if ( editPage != null ) {
			associatedBean = editPage.resolveAssociatedBean();
			if ( associatedBean == null ) {
				associatedBean = JSFUtil.getBeanFromView( (Class<? extends AplosBean>) editPage.getBeanDao().getBeanClass() );
			}
		}
		return associatedBean;
	}

	public boolean isRenderActiveButtons() {
		
		AplosBean associatedBean = getAssociatedBean();

		return associatedBean != null && !associatedBean.isNew();
	}
	
	public boolean isActive() {
		
		AplosBean associatedBean = getAssociatedBean();
		
		return associatedBean != null && associatedBean.isActive();
	}

//	public MethodExpression getDeleteMethod() {
//		return deleteMethod;
//	}
//
//	public MethodExpression getReinstateMethod() {
//		return reinstateMethod;
//	}
	
	public void finishDelete() {
		/**
		 * Have to get the method binding here, doing it in encode begin doesnt work - when we execute the action its all null again
		 */
		
		BackingPage backingPage = (BackingPage) getAttributes().get( AplosScopedBindings.EDIT_PAGE );
		String editPageId = null;
		if ( backingPage == null ) {
			backingPage = (BackingPage) JSFUtil.resolveVariable( "backingPage" );
			editPageId = AplosScopedBindings.BACKING_PAGE;
		} else {
			editPageId = CommonUtil.getBinding(backingPage.getClass()); //ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() );
		}
		ExpressionFactory expf = JSFUtil.getFacesContext().getApplication().getExpressionFactory();
		expf.createMethodExpression(JSFUtil.getFacesContext().getELContext(), "#{" + editPageId  + ".deleteBean }", null, new Class[] {}).invoke(JSFUtil.getFacesContext().getELContext(), new Object[] {});
	}
	
	public void doDelete() { /* do nothing */ }

	public String doReinstate() {
//		reinstateMethod.invoke(JSFUtil.getFacesContext().getELContext(), new Object[] {});
		
		EditPage editPage = (EditPage) getAttributes().get( AplosScopedBindings.EDIT_PAGE );
		String editPageId = null;
		if( editPage == null ) {
			editPageId = AplosScopedBindings.BACKING_PAGE;
		} else {
			editPageId = CommonUtil.getBinding(editPage.getClass()); //ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() );
		}
		ExpressionFactory expf = JSFUtil.getFacesContext().getApplication().getExpressionFactory();
		expf.createMethodExpression(JSFUtil.getFacesContext().getELContext(), "#{" + editPageId  + ".reinstateBean }", null, new Class[] {}).invoke(JSFUtil.getFacesContext().getELContext(), new Object[] {});

		return null;
	}
}
