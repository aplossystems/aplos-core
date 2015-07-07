var showingQuickviews = [];
var quickviewTimeout;
	
$j(document).ready( function() {
	$j('html').bind('click', function(event) {
		for (var i = 0; i < showingQuickviews.length; i++) {
		    showingQuickviews[i].hide();
		}
		showingQuickviews = [];
	});
	
	$j('.aplos-quickview-hover-trigger').hover( function () {
		var extension = "";
		for( var i = 0; i < this.classList.length; i++ ) {
			if( this.classList[ i ].indexOf( "aplos-quickview-trigger_" ) != -1 ) {
				extension = this.classList[ i ].substring( 24, this.classList[ i ].length );
				break;
			}
		}
		renderQuickview(extension);
	});

	
	$j('.aplos-quickview-button-trigger').hover( function () {
		aplosJavascript.aplosClearTimeout(quickviewTimeout);

		var extension = "";
		for( var i = 0; i < this.classList.length; i++ ) {
			if( this.classList[ i ].indexOf( "aplos-quickview-trigger_" ) != -1 ) {
				extension = this.classList[ i ].substring( 24, this.classList[ i ].length );
				break;
			}
		}
		quickviewTimeout = setTimeout( function() {renderQuickview(extension)}, 600);
		
	}, function () { 
		aplosJavascript.aplosClearTimeout(quickviewTimeout); 
	});
});

function renderQuickview(extension) {
	
	if( extension.length > 0 && extension.length < 15 ) {
		eval( "showDialog_" + extension + "()" );
	}
}

function showQuickview(quickview) {
	$('html').click();
	 $j('.aplos-ui-dialog').click(function(event){
	     event.stopPropagation(); 
	 });
	showingQuickviews.push( quickview );
	quickview.show();
}


function registerQuickview(quickviewVar,quickviewObj,isButton) {
	if( isButton ) {
		quickviewObj.click( function () {
		});
	} else {
		quickviewObj.hover( function () {
			
		}, function () { 
			aplosJavascript.aplosClearTimeout(quickviewTimeout); 
		});
	}
}