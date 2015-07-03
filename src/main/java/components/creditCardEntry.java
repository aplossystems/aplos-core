package components;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;

import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.CreditCardDetails;
import com.aplos.common.beans.CreditCardType;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.core.interfaces.CreditCardEntryListener;


public class creditCardEntry extends UIInput implements NamingContainer {
	public static final String CREDIT_CARD_DETAILS = "creditCardDetails";
	private List<SelectItem> storedDetailsSelectItems;
	
	@Override
	public String getFamily() { return "javax.faces.NamingContainer"; }
	
	public creditCardEntry() {
		createStoredDetailsSelectItems();
	}

	public void createStoredDetailsSelectItems() {
		setStoredDetailsSelectItems(new ArrayList<SelectItem>());
		getStoredDetailsSelectItems().add( new SelectItem(true, "Use stored credit card" ) );
		getStoredDetailsSelectItems().add( new SelectItem(false, "Enter new credit card details" ) );
	}

	public SelectItem[] getCardTypeSelectItems() {
		return AplosBean.getSelectItemBeans( CreditCardType.class );
	}

	public void validateCreditCardNumber(FacesContext context, UIComponent toValidate, Object value) {
		String cardNumber = (String) value;

		int numberLength = cardNumber.replace( " ", "" ).trim().length();
		if ( numberLength > 12 && numberLength < 17 ) {
			try {
				Long.parseLong( cardNumber.replace( " ", "" ).trim() );
			} catch( NumberFormatException nfex ) {
				FacesMessage message = new FacesMessage("Please enter your 13-16 digit card number exactly as shown on the front of your card.");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				context.addMessage(toValidate.getClientId(context), message);
				((UIInput) toValidate).setValid(false);
			}
		} else {
			FacesMessage message = new FacesMessage("Please enter your 13-16 digit card number exactly as shown on the front of your card.");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(toValidate.getClientId(context), message);
			((UIInput) toValidate).setValid(false);
		}
	}

	public void validateCvv(FacesContext context, UIComponent toValidate, Object value) {
		String cvv = (String) value;

		int numberLength = cvv.replace( " ", "" ).trim().length();
		if ( numberLength == 3 ) {
			try {
				Long.parseLong( cvv.replace( " ", "" ).trim() );
			} catch( NumberFormatException nfex ) {
				if( determineCreditCardEntryListener() != null ) {
					determineCreditCardEntryListener().cvvNumericFailed();
				}
//				EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart(CartAbandonmentIssue.CVV_INCORRECT);
				FacesMessage message = new FacesMessage("Please enter your 3 digit security number exactly as shown on the back of your card.");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				context.addMessage(toValidate.getClientId(context), message);
				((UIInput) toValidate).setValid(false);
			}
		} else {
			if( determineCreditCardEntryListener() != null ) {
				determineCreditCardEntryListener().cvvLengthFailed();
			}
//			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart(CartAbandonmentIssue.CVV_INCORRECT_LENGTH);
			FacesMessage message = new FacesMessage("Please enter your 3 digit security number exactly as shown on the back of your card.");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(toValidate.getClientId(context), message);
			((UIInput) toValidate).setValid(false);
		}
	}

	public SelectItem[] getMonthSelectItems() {
		SelectItem[] months = new SelectItem[12];
		for (int i = 0; i < 12; i++) {
			months[i] = new SelectItem((i + 1), ((i < 9) ? "0" : "") + (i + 1) );
		}
		return months;
	}

	public SelectItem[] getStartMonthSelectItems() {
		SelectItem[] months = new SelectItem[13];
		months[0] = new SelectItem(null, "---");
		for (int i = 0; i < 12; i++) {
			months[i + 1] = new SelectItem((i + 1), ((i < 9) ? "0" : "")
					+ (i + 1));
		}
		return months;
	}

