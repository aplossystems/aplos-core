
if (shortMonths==null) { //not internationalised
	var shortMonths = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];
}

function DateTimeSelector( minuteSelect, hourSelect, daySelect, monthSelect, yearSelect, hidDate, startYear, finishYear, minimumHour, maximumHour, minuteStep, defaultDate ) {
	this.minuteSelect = minuteSelect;
	this.hourSelect = hourSelect;
	this.daySelect = daySelect;
	this.monthSelect = monthSelect;
	this.yearSelect = yearSelect;
	this.hidDate = hidDate;
//	alert(minuteSelect);
	var dateTimeStr = hidDate.value;
	if( dateTimeStr != "" ) {
		var dateTimeBits = dateTimeStr.split( " " );
		var dateBits = dateTimeBits[0].split( "/" );
		var timeBits = dateTimeBits[1].split( ":" );
		this.day = dateBits[ 0 ];
		this.month = dateBits[ 1 ] - 1;
		this.year = dateBits[ 2 ];
		this.hour = timeBits[ 0 ];
		this.minute = timeBits[ 1 ];
	} else {
		if( defaultDate != null ) {
			this.day = defaultDate.getDate();
			this.month = defaultDate.getMonth();
			this.year = defaultDate.getFullYear();
		} else {
			this.day = 1;
			this.month = 0;
			this.year = startYear;
		}
		this.hour = minimumHour;
		this.minute = "00";
	}
	
	//console.log("Aplos:DateTime::Setup { ID: " + this.hidDate.id.substring(0, this.hidDate.id.length-4) + ", Start: " + startYear + ", Finish: " + finishYear + ", Yr: " + this.year + ", Mon: " + this.month + ", Day: " + this.day + ", Hr: " + this.hour + ", Min: " + this.minute + ", Hidden Value: " + this.hidDate.value + " }");
	
	if( this.daySelect != null ) {
		this.setDays();
	}

	if( this.monthSelect != null ) {
//		for( var i = 0, n = shortMonths.length; i < n; i++ ) {
//			var elOptNew = document.createElement('option');
//		  elOptNew.text = shortMonths[ i ];
//		  elOptNew.value = i;
//			try {
//		    this.monthSelect.add(elOptNew, null); // standards compliant; doesn't work in IE
//		  }
//		  catch(ex) {
//		    this.monthSelect.add(elOptNew); // IE only
//		  }
//		}
		this.monthSelect.selectedIndex = this.month;
	}
//
	if( this.yearSelect != null ) {
//		for ( var i = finishYear; i > startYear - 1; i-- ) {
//			var elOptNew = document.createElement('option');
//		  elOptNew.text = i;
//		  elOptNew.value = i;
//			try {
//		    this.yearSelect.add(elOptNew, null); // standards compliant; doesn't work in IE
//		  }
//		  catch(ex) {
//		    this.yearSelect.add(elOptNew); // IE only
//		  }
//		}
		if (finishYear >= startYear) {
			this.yearSelect.selectedIndex = (finishYear - this.year);
		} else { //elements are in reverse order 
			if (this.year) {
				this.yearSelect.selectedIndex = (this.year - finishYear);
			} else {
				this.yearSelect.selectedIndex = 0;
			}
		}
//		if (this.yearSelect.options[ this.yearSelect.selectedIndex ]) {
//			console.log("Aplos:DateTime::Setup { ID: " + this.hidDate.id.substring(0, this.hidDate.id.length-4) + ", Selected year: " + this.yearSelect.options[ this.yearSelect.selectedIndex ].value + ", Index: " + this.yearSelect.selectedIndex  + " }");
//		} else {
//			console.log("Aplos:DateTime::Setup { ID: " + this.hidDate.id.substring(0, this.hidDate.id.length-4) + ", Selected year: invalid, Index: " + this.yearSelect.selectedIndex  + " }");
//		}
	}
	
	
	if( this.hourSelect != null ) {
//		for( var i = minimumHour; i <= maximumHour; i++) {
//			var elOptNew = document.createElement('option');
//				elOptNew.text = i;
//				if (i < 10) {
//					elOptNew.text = "0" + elOptNew.text;
//				}
//				elOptNew.value = i;
//			try {
//				this.hourSelect.add(elOptNew, null); // standards compliant; doesn't work in IE
//			} catch(ex) {
//				this.hourSelect.add(elOptNew); // IE only
//			}
//		}
		this.hourSelect.selectedIndex = this.hour;
	}
	
	if( this.minuteSelect != null ) {
//		for( var i = 0; i < 60; i += minuteStep) {
//			//alert(i + ', ' + minuteStep);
//			var elOptNew = document.createElement('option');
//				elOptNew.text = i;
//				if (i < 10) {
//					elOptNew.text = "0" + elOptNew.text;
//				}
//				elOptNew.value = i;
//			try {
//				this.minuteSelect.add(elOptNew, null); // standards compliant; doesn't work in IE
//			} catch(ex) {
//				this.minuteSelect.add(elOptNew); // IE only
//			}
//		}
		this.minuteSelect.selectedIndex = this.minute / minuteStep;
	}
	
	this.hidDate.value = this.day + "/" + (parseInt(this.month)+1) + "/" + this.year + " " + this.hour + ":" + this.minute;
}

