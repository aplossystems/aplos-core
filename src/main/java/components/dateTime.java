package components;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ComponentUtil;
import com.aplos.core.component.InternalComponentHolder;

public class dateTime extends UIInput implements NamingContainer, InternalComponentHolder {
	public static final String START_YEAR_DIFF_ATTRIBUTE_KEY = "startYearDiff";
	public static final String END_YEAR_DIFF_ATTRIBUTE_KEY = "endYearDiff";
	public static final String SHOW_CHECKBOX_ATTRIBUTE_KEY = "showCheckbox";
	public static final String SHOW_TIME_ATTRIBUTE_KEY = "showTime";
	public static final String SHOW_DATE_ATTRIBUTE_KEY = "showDate";

	private boolean dateChkBoxSelected = true;
	private Integer year = null;
	private Integer month = null;
	private Integer day = null;
	private Integer hour = null;
	private Integer minute = null;

	public dateTime() {
		//do nothing
	}

	//#### UIInput stuff ######

	@Override
	public String getFamily() { return "javax.faces.NamingContainer"; }

	@Override
	public void initialiseComponents(FacesContext facesContext) {
		setDdlsFromValue();
	}
	
	public Date setDdlsFromValue() {
        Date beanDate = (Date) getValue();
		Calendar cal = Calendar.getInstance();
        if (beanDate == null) {
        	Date defaultDate = (Date) getAttributes().get( "defaultDate" );
        	if( defaultDate == null ) {
        		cal.setTime(new Date());
        	} else {
        		cal.setTime(defaultDate);
        	}
        } else {
        	cal.setTime(beanDate);
        }

		UIInput dayComponent = (UIInput) findComponent("dayDd");
		UIInput monthComponent = (UIInput) findComponent("monthDd");
        UIInput yearComponent = (UIInput) findComponent("yearDd");
        UIInput hourComponent = (UIInput) findComponent("hourDd");
        UIInput minuteComponent = (UIInput) findComponent("minuteDd");
        int day = cal.get(Calendar.DAY_OF_MONTH);
        dayComponent.setValue(day);
        int month = cal.get(Calendar.MONTH);
        monthComponent.setValue(month);
        int year = cal.get(Calendar.YEAR);
        yearComponent.setValue(year);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        hourComponent.setValue(hour);
        int minute = cal.get(Calendar.MINUTE);
        minuteComponent.setValue(minute);
        
        return beanDate;
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
        super.encodeBegin(context);
        Date beanDate = setDdlsFromValue();

        if( ComponentUtil.determineBooleanAttributeValue( this, "showCheckbox", true ) ) {
            if( beanDate == null ) {
            	setDateChkBoxSelected(false);
            } else {
            	setDateChkBoxSelected(true);
            }
        }
    }

	@Override
	public Object getSubmittedValue() {
		//http://weblogs.java.net/blog/cayhorstmann/archive/2010/01/30/composite-input-components-jsf
		return this; // Any non-null value will do
	}

	@Override
	public void decode(FacesContext context) {
		super.decode(context);
	}
	
	@Override
	public void processDecodes(FacesContext context) {
		super.processDecodes(context);
	}
	
	/*
	 * This is method is required, because if the component is loaded with the checkbox unticked,
	 * then the getConvertedMethod won't have submitted values in the ddl's as they would
	 * have been disabled and therefore JSF will not put the values from the request into
	 * the component.  Therefore we set the date for this circumstance.
	 */
	public void setTodaysDate() {
		setValue( new Date() );
	}

