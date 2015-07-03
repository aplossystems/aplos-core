package components;
import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.utils.JSFUtil;

public class editButtons extends UINamingContainer {
//	private String editPageId;
//	private MethodExpression applyMethod;
//	private MethodExpression okMethod;
//	private MethodExpression cancelMethod;

	@Override
	public void encodeBegin(FacesContext facesContext) throws IOException {
		super.encodeBegin(facesContext);
//		EditPage editPage = (EditPage) getAttributes().get( "editPage" );
//		if( editPage == null ) {
//			editPageId = "backingPage";
//			editPage = (EditPage) JSFUtil.resolveVariable( editPageId );
//		} else {
//			editPageId = ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() );
//		}
//		ExpressionFactory expf = facesContext.getApplication().getExpressionFactory();
//		applyMethod = expf.createMethodExpression(facesContext.getELContext(), "#{" + editPageId  + ".applyBtnAction }", null, new Class[] {});
//		okMethod = expf.createMethodExpression(facesContext.getELContext(), "#{" + editPageId  + ".okBtnAction }", null, new Class[] {});
//		cancelMethod = expf.createMethodExpression(facesContext.getELContext(), "#{" + editPageId  + ".cancelBtnAction }", null, new Class[] {});
	}

	public EditPage determineEditPage() {
		EditPage editPage=null;
		editPage = (EditPage) getAttributes().get( AplosScopedBindings.EDIT_PAGE );
		if ( editPage == null ) {
			editPage = (EditPage) JSFUtil.getCurrentBackingPage();
		}
		return editPage;
	}

}
