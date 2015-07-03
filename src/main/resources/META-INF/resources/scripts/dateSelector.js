
var shortMonths = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];
    			
function DateSelector( daySelect, monthSelect, yearSelect, hidDate, startYear, finishYear, defaultDate ) {
	this.daySelect = daySelect;
	this.monthSelect = monthSelect;
	this.yearSelect = yearSelect;
	this.hidDate = hidDate;
	
	var dateOfBirthStr = hidDate.value;
	if( dateOfBirthStr != "" ) {
		var dateOfBirthBits = dateOfBirthStr.split( "/" );
		this.day = dateOfBirthBits[ 0 ];
		this.month = dateOfBirthBits[ 1 ] - 1;
		this.year = dateOfBirthBits[ 2 ];
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
	}

//	var options = new Array();
//	for( var i = 0, n = shortMonths.length; i < n; i++ ) {
//		var elOptNew = document.createElement('option');
//	  elOptNew.text = shortMonths[ i ];
//	  elOptNew.value = i;
//		try {
//	    this.monthSelect.add(elOptNew, null); // standards compliant; doesn't work in IE
//	  }
//	  catch(ex) {
//	    this.monthSelect.add(elOptNew); // IE only
//	  }
//	}
	
	
//	var options = new Array();
//	for( var i = finishYear, n = startYear - 1; i > n; i-- ) {
//		var elOptNew = document.createElement('option');
//	  elOptNew.text = i;
//	  elOptNew.value = i;
//		try {
//	    this.yearSelect.add(elOptNew, null); // standards compliant; doesn't work in IE
//	  }
//	  catch(ex) {
//	    this.yearSelect.add(elOptNew); // IE only
//	  }
//	}
	
	this.yearSelect.selectedIndex = (finishYear - this.year);
	this.monthSelect.selectedIndex = this.month;
	this.setDays();
  this.hidDate.value = this.day + "/" + (parseInt(this.month)+1) + "/" + this.year;
}

DateSelector.prototype.disable = function() {
	this.daySelect.disabled = "disabled";
	this.monthSelect.disabled = "disabled";
	this.yearSelect.disabled = "disabled";
}

DateSelector.prototype.enable = function() {
	this.daySelect.disabled = null;
	this.monthSelect.disabled = null;
	this.yearSelect.disabled = null;
}
	
DateSelector.prototype.setDays = function() {
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
	
DateSelector.prototype.dateChanged = function() {
	newYear = this.yearSelect.options[ this.yearSelect.selectedIndex ].value;
	newMonth = this.monthSelect.options[ this.monthSelect.selectedIndex ].value;
	newDay = this.daySelect.options[ this.daySelect.selectedIndex ].value;
	
	if( this.year != newYear || this.month != newMonth ) {
		this.month = newMonth;
		this.year = newYear;
		this.setDays();
	} else {
		this.day = newDay;
	}
	
	this.hidDate.value = this.day + "/" + (parseInt(this.month)+1) + "/" + this.year;
}