	@Override //all validation should happen in the validateValue method
	protected Object getConvertedValue(FacesContext context, Object newSubmittedValue) {
		Date dateToPropagate = null;
				
		UIInput checkboxComponent = (UIInput) findComponent("checkboxComponent");
		if (checkboxComponent != null && checkboxComponent.getSubmittedValue() != null) {
			setDateChkBoxSelected((Boolean)checkboxComponent.getSubmittedValue());
		}
		UIInput dayComponent = null;
		UIInput monthComponent = null;
        UIInput yearComponent = null;
		if (isDateChkBoxSelected()) {
			boolean valuesSet = true;
			Calendar cal = Calendar.getInstance();
			Object showDateObj = getAttributes().get("showDate");
			Object showTimeObj = getAttributes().get("showTime");
			if ((showDateObj instanceof String && showDateObj.equals("true")) ||
				(showDateObj instanceof Boolean && showDateObj.equals(true))) {
				dayComponent = (UIInput) findComponent("dayDd");
				monthComponent = (UIInput) findComponent("monthDd");
		        yearComponent = (UIInput) findComponent("yearDd");
				String dayStr = (String)dayComponent.getSubmittedValue();
		        if (dayStr != null && !dayStr.equals("")) {
		        	day = Integer.parseInt(dayStr);
		        	cal.set(Calendar.DAY_OF_MONTH, day);
		        } else {
		        	valuesSet = false;
		        }
		        String monthStr = (String)monthComponent.getSubmittedValue();
		        if (monthStr != null && !monthStr.equals("")) {
		        	month = Integer.parseInt(monthStr);
		        	cal.set(Calendar.MONTH, month);
		        } else {
		        	valuesSet = false;
		        }
		        String yearStr = (String)yearComponent.getSubmittedValue();
		        if (yearStr != null && !yearStr.equals("")) {
		        	year = Integer.parseInt(yearStr);
		        	cal.set(Calendar.YEAR, year);
		        } else {
		        	valuesSet = false;
		        }
			}
			if ((showTimeObj instanceof String && showTimeObj.equals("true")) ||
				(showTimeObj instanceof Boolean && showTimeObj.equals(true))) {
				UIInput hourComponent = (UIInput) findComponent("hourDd");
		        UIInput minuteComponent = (UIInput) findComponent("minuteDd");
		        String hourStr = (String)hourComponent.getSubmittedValue();
		        if (hourStr != null && !hourStr.equals("")) {
		        	hour = Integer.parseInt(hourStr);
		        	cal.set(Calendar.HOUR_OF_DAY, hour);
		        } else {
		        	valuesSet = false;
		        }
		        String minuteStr = (String)minuteComponent.getSubmittedValue();
		        if (minuteStr != null && !minuteStr.equals("")) {
		        	minute = Integer.parseInt(minuteStr);
		        	cal.set(Calendar.MINUTE, minute);
		        } else {
		        	valuesSet = false;
		        }
			}
			
			/*
			 * If the values haven't yet been set, it's probably because the check box has 
			 * only just been ticked, in which case set the value to the default date if available.
			 */
			if( valuesSet ) {
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dateToPropagate = cal.getTime();
			} else {
		        Date beanDate = (Date) getValue();
		        if (beanDate == null) {
		        	Date defaultDate = (Date) getAttributes().get( "defaultDate" );
		        	if( defaultDate == null ) {
		        		dateToPropagate = new Date();
	        			cal.setTime( dateToPropagate );
		        	} else {
		        		dateToPropagate = defaultDate;
		        	}
		        } else {
		        	dateToPropagate = beanDate;
		        }
			}
		}
		return dateToPropagate;
    }

	//#### ^ UIInput stuff ######
	
	public int getMinimumMinute() {
		String minimumTime = (String) getAttributes().get("minimumTime");
		if( minimumTime == null ) {
			return 0;
		} else {
			return Integer.valueOf( minimumTime.split( ":" )[ 1 ] ); 
		}
	}
	
	public int getMaximumMinute() {
		String maximumTime = (String) getAttributes().get("maximumTime");
		if( maximumTime == null ) {
			return 59;
		} else {
			return Integer.valueOf( maximumTime.split( ":" )[ 1 ] ); 
		}
	}
	
	public int getMinimumHour() {
		String minimumTime = (String) getAttributes().get("minimumTime");
		if( minimumTime == null ) {
			return 0;
		} else {
			return Integer.valueOf( minimumTime.split( ":" )[ 0 ] ); 
		}
	}
	
	public int getMaximumHour() {
		String maximumTime = (String) getAttributes().get("maximumTime");
		if( maximumTime == null ) {
			return 23;
		} else {
			return Integer.valueOf( maximumTime.split( ":" )[ 0 ] ); 
		}
	}

