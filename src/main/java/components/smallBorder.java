package components;
import java.io.IOException;

import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import com.aplos.common.utils.ComponentUtil;

public class smallBorder extends UINamingContainer {
	private String aplosSmallBorderLeftDiv;
	private String aplosSmallBorderMiddleDiv;
	private String aplosSmallBorderRightDiv;
	private String imgLeft;
	private String imgMiddle;
	private String imgRight;
	private String lineHeight;
	private String theme;

	@Override
	public void encodeBegin(FacesContext facesContext) throws IOException {
		super.encodeBegin(facesContext);
		theme = ComponentUtil.getTheme( facesContext );

		boolean isSearchText = ComponentUtil.determineBooleanAttributeValue( this, "isSearchText", false );
		if( isSearchText ) {
			setAplosSmallBorderLeftDiv("searchFieldLeftDiv");
			setAplosSmallBorderMiddleDiv("searchFieldMiddleDiv");
			setAplosSmallBorderRightDiv("searchFieldRightDiv");
			setImgLeft( ComponentUtil.getImageUrlWithTheme( facesContext, "searchFieldLeft.png" ) );
			setImgMiddle( ComponentUtil.getImageUrlWithTheme( facesContext, "searchFieldMiddle.png" ) );
			setImgRight( ComponentUtil.getImageUrlWithTheme( facesContext, "searchFieldRight.png" ) );
			setLineHeight( "45");
		} else {
			setAplosSmallBorderLeftDiv("aplosSmallBorderLeftDiv");
			setAplosSmallBorderMiddleDiv("aplosSmallBorderMiddleDiv");
			setAplosSmallBorderRightDiv("aplosSmallBorderRightDiv");
			setImgLeft( ComponentUtil.getImageUrlWithTheme( facesContext, "btnLeft.png" ) );
			setImgMiddle( ComponentUtil.getImageUrlWithTheme( facesContext, "btnMiddle.png" ) );
			setImgRight( ComponentUtil.getImageUrlWithTheme( facesContext, "btnRight.png" ) );
			setLineHeight( "30");
		}
	}

	public String getEndsWidth() {
		if (theme.equals("mellow")) {
			return "19";
		} else {
			return "30";
		}
	}

	public String getTheme() {
		return "classic";
	}

	public void setAplosSmallBorderLeftDiv(String aplosSmallBorderLeftDiv) {
		this.aplosSmallBorderLeftDiv = aplosSmallBorderLeftDiv;
	}

	public String getAplosSmallBorderLeftDiv() {
		return aplosSmallBorderLeftDiv;
	}

	public void setAplosSmallBorderMiddleDiv(String aplosSmallBorderMiddleDiv) {
		this.aplosSmallBorderMiddleDiv = aplosSmallBorderMiddleDiv;
	}

	public String getAplosSmallBorderMiddleDiv() {
		return aplosSmallBorderMiddleDiv;
	}

	public void setAplosSmallBorderRightDiv(String aplosSmallBorderRightDiv) {
		this.aplosSmallBorderRightDiv = aplosSmallBorderRightDiv;
	}

	public String getAplosSmallBorderRightDiv() {
		return aplosSmallBorderRightDiv;
	}

	public void setImgLeft(String imgLeft) {
		this.imgLeft = imgLeft;
	}

	public String getImgLeft() {
		return imgLeft;
	}

	public void setImgMiddle(String imgMiddle) {
		this.imgMiddle = imgMiddle;
	}

	public String getImgMiddle() {
		return imgMiddle;
	}

	public void setImgRight(String imgRight) {
		this.imgRight = imgRight;
	}

	public String getImgRight() {
		return imgRight;
	}

	public void setLineHeight(String lineHeight) {
		this.lineHeight = lineHeight;
	}

	public String getLineHeight() {
		return lineHeight;
	}
}