DateTimeSelector.prototype.disable = function() {
	this.daySelect.disabled = "disabled";
	this.monthSelect.disabled = "disabled";
	this.yearSelect.disabled = "disabled";
	if (this.hourSelect != null) {
		this.hourSelect.disabled = "disabled";
		this.minuteSelect.disabled = "disabled";
	}
}

DateTimeSelector.prototype.enable = function() {
	this.daySelect.disabled = null;
	this.monthSelect.disabled = null;
	this.yearSelect.disabled = null;
	if (this.hourSelect != null) {
		this.hourSelect.disabled = null;
		this.minuteSelect.disabled = null;
	}
}
	
DateTimeSelector.prototype.setDays = function() {
	var currentOptions = this.daySelect.options;
	for( var i = 0, n = currentOptions.length; i < n; i++ ) {
		this.daySelect.remove( currentOptions );
	}
	
	var tmpDate = new Date( this.year, this.month, 1 );
	var finaDay = 0;
	
	for ( i = 1; i < 32; i++ ) {
		tmpDate.setDate( i );
		if ( tmpDate.getMonth() == this.month ) {
			var elOptNew = document.createElement('option');
		  elOptNew.text = i;
		  elOptNew.value = i;
		  try {
	       this.daySelect.add(elOptNew, null); // standards compliant; doesn't work in IE
	     }
	     catch(ex) {
	       this.daySelect.add(elOptNew); // IE only
	     }
		  finalDay = i;
		}	
	}

	if( this.day <= finalDay ) {
		this.daySelect.selectedIndex = this.day - 1;
	} 
}
	
DateTimeSelector.prototype.dateTimeChanged = function() {
	if( this.yearSelect != null ) {
		newYear = this.yearSelect.options[ this.yearSelect.selectedIndex ].value;
	}
	if( this.monthSelect != null ) {
		newMonth = this.monthSelect.options[ this.monthSelect.selectedIndex ].value;
	}
	if( this.daySelect != null ) {
		newDay = this.daySelect.options[ this.daySelect.selectedIndex ].value;
	}
	if( this.hourSelect != null ) {
		this.hour = this.hourSelect.options[ this.hourSelect.selectedIndex ].value;
	}
	if( this.minuteSelect != null ) {
		this.minute = this.minuteSelect.options[ this.minuteSelect.selectedIndex ].value;
	}
		
	if( this.year != newYear || this.month != newMonth ) {
		this.month = newMonth;
		this.year = newYear;
		if( this.daySelect != null ) {
			this.setDays();
		}
	} else {
		this.day = newDay;
	}
	
	this.hidDate.value = this.day + "/" + (parseInt(this.month)+1) + "/" + this.year + " " + this.hour + ":" + this.minute;
	
//	console.log("Aplos:DateTime::Changed { ID: " + this.hidDate.id.substring(0, this.hidDate.id.length-4) + ", Year:" + this.year + ", Month:" + this.month + ", Day:" + this.day + ", Hour:" + this.hour + ", Minute:" + this.minute + ", Hidden Value: " + this.hidDate.value + " }");
	
}