	public List<SelectItem> getMinuteSelectItems() {
		int maximumHour = getMaximumHour();
		int minimumHour = getMinimumHour();
        Date beanDate = (Date) getValue();
		Calendar cal = Calendar.getInstance();
        if (beanDate == null) {
        	cal.setTime(new Date());
        } else {
        	cal.setTime(beanDate);
        }
        int valueHour = cal.get( Calendar.HOUR_OF_DAY );
        int valueMinute = cal.get( Calendar.MINUTE );
        
        int currentMinute = 0;
        int finalMinute = 59;
        if( valueHour == minimumHour ) {
        	int minimumMinute = getMinimumMinute(); 
        	if( valueMinute < minimumMinute && beanDate != null ) {
        		minimumMinute = valueMinute;
        	} else {
        		currentMinute = minimumMinute;
        	}
        } else if( valueHour == maximumHour ) {
        	int maximumMinute = getMaximumMinute(); 
        	if( valueMinute > maximumMinute && beanDate != null ) {
        		finalMinute = valueMinute;
        	} else {
        		finalMinute = maximumMinute;
        	}
        }
				
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		int minuteStep = Integer.parseInt((String) getAttributes().get("minuteStep"));
		for (; currentMinute <= finalMinute; currentMinute += minuteStep) {
			String label = String.valueOf(currentMinute);
			if (label.length() < 2) {
				label = "0" + label;
			}
			selectItems.add(new SelectItem(currentMinute, label));
		}
		return selectItems;
	}

	public SelectItem[] getHourSelectItems() {
		int maximumHour = getMaximumHour();
		int minimumHour = getMinimumHour();
		if (maximumHour < minimumHour) {
			Integer temp = maximumHour;
			maximumHour = minimumHour;
			minimumHour = temp;
		}
        Date beanDate = (Date) getValue();
		Calendar cal = Calendar.getInstance();
        if (beanDate == null) {
        	cal.setTime(new Date());
        } else {
        	cal.setTime(beanDate);
        }
        int valueHour = cal.get( Calendar.HOUR_OF_DAY );
        
        if( beanDate != null ) {
	        if( valueHour < minimumHour ) {
	        	minimumHour = valueHour;
	        } else if( valueHour > maximumHour ) {
	        	maximumHour = valueHour;
	        }
        }
		SelectItem[] selectItems = new SelectItem[maximumHour - minimumHour + 1];
		for (int index = 0; index < (maximumHour - minimumHour + 1); index++) {
			String label = String.valueOf(minimumHour + index);
			if (label.length() < 2) {
				label = "0" + label;
			}
			selectItems[index] = new SelectItem(minimumHour + index, label);
		}
		return selectItems;
	}

	public SelectItem[] getDaySelectItems() {
		Calendar cal = Calendar.getInstance();
		UIInput monthComponent = (UIInput) findComponent("monthDd");
        UIInput yearComponent = (UIInput) findComponent("yearDd");
        /*
         * This is a hack to fix an issue that occurs when in a ui:repeat and the values set in the
         * encode begin are lost.
         */
        if( monthComponent.getValue()==null && monthComponent.getSubmittedValue() == null ) {
        	setDdlsFromValue();
        }
        //when clicking save it uses the submitted value whereas it usually just uses value
        int month = (Integer) ((monthComponent.getValue()==null)?Integer.parseInt((String)monthComponent.getSubmittedValue()):monthComponent.getValue());
        int year = (Integer) ((yearComponent.getValue()==null)?Integer.parseInt((String)yearComponent.getSubmittedValue()):yearComponent.getValue());
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		int daysThisMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		SelectItem[] selectItems = new SelectItem[daysThisMonth];
		for (int i=1; i <= daysThisMonth; i++) {
			selectItems[i-1] = new SelectItem(i, String.valueOf(i));
		}
		return selectItems;
	}
	
	@Override
	public void processUpdates(FacesContext context) {
		// TODO Auto-generated method stub
		super.processUpdates(context);
	}

