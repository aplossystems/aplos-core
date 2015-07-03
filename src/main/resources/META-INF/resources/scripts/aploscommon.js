(function($) {
	
	addEvent( window, "load", attachRolloverButtonEvents, false );
	
	
	//########################################################################
	//Nease issue 3360 (aplos architecture)
	function applyStylingToActiveFiltersInDatatables() {
		//datatable column filters
		$('.ui-datatable th input').each(function() {
			$(this).keyup(function() {
				if ($(this).val() != '') {
					$(this).parents('th').addClass('aplos-filter-active');
				} else {
					$(this).parents('th').removeClass('aplos-filter-active');
				}
			});
			//apply each filter now / page load
			$(this).keyup();
		});
		
		$('.ui-datatable .ui-filter-column select').each(function() {
			$(this).change(function() {
				if ($(this).val() != '') {
					$(this).parents('th').addClass('aplos-filter-active');
				} else {
					$(this).parents('th').removeClass('aplos-filter-active');
				}
			});
			//apply each filter now / page load
			$(this).change();
		});
	
		//main datatable search
		$('.aplos-wdt-search-panel input').each(function() {
			//there should be no need to bind this to keyup (as this method is called each time by oncomplete)
			//$(this).keyup(function() {
				if ($(this).val() == '') {
						$(this).parents('.searchFieldRightDiv').removeClass('aplos-search-panel-active');
				} else {
						$(this).parents('.searchFieldRightDiv').addClass('aplos-search-panel-active');
				}
			//});
			//$(this).keyup();
		});
		
	}
	window.applyStylingToActiveFiltersInDatatables = applyStylingToActiveFiltersInDatatables;
	
	$(document).ready(function() {
		applyStylingToActiveFiltersInDatatables();
	});
	//########################################################################
	
	
	function addEvent(elm, evType, fn, useCapture) {
		if (elm.addEventListener) {
			elm.addEventListener(evType, fn, useCapture);
			return true;
		}
		else if (elm.attachEvent) {
			var r = elm.attachEvent('on' + evType, fn);
			return r;
		}
		else {
			elm['on' + evType] = fn;
		}
	}
	
	function captureEnter(event) {
		if (event.keyCode == 13) {
			event.preventDefault(); //most/all browsers
			event.stopPropagation(); //ff
			return false;
		} else {
			return true;
		}
	}
	window.captureEnter = captureEnter;
	
	function fireOnEnterPressed( evt, elementId ) {
		var charCode = (evt.which) ? evt.which : event.keyCode;
		if( charCode == 13 ) {
			document.getElementById( elementId ).click();
		}
	}
	window.fireOnEnterPressed = fireOnEnterPressed;
	
	function fireOnEnterPressedByClass( evt, styleClass ) {
		var charCode = (evt.which) ? evt.which : event.keyCode;
		if( charCode == 13 ) {
			$( "." + styleClass ).click();
		}
	}
	window.fireOnEnterPressedByClass = fireOnEnterPressedByClass;
	
	function fireOnclick(elementId) {
		if( document.createEventObject ) {
			document.getElementById( elementId ).click();
		}
	}
	window.fireOnclick = fireOnclick;
	
	function forceCapitals(element){
		element.value = element.value.toUpperCase();
	}
	window.forceCapitals = forceCapitals;
	
	function attachRolloverButtonEvents() {
		if(window.Prototype) {
			$$('.aplosRolloverButton').each(function(aplosRolloverBtn) {
				aplosRolloverBtn.observe('mouseover', function() {
					
					var btnRightDiv = aplosRolloverBtn;
					divElements = btnRightDiv.getElementsByTagName( "div" );
					var btnLeftDiv = divElements[ 0 ];
					var btnMiddleDiv = divElements[ 1 ];
					btnRightDiv.style.backgroundImage = btnRightDiv.style.backgroundImage.replace( "btnRight", "btnRight_over" );
					btnLeftDiv.style.backgroundImage = btnLeftDiv.style.backgroundImage.replace( "btnLeft", "btnLeft_over" );
					btnMiddleDiv.style.backgroundImage = btnMiddleDiv.style.backgroundImage.replace( "btnMiddle", "btnMiddle_over" );
					document.body.style.cursor = 'pointer';
				} );   
			});
			$$('.aplosRolloverButton').each(function(aplosRolloverBtn) {
				aplosRolloverBtn.observe('mouseout', function() {
					var btnRightDiv = aplosRolloverBtn;
					divElements = btnRightDiv.getElementsByTagName( "div" );
					var btnLeftDiv = divElements[ 0 ];
					var btnMiddleDiv = divElements[ 1 ];
					btnRightDiv.style.backgroundImage = btnRightDiv.style.backgroundImage.replace( "btnRight_over", "btnRight" );
					btnLeftDiv.style.backgroundImage = btnLeftDiv.style.backgroundImage.replace( "btnLeft_over", "btnLeft" );
					btnMiddleDiv.style.backgroundImage = btnMiddleDiv.style.backgroundImage.replace( "btnMiddle_over", "btnMiddle" );
					document.body.style.cursor = 'default';
				} );   
			});
		}
	}
	window.attachRolloverButtonEvents = attachRolloverButtonEvents;
	
})(jQuery);

