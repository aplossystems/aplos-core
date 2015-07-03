package components;
import java.util.List;

import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

public class sortable extends UINamingContainer {
	private int previousIdx;
	private int newIdx;
	
	public void postionUpdated() {
		List list = (List) getAttributes().get( "value" );
		Object object = list.remove( previousIdx );
		list.add( newIdx, object );
	}
	
	@Override
	public void processDecodes(FacesContext context) {
		// TODO Auto-generated method stub
		super.processDecodes(context);
	}
	
	public int getPreviousIdx() {
		return previousIdx;
	}
	public void setPreviousIdx(int previousIdx) {
		this.previousIdx = previousIdx;
	}
	public int getNewIdx() {
		return newIdx;
	}
	public void setNewIdx(int newIdx) {
		this.newIdx = newIdx;
	}
}