	public SelectItem[] getMonthSelectItems() {
		SelectItem[] selectItems = new SelectItem[12];
		selectItems[0] = new SelectItem(0, "Jan");
		selectItems[1] = new SelectItem(1, "Feb");
		selectItems[2] = new SelectItem(2, "Mar");
		selectItems[3] = new SelectItem(3, "Apr");
		selectItems[4] = new SelectItem(4, "May");
		selectItems[5] = new SelectItem(5, "Jun");
		selectItems[6] = new SelectItem(6, "Jul");
		selectItems[7] = new SelectItem(7, "Aug");
		selectItems[8] = new SelectItem(8, "Sep");
		selectItems[9] = new SelectItem(9, "Oct");
		selectItems[10] = new SelectItem(10, "Nov");
		selectItems[11] = new SelectItem(11, "Dec");
		return selectItems;
	}

	public List<SelectItem> getYearSelectItems() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		Calendar cal = Calendar.getInstance();
		Integer currentYear = cal.get(Calendar.YEAR);
		Integer startYearDiff = ComponentUtil.determineIntegerAttributeValue( this, START_YEAR_DIFF_ATTRIBUTE_KEY, 0);
		Integer endYearDiff = ComponentUtil.determineIntegerAttributeValue( this, END_YEAR_DIFF_ATTRIBUTE_KEY, 0);
		Integer startYear = currentYear + startYearDiff;
		Integer endYear = currentYear + endYearDiff;
		Date valueDate = (Date) getAttributes().get( "value" );
		Integer valueYear = null;
		if( valueDate != null ) {
			cal.setTime( valueDate );
			valueYear = cal.get( Calendar.YEAR );
		}
		if (endYear < startYear) {
			
			if (valueYear != null && valueYear > startYear) {
				selectItems.add(new SelectItem(valueYear, String.valueOf(valueYear)));
			} 
			
//			if ( valueYear != null ) {
//				if ( valueYear > startYear ) {
//					startYear = valueYear;
//				} else if ( valueYear < endYear ) {
//					endYear = valueYear;
//				}
//			}
			
			if (endYear.intValue() == startYear.intValue()) {
				selectItems.add(new SelectItem(endYear, String.valueOf(endYear)));
			} else {
				for (currentYear = startYear; currentYear >= endYear; currentYear--) {
					selectItems.add(new SelectItem(currentYear, String.valueOf(currentYear)));
				}
			}
			
			if (valueYear != null && valueYear < endYear) {
				selectItems.add(new SelectItem(valueYear, String.valueOf(valueYear)));
			} 
			
		} else {
			
			if (valueYear != null && valueYear > endYear) {
				selectItems.add(new SelectItem(valueYear, String.valueOf(valueYear)));
			} 
			
//			if ( valueYear != null ) {
//				if ( valueYear < startYear ) {
//					startYear = valueYear;
//				} else if ( valueYear > endYear ) {
//					endYear = valueYear;
//				}
//			}
			
			if (endYear.intValue() == startYear.intValue()) {
				selectItems.add(new SelectItem(endYear, String.valueOf(endYear)));
			} else {
				for (currentYear = startYear; currentYear <= endYear; currentYear++) {
					selectItems.add(new SelectItem(currentYear, String.valueOf(currentYear)));
				}
			}
			
			if (valueYear != null && valueYear < startYear) {
				selectItems.add(new SelectItem(valueYear, String.valueOf(valueYear)));
			} 

		}
		return selectItems;
	}

	public boolean isAjaxOnchangeDisabled() {
		if( CommonUtil.isNullOrEmpty( (String) getAttributes().get( "ajaxOnchange" ) ) ) {
			return true;
		} else {
			return false;
		}
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getHour() {
		return hour;
	}

	public void setHour(Integer hour) {
		this.hour = hour;
	}

	public Integer getMinute() {
		return minute;
	}

	public void setMinute(Integer minute) {
		this.minute = minute;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public boolean isExternalDisabledBoolSupplied() {
		Object disabled = ComponentUtil.determineBooleanAttributeValue( this, "disabled", null );
		return disabled != null;
	}

	public boolean isDateChkBoxSelected() {
		Object disabled = ComponentUtil.determineBooleanAttributeValue( this, "disabled", null );
		if (disabled instanceof Boolean && (Boolean)disabled) {
			return false;
		}
		return dateChkBoxSelected;
	}

	public void setDateChkBoxSelected(boolean dateChkBoxSelected) {
		this.dateChkBoxSelected = dateChkBoxSelected;
	}

}