aplosJavascript = (function($) {
	var components = {
			ckEditorHelper:{
				url:'ckEditorHelper.js',
				loaded:false
			}
		};
	
	var registeredQuickviews = [];
	
	var showingQuickviews = [];
	
	var quickviewTimeout;
	
	return {
		addComponent:function(component){
			var c = components[component];
			if(c && c.loaded === false){
				var s = document.createElement('script');
				s.setAttribute('type', 'text/javascript');
				s.setAttribute('src',c.url);
				document.getElementsByTagName('head')[0].appendChild(s);
			}
		},
		componentAvailable:function(component){
			components[component].loaded = true;
			if(this.listener){
				this.listener(component);
			};
		},
		aplosClearTimeout:function(timeout) {
			clearTimeout(timeout);
		},
		showQuickview:function(quickview) {
			$('html').click();
			showingQuickviews.push( quickview );
			quickview.show();
		},
		setCookie:function(name,value,days) {
			var expires = "";
			if (days) {
				var date = new Date();
				date.setTime(date.getTime()+(days*24*60*60*1000));
				expires = "; expires="+date.toGMTString();
			}
			document.cookie = name+"="+value+expires+"; path=/";
		},
		registerQuickview:function(quickviewVar,quickviewObj,isButton) {
			if( registeredQuickviews.length == 0 ) {
				$('html').bind('click', function(event) {
					for (var i = 0; i < showingQuickviews.length; i++) {
					    showingQuickviews[i].hide();
					}
					showingQuickviews = [];
	    		});
	
	   			//dont dismiss within the dialog
	    		 $('.aplos-ui-dialog').click(function(event){
	    		     event.stopPropagation(); 
	    		 });
			}
			registeredQuickviews.push( quickviewVar );
			if( isButton ) {
				quickviewObj.click( function () {
	   			 	setTimeout( function() {aplosJavascript.showQuickview(quickviewVar)}, 300);
	   			});
			} else {
				quickviewObj.hover( function () {
	   				aplosJavascript.aplosClearTimeout(quickviewTimeout); 
					quickviewTimeout = setTimeout( function() {aplosJavascript.showQuickview(quickviewVar)}, 600);
	   			}, function () { 
	   				aplosJavascript.aplosClearTimeout(quickviewTimeout); 
	   			});
			}
		}
	}
})(jQuery);

var scannedStr = null;
var scannedPrefix = null;
var scannedSuffix = null;
var hookIdx;

function monitorKeyPress( evt ) {
	var charCode = (evt.which) ? evt.which : evt.keyCode;
	if( scannerHooks != null ) {
		for( var i = 0, n = scannerHooks.length; i < n; i++ ) {
			var scannerPrefix = scannerHooks[ i ][ 0 ];
			var scannerSuffix = scannerHooks[ i ][ 1 ];
			var isScannerPrefixUsingCtrl = scannerHooks[ i ][ 2 ];
			//  The null check needs to be separate from the inner statement so that the other
			//  if conditions aren't called when the scannedStr is null
			if( scannedStr == null ) {
				if( (evt.ctrlKey && isScannerPrefixUsingCtrl) || scannerPrefix.indexOf( String.fromCharCode(charCode) ) != -1 ) {
					if( scannedPrefix == null ) {
						if( scannerPrefix.indexOf( String.fromCharCode(charCode) ) == 0 ) {
							scannedPrefix = String.fromCharCode(charCode);
						}
					} else if( scannerPrefix.substring(scannedPrefix.length).indexOf( String.fromCharCode(charCode) ) == 0 ) {
						scannedPrefix += String.fromCharCode(charCode);
					} else {
						scannedPrefix = null;
					}
					
					if( scannedPrefix == scannerPrefix ) {
						scannedStr = "";
						scannedPrefix = null;  
					}
				}
			} else if( scannerSuffix.indexOf( String.fromCharCode(charCode) ) != -1 ) {
				if( scannedSuffix == null ) {
					if( scannerSuffix.indexOf( String.fromCharCode(charCode) ) == 0 ) {
						scannedSuffix = String.fromCharCode(charCode);
					}
				} else if( scannerSuffix.substring(scannedSuffix.length).indexOf( String.fromCharCode(charCode) ) == 0 ) {
					scannedSuffix += String.fromCharCode(charCode);
				} else {
					scannedStr += scannedSuffix;
					scannedSuffix = null;
				}
				
				if( scannedSuffix == scannerSuffix ) {
					updateScanField( evt.target, scannedStr, scannerPrefix, scannerSuffix );
					registerScan( scannedStr );
					evt.preventDefault();
					scannedStr = null;
					scannedSuffix = null;
				}
			} else if( scannedStr != null ) {
				scannedStr += String.fromCharCode(charCode);
			}
		}
	}
}
function monitorFormKeyPress( evt ) {
	//firefox
	if ( typeof(evt.originalTarget) != "undefined" && evt.originalTarget.type != "textarea" ) {
		return evt.keyCode != 13;
	//chrome
	} else if ( typeof(evt.srcElement) != "undefined" && evt.srcElement.type != "textarea" ) {
		return evt.keyCode != 13;
	} else {
		return true;
	}
}

function updateScanField( scanField, scannedStr, prefix, suffix ) {
	var targetValue = evt.target.value;
	var targetScannedStr = prefix + scannedStr + suffix;
	if( targetValue != null ) {
		evt.target.value = targetValue.replace( targetScannedStr, scannedStr );
	}
}
/**
 * The onclick handler for ShufflerRenderer.
 *
 * @param formClientId  the clientId of the enclosing UIForm component
 * @param clientId      the clientId of the Shuffler component
 */
function _aplos_click_event( formClientId, clientId)
{
  var form = document.forms[formClientId];
  var input = form[clientId];
  if (!input) // if the input element does not already exist, create it and add it to the form
  {
    input = document.createElement("input");
    input.type = 'hidden';
    input.name = clientId;
    form.appendChild(input);
  }
  input.value = 'clicked';
  form.submit();
}