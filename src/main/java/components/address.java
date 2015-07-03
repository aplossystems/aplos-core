package components;
import java.io.IOException;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UINamingContainer;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;

import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.Country;
import com.aplos.common.beans.CountryArea;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.enums.ContactTitle;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ComponentUtil;
import com.aplos.common.utils.JSFUtil;

public class address extends UINamingContainer {

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		Address address = getAddress();
		if ( address != null && address.getState() != null && address.getCountry() != null &&
				(address.getCountry().getId().equals(840l) || address.getCountry().getId().equals(124l))) {
			BeanDao countryAreaDao = new BeanDao( CountryArea.class ).addWhereCriteria( "areaCode = :countryAreaName AND (country.id = " + address.getCountry().getId() + ")" );
			countryAreaDao.setNamedParameter( "countryAreaName", address.getState() );
			List<CountryArea> countryAreaList = countryAreaDao.getAll();
			if ( countryAreaList.size() > 0 ) {
				address.setCountryArea(countryAreaList.get( 0 ));
			}
		}
		super.encodeBegin(context);
	}
	
	@Override
	public void decode(FacesContext context) {
		super.decode(context);
	}

	public boolean isPostcodeRequired() {
		Country country;
		if( JSFUtil.getFacesContext().getCurrentPhaseId().getOrdinal() > PhaseId.UPDATE_MODEL_VALUES.getOrdinal() ) {
			country = (Country) ((HtmlSelectOneMenu) findComponent( "countryDdl" )).getValue();
		} else {
			country = (Country) ((HtmlSelectOneMenu) findComponent( "countryDdl" )).getLocalValue();
		}
		
		boolean isEcommerceFieldsRequired = ComponentUtil.determineBooleanAttributeValue( this, "ecommerceFieldsRequired", false);
		String postcodeValue = String.valueOf( getAttributes().get("postcode") );
		return ("required".equals( postcodeValue ))
				|| (CommonUtil.isPostcodeRequired( country ) && (isEcommerceFieldsRequired || "conditionallyRequired".equals( postcodeValue )));
	}

	public boolean isAddressValid() {
		Object address = getAttributes().get( "bean" );
		return address != null && address instanceof Address;
	}

	public Address getAddress() {
		return (Address) getAttributes().get( "bean" );
	}

	public boolean isValidationRequired() {
		return ComponentUtil.determineBooleanAttributeValue( this, "validation", true );
	}

	public boolean isValidationRequiredLater() {
		//!= because if it has any el we assume we use validation (but dont waste time evaluating)
		if ( getValueExpression( "validation" ) != null ) {
			return true;
		} else {
			return ComponentUtil.determineBooleanAttributeValue( this, "validation", true );
		}
	}

	public boolean isValidationRequiredValid() {
		Object validationRequiredAttribute = getAttributes().get( "validation" );
		if (validationRequiredAttribute == null || validationRequiredAttribute instanceof Boolean || validationRequiredAttribute instanceof String || validationRequiredAttribute instanceof ValueExpression) {
			return true;
		}
		return false;
	}

	public SelectItem[] getCountrySelectItems() {
		return ApplicationUtil.getAplosModuleFilterer().getCountrySelectItems();
	}

	public List<SelectItem> getContactTitleSelectItems() {
		return CommonUtil.getEnumSelectItemsWithNotSelected(ContactTitle.class );
	}

	public SelectItem[] getStateSelectItems() {
		if( getAddress().getCountry() != null ) {
			List<CountryArea> canadianCountryAreas = new BeanDao( CountryArea.class ).addWhereCriteria( "country.id = " + getAddress().getCountry().getId() ).setIsReturningActiveBeans(true).getAll();
			return AplosBean.getSelectItemBeansWithNotSelected( canadianCountryAreas );
		} else {
			return new SelectItem[ 0 ];
		}
	}

	public boolean isShowingStates() {
		Address address = getAddress();
		if ( address.getCountry() != null && 
				(address.getCountry().getId().equals( 124l ) || address.getCountry().getId().equals( 840l )) ) {
			return true;
		} else {
			return false;
		}
	}

	private boolean shouldRender(String attributeName, boolean defaultValue, boolean ecommerceFieldsRequired) {
		Object attributeToTest = getAttributes().get( attributeName );
		if ( attributeToTest instanceof ValueExpression ) {
			try {

				return (Boolean) ((ValueExpression)attributeToTest).getValue(JSFUtil.getFacesContext().getELContext());

			} catch (ClassCastException cce) {
				JSFUtil.addMessageForError("The ValueExpression supplied for " + attributeName + " on " + getId() + " is invalid. (Does not equate to a boolean value)");
				return false;
			}
		} else {
			if( getAttributes().get( attributeName ) == null ) {
				if( ecommerceFieldsRequired ) {
					getAttributes().put( attributeName, "required" );
					return true;
				} else {
					return defaultValue;
				}
			} else if( (getAttributes().get( attributeName ).equals( "true" ) || getAttributes().get( attributeName ).equals( "required" )) ||
					(getAttributes().get( attributeName ) instanceof Boolean && (Boolean)getAttributes().get( attributeName ) == true)) {
				return true;
			} else if( getAttributes().get( attributeName ).equals( "false" ) ||
					(getAttributes().get( attributeName ) instanceof Boolean && (Boolean)getAttributes().get( attributeName ) == false)) {
				return false;
			} else {
				return defaultValue;
			}

		}
	}
	
	public String getEmailAddress() {
		Address address = getAddress();
		return getAddress().getSubscriber().getEmailAddress();
	}
	
	public void setEmailAddress( String emailAddress ) {
		Address address = getAddress();
		if( CommonUtil.compare( emailAddress, address.getSubscriber().getEmailAddress() ) != 0 ) {
			if( !CommonUtil.isNullOrEmpty( emailAddress ) ) {
				Subscriber subscriber = Subscriber.getOrCreateSubscriber(emailAddress);
				address.setSubscriber( subscriber );
			} else {
				address.setSubscriber( new Subscriber() );
			}
		}
		
		boolean updateSubscriber = ComponentUtil.determineBooleanAttributeValue( this, "updateSubscriber", false);
		if( address.getSubscriber() != null && updateSubscriber ) {
			address.getSubscriber().setFirstName( address.getContactFirstName() );
			address.getSubscriber().setSurname( address.getContactSurname() );
		}
	}

	public boolean isRenderContactTitle() {
		return shouldRender("contactTitle", false, false );
	}

	public boolean isRenderContactFirstName() {
		return shouldRender("contactFirstName", true, ComponentUtil.determineBooleanAttributeValue( this, "ecommerceFieldsRequired", false) );
	}

	public boolean isRenderContactSurname() {
		return shouldRender("contactSurname", true, ComponentUtil.determineBooleanAttributeValue( this, "ecommerceFieldsRequired", false) );
	}

	public boolean isRenderCompanyName() {
		return shouldRender("companyName", false, false );
	}

	public boolean isRenderContactName() {
		return shouldRender("contactName", true, ComponentUtil.determineBooleanAttributeValue( this, "ecommerceFieldsRequired", false));
	}

	public boolean isRenderCountry() {
		return shouldRender("country", true, ComponentUtil.determineBooleanAttributeValue( this, "ecommerceFieldsRequired", false));
	}

	public boolean isRenderLine1() {
		return shouldRender("line1", true, ComponentUtil.determineBooleanAttributeValue( this, "ecommerceFieldsRequired", false));
	}

	public boolean isRenderLine2() {
		return shouldRender("line2", true, false );
	}

	public boolean isRenderLine3() {
		return shouldRender("line3", true, false);
	}

	public boolean isRenderCity() {
		return shouldRender("city", true, ComponentUtil.determineBooleanAttributeValue( this, "ecommerceFieldsRequired", false));
	}

	public boolean isRenderState() {
		return shouldRender("state", true, ComponentUtil.determineBooleanAttributeValue( this, "ecommerceFieldsRequired", false) );
	}

	public boolean isRenderPostcode() {
		return shouldRender("postcode", true, ComponentUtil.determineBooleanAttributeValue( this, "ecommerceFieldsRequired", false));
	}

	public boolean isRenderEmail() {
		return shouldRender("email", false, false);
	}

	public boolean isRenderPhone() {
		return shouldRender("phone", false, false);
	}

	public boolean isRenderPhone2() {
		return shouldRender("phone2", false, false);
	}

	public boolean isRenderMobile() {
		return shouldRender("mobile", false, false);
	}

	public boolean isRenderFax() {
		return shouldRender("fax", false, false);
	}
}