	public SelectItem[] getIssueNumberSelectItems() {
		SelectItem[] issues = new SelectItem[100];
		issues[0] = new SelectItem(null, "---");
		for (int i = 1; i < 100; i++) {
			issues[i] = new SelectItem(i, "" + i);
		}
		return issues;
	}
	
	public int getCentury() {
		Calendar cal = Calendar.getInstance();
		int thisYear = cal.get(Calendar.YEAR);
		return thisYear/100;
		
	}

	public SelectItem[] getStartYearSelectItems() {
		SelectItem[] years = new SelectItem[11];
		years[0] = new SelectItem(null, "---");
		Calendar cal = Calendar.getInstance();
		int thisYear = cal.get(Calendar.YEAR);
		int century = getCentury();
		int yearMinusCentury = thisYear - (century * 100);
		for (int i = 0; i < 10; i++) {
			years[i + 1] = new SelectItem( yearMinusCentury, "" + ((century * 100) + yearMinusCentury));
			yearMinusCentury = --yearMinusCentury % 100;
		}
		return years;
	}

	public SelectItem[] getEndYearSelectItems() {
		SelectItem[] years = new SelectItem[10];
		Calendar cal = Calendar.getInstance();
		int thisYear = cal.get(Calendar.YEAR);
		int century = getCentury();
		int yearMinusCentury = thisYear - (century * 100);
		for (int i = 0; i < 10; i++) {
			years[i] = new SelectItem( yearMinusCentury, "" + ((century * 100) + yearMinusCentury) );
			yearMinusCentury = ++yearMinusCentury % 100;
		}
		return years;
	}
	
	@Override
	protected Object getConvertedValue(FacesContext context, Object newSubmittedValue) throws ConverterException {
		CreditCardDetails creditCardDetails = (CreditCardDetails) getAttributes().get(CREDIT_CARD_DETAILS);

		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		Date today = cal.getTime();
		int currentCentury = getCentury();
		int currentYear = cal.get( Calendar.YEAR );
		
		int expiryYear = creditCardDetails.getExpiryYear() + (currentCentury * 100);
		if( expiryYear < currentYear ) {
			expiryYear = creditCardDetails.getExpiryYear() + ((currentCentury+1) * 100);
		}
		cal.set(Calendar.YEAR, expiryYear);
		cal.set(Calendar.MONTH, creditCardDetails.getExpiryMonth() - 1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date lastDayValid = cal.getTime();

		if (!lastDayValid.after(today)) {
			JSFUtil.addMessageForError("Sorry, this card is expired, we cannot accept payment with it.");
			setValid( false );
		}
		
		Date firstDayValid = null;
		if (creditCardDetails.getStartYear() != null && creditCardDetails.getStartMonth() != null) {
			int startYear = creditCardDetails.getStartYear() + (currentCentury * 100);
			if( startYear > currentYear ) {
				startYear = creditCardDetails.getStartYear() + ((currentCentury-1) * 100);
			}
			cal.set(Calendar.YEAR, startYear);
			cal.set(Calendar.MONTH, creditCardDetails.getStartMonth() - 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			firstDayValid = FormatUtil.removeDatesTime(cal.getTime());
		}
		if (creditCardDetails.getStartYear() != null && creditCardDetails.getStartMonth() != null && firstDayValid.before(today)) {
			JSFUtil.addMessageForError("Sorry, this card has not yet become active, we cannot accep payment with it.");
			setValid( false );
		}
		return super.getConvertedValue(context, newSubmittedValue);
	}
	
	public CreditCardEntryListener determineCreditCardEntryListener() {
		return (CreditCardEntryListener) getAttributes().get( "creditCardEntryListener" );
	}

	public List<SelectItem> getStoredDetailsSelectItems() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		selectItems.add( new SelectItem(true, "Use stored credit card" ) );
		selectItems.add( new SelectItem(false, "Enter new credit card details" ) );
		return selectItems;
	}

	public void setStoredDetailsSelectItems(List<SelectItem> creditCardSelectItems) {
		this.storedDetailsSelectItems = creditCardSelectItems;
	}
	
}